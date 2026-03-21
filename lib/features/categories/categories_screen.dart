import 'dart:async';

import 'package:flutter/cupertino.dart';

import '../../app/app_state.dart';
import '../../domain/entities/models.dart';

class CategoriesScreen extends StatefulWidget {
  const CategoriesScreen({super.key, required this.state});

  final AppState state;

  @override
  State<CategoriesScreen> createState() => _CategoriesScreenState();
}

class _CategoriesScreenState extends State<CategoriesScreen> {
  late Future<List<Category>> _future;
  StreamSubscription<void>? _sub;
  final _controller = TextEditingController();
  String? _error;

  @override
  void initState() {
    super.initState();
    _future = widget.state.loadCategories();
    _sub = widget.state.updates.listen((_) {
      if (mounted) {
        setState(() {
          _future = widget.state.loadCategories();
        });
      }
    });
  }

  @override
  void dispose() {
    _sub?.cancel();
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Categories')),
      child: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(12),
              child: Row(
                children: [
                  Expanded(
                    child: CupertinoTextField(
                      controller: _controller,
                      placeholder: 'Create category (e.g. English mock)',
                    ),
                  ),
                  const SizedBox(width: 8),
                  CupertinoButton.filled(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    onPressed: _addCategory,
                    child: const Text('Add'),
                  ),
                ],
              ),
            ),
            if (_error != null)
              Padding(
                padding: const EdgeInsets.only(left: 12, right: 12, bottom: 8),
                child: Text(
                  _error!,
                  style: const TextStyle(color: CupertinoColors.systemRed),
                ),
              ),
            Expanded(
              child: FutureBuilder<List<Category>>(
                future: _future,
                builder: (context, snapshot) {
                  if (snapshot.connectionState != ConnectionState.done) {
                    return const Center(child: CupertinoActivityIndicator());
                  }
                  if (snapshot.hasError) {
                    return Center(
                      child: Text(
                        'Failed to load categories: ${snapshot.error}',
                      ),
                    );
                  }

                  final categories = snapshot.data ?? <Category>[];
                  if (categories.isEmpty) {
                    return const Center(
                      child: Text('No categories created yet.'),
                    );
                  }

                  return ListView.separated(
                    padding: const EdgeInsets.fromLTRB(12, 8, 12, 24),
                    itemBuilder: (context, index) {
                      final category = categories[index];
                      return Container(
                        decoration: BoxDecoration(
                          color: CupertinoColors.secondarySystemBackground
                              .resolveFrom(context),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        padding: const EdgeInsets.all(12),
                        child: Row(
                          children: [
                            Expanded(
                              child: Text(
                                category.name,
                                style: const TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                            CupertinoButton(
                              padding: EdgeInsets.zero,
                              minimumSize: const Size(20, 20),
                              onPressed: () => _deleteCategory(category.id),
                              child: const Icon(CupertinoIcons.delete_simple),
                            ),
                          ],
                        ),
                      );
                    },
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemCount: categories.length,
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _addCategory() async {
    final value = _controller.text.trim();
    if (value.isEmpty) {
      setState(() {
        _error = 'Category name is required';
      });
      return;
    }

    try {
      await widget.state.createCategory(value);
      _controller.clear();
      if (mounted) {
        setState(() {
          _error = null;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e
              .toString()
              .replaceFirst('StateError: ', '')
              .replaceFirst('DatabaseException(', '')
              .replaceAll(')', '');
        });
      }
    }
  }

  Future<void> _deleteCategory(int id) async {
    try {
      await widget.state.deleteCategory(id);
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString().replaceFirst('StateError: ', '');
        });
      }
    }
  }
}
