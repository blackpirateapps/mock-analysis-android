import 'dart:async';

import 'package:flutter/cupertino.dart';

import '../../app/app_state.dart';
import '../../core/formatters.dart';
import '../../domain/entities/models.dart';

class EntriesScreen extends StatefulWidget {
  const EntriesScreen({super.key, required this.state});

  final AppState state;

  @override
  State<EntriesScreen> createState() => _EntriesScreenState();
}

class _EntriesScreenState extends State<EntriesScreen> {
  late Future<_EntryViewData> _future;
  StreamSubscription<void>? _sub;

  @override
  void initState() {
    super.initState();
    _future = _load();
    _sub = widget.state.updates.listen((_) {
      if (mounted) {
        setState(() {
          _future = _load();
        });
      }
    });
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  Future<_EntryViewData> _load() async {
    final entries = await widget.state.loadEntries();
    final categories = await widget.state.loadCategories();
    return _EntryViewData(entries: entries, categories: categories);
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: const Text('Mock Entries'),
        trailing: CupertinoButton(
          padding: EdgeInsets.zero,
          onPressed: _showAddEntry,
          child: const Icon(CupertinoIcons.add),
        ),
      ),
      child: SafeArea(
        child: FutureBuilder<_EntryViewData>(
          future: _future,
          builder: (context, snapshot) {
            if (snapshot.connectionState != ConnectionState.done) {
              return const Center(child: CupertinoActivityIndicator());
            }

            if (snapshot.hasError) {
              return Center(
                child: Text('Failed to load entries: ${snapshot.error}'),
              );
            }

            final data =
                snapshot.data ??
                const _EntryViewData(
                  entries: <MockEntry>[],
                  categories: <Category>[],
                );

            if (data.categories.isEmpty) {
              return _EmptyState(
                title: 'No categories yet',
                message: 'Create categories first, then add mock entries.',
                buttonLabel: 'Add in Categories tab',
                onPressed: () {},
              );
            }

            if (data.entries.isEmpty) {
              return _EmptyState(
                title: 'No mocks logged',
                message: 'Tap + to add your first mock analysis entry.',
                buttonLabel: 'Add Entry',
                onPressed: _showAddEntry,
              );
            }

            return ListView.separated(
              padding: const EdgeInsets.fromLTRB(12, 12, 12, 24),
              itemBuilder: (context, index) {
                final entry = data.entries[index];
                return Container(
                  decoration: BoxDecoration(
                    color: CupertinoColors.secondarySystemBackground
                        .resolveFrom(context),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.all(12),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              entry.mockName,
                              style: const TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                            const SizedBox(height: 6),
                            Text(
                              '${entry.totalQuestions} Q • R ${entry.rightAnswers} • W ${entry.wrongAnswers} • ${formatPercentage(entry.accuracy)}\n${entry.categories.map((e) => e.name).join(', ')}\n${formatDateTime(entry.createdAt)}',
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 8),
                      CupertinoButton(
                        padding: EdgeInsets.zero,
                        minSize: 20,
                        onPressed: () => _deleteEntry(entry.id),
                        child: const Icon(CupertinoIcons.delete_simple),
                      ),
                    ],
                  ),
                );
              },
              separatorBuilder: (_, __) => const SizedBox(height: 8),
              itemCount: data.entries.length,
            );
          },
        ),
      ),
    );
  }

  Future<void> _deleteEntry(int id) async {
    await widget.state.deleteEntry(id);
  }

  Future<void> _showAddEntry() async {
    final categories = await widget.state.loadCategories();
    if (!mounted) return;
    await showCupertinoModalPopup<void>(
      context: context,
      builder: (context) =>
          _AddEntrySheet(state: widget.state, categories: categories),
    );
  }
}

class _EntryViewData {
  const _EntryViewData({required this.entries, required this.categories});

  final List<MockEntry> entries;
  final List<Category> categories;
}

