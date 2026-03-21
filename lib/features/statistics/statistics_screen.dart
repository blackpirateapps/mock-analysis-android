import 'dart:async';

import 'package:flutter/cupertino.dart';

import '../../app/app_state.dart';
import '../../core/formatters.dart';
import '../../domain/entities/models.dart';

class StatisticsScreen extends StatefulWidget {
  const StatisticsScreen({super.key, required this.state});

  final AppState state;

  @override
  State<StatisticsScreen> createState() => _StatisticsScreenState();
}

class _StatisticsScreenState extends State<StatisticsScreen> {
  late Future<_StatsData> _future;
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

  Future<_StatsData> _load() async {
    final overall = await widget.state.loadOverallStats();
    final byCategory = await widget.state.loadCategoryStats();
    return _StatsData(overall: overall, byCategory: byCategory);
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Statistics')),
      child: SafeArea(
        child: FutureBuilder<_StatsData>(
          future: _future,
          builder: (context, snapshot) {
            if (snapshot.connectionState != ConnectionState.done) {
              return const Center(child: CupertinoActivityIndicator());
            }

            if (snapshot.hasError) {
              return Center(
                child: Text('Failed to load stats: ${snapshot.error}'),
              );
            }

            final data =
                snapshot.data ??
                const _StatsData(
                  overall: OverallStats(
                    mockCount: 0,
                    totalQuestions: 0,
                    totalRight: 0,
                    totalWrong: 0,
                  ),
                  byCategory: <CategoryStats>[],
                );

            return ListView(
              padding: const EdgeInsets.fromLTRB(12, 12, 12, 24),
              children: [
                _Card(
                  title: 'Overall',
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Mocks: ${data.overall.mockCount}'),
                      Text('Questions: ${data.overall.totalQuestions}'),
                      Text('Right: ${data.overall.totalRight}'),
                      Text('Wrong: ${data.overall.totalWrong}'),
                      Text('Unanswered: ${data.overall.unanswered}'),
                      Text(
                        'Accuracy: ${formatPercentage(data.overall.accuracy)}',
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
                _Card(
                  title: 'Category Wise',
                  child: data.byCategory.isEmpty
                      ? const Text('No category stats yet.')
                      : Column(
                          children: data.byCategory
                              .map(
                                (stat) => Padding(
                                  padding: const EdgeInsets.only(bottom: 10),
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Expanded(
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                            Text(
                                              stat.category.name,
                                              style: const TextStyle(
                                                fontWeight: FontWeight.w600,
                                              ),
                                            ),
                                            Text('Mocks: ${stat.mockCount}'),
                                            Text(
                                              'Questions: ${stat.totalQuestions}',
                                            ),
                                            Text(
                                              'R ${stat.totalRight} • W ${stat.totalWrong} • U ${stat.unanswered}',
                                            ),
                                          ],
                                        ),
                                      ),
                                      Text(formatPercentage(stat.accuracy)),
                                    ],
                                  ),
                                ),
                              )
                              .toList(growable: false),
                        ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}

class _StatsData {
  const _StatsData({required this.overall, required this.byCategory});

  final OverallStats overall;
  final List<CategoryStats> byCategory;
}

class _Card extends StatelessWidget {
  const _Card({required this.title, required this.child});

  final String title;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: CupertinoColors.secondarySystemBackground.resolveFrom(context),
        borderRadius: BorderRadius.circular(12),
      ),
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 8),
          child,
        ],
      ),
    );
  }
}