class _AddEntrySheet extends StatefulWidget {
  const _AddEntrySheet({required this.state, required this.categories});

  final AppState state;
  final List<Category> categories;

  @override
  State<_AddEntrySheet> createState() => _AddEntrySheetState();
}

class _AddEntrySheetState extends State<_AddEntrySheet> {
  final _mockNameCtrl = TextEditingController();
  final _totalCtrl = TextEditingController();
  final _rightCtrl = TextEditingController();
  final _wrongCtrl = TextEditingController();
  final _selected = <int>{};

  String? _error;
  bool _saving = false;

  @override
  void dispose() {
    _mockNameCtrl.dispose();
    _totalCtrl.dispose();
    _rightCtrl.dispose();
    _wrongCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPopupSurface(
      child: SafeArea(
        top: false,
        child: SizedBox(
          height: 560,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    const Expanded(
                      child: Text(
                        'Add Mock Entry',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    CupertinoButton(
                      padding: EdgeInsets.zero,
                      onPressed: _saving
                          ? null
                          : () => Navigator.of(context).pop(),
                      child: const Text('Close'),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                CupertinoTextField(
                  controller: _mockNameCtrl,
                  placeholder: 'Mock name',
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: CupertinoTextField(
                        controller: _totalCtrl,
                        keyboardType: TextInputType.number,
                        placeholder: 'Total',
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: CupertinoTextField(
                        controller: _rightCtrl,
                        keyboardType: TextInputType.number,
                        placeholder: 'Right',
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: CupertinoTextField(
                        controller: _wrongCtrl,
                        keyboardType: TextInputType.number,
                        placeholder: 'Wrong',
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                const Text(
                  'Select categories',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 6),
                Expanded(
                  child: ListView.builder(
                    itemCount: widget.categories.length,
                    itemBuilder: (context, index) {
                      final category = widget.categories[index];
                      final checked = _selected.contains(category.id);
                      return CupertinoButton(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 0,
                          vertical: 6,
                        ),
                        onPressed: _saving
                            ? null
                            : () {
                                setState(() {
                                  if (checked) {
                                    _selected.remove(category.id);
                                  } else {
                                    _selected.add(category.id);
                                  }
                                });
                              },
                        child: Row(
                          children: [
                            Icon(
                              checked
                                  ? CupertinoIcons.check_mark_circled_solid
                                  : CupertinoIcons.circle,
                            ),
                            const SizedBox(width: 8),
                            Expanded(child: Text(category.name)),
                          ],
                        ),
                      );
                    },
                  ),
                ),
                if (_error != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Text(
                      _error!,
                      style: const TextStyle(color: CupertinoColors.systemRed),
                    ),
                  ),
                SizedBox(
                  width: double.infinity,
                  child: CupertinoButton.filled(
                    onPressed: _saving ? null : _save,
                    child: _saving
                        ? const CupertinoActivityIndicator()
                        : const Text('Save Entry'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _save() async {
    setState(() {
      _error = null;
      _saving = true;
    });

    try {
      await widget.state.createEntry(
        mockName: _mockNameCtrl.text,
        totalQuestions: int.tryParse(_totalCtrl.text) ?? -1,
        rightAnswers: int.tryParse(_rightCtrl.text) ?? -1,
        wrongAnswers: int.tryParse(_wrongCtrl.text) ?? -1,
        categoryIds: _selected.toList(),
      );
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _error = e.toString().replaceFirst('Invalid argument(s): ', '');
      });
    } finally {
      if (mounted) {
        setState(() {
          _saving = false;
        });
      }
    }
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState({
    required this.title,
    required this.message,
    required this.buttonLabel,
    required this.onPressed,
  });

  final String title;
  final String message;
  final String buttonLabel;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              title,
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 8),
            Text(message, textAlign: TextAlign.center),
            const SizedBox(height: 12),
            CupertinoButton.filled(
              onPressed: onPressed,
              child: Text(buttonLabel),
            ),
          ],
        ),
      ),
    );
  }
}
