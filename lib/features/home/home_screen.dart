import 'package:flutter/cupertino.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../app/providers.dart';
import '../../core/formatters.dart';
import '../../domain/entities/models.dart';
import '../../domain/services/analytics_service.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<List<TestEntry>> testsAsync = ref.watch(testsProvider);
    final AsyncValue<MarkingScheme> schemeAsync = ref.watch(
      markingSchemeProvider,
    );
    final AnalyticsService analytics = ref.watch(analyticsServiceProvider);

    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Home')),
      child: SafeArea(
        child: testsAsync.when(
          data: (List<TestEntry> tests) {
            if (tests.isEmpty) {
              return const Center(
                child: Text(
                  'No data yet. Log your first mock.',
                  style: TextStyle(color: Color(0xFFB8B8BE), fontSize: 16),
                ),
              );
            }

            return schemeAsync.when(
              data: (MarkingScheme scheme) {
                final Map<String, List<TestEntry>> byFolder =
                    <String, List<TestEntry>>{};
                for (final TestEntry test in tests) {
                  for (final SubjectEntry subject in test.subjects) {
                    byFolder
                        .putIfAbsent(subject.name, () => <TestEntry>[])
                        .add(test);
                  }
                }

                final List<String> folders = byFolder.keys.toList()..sort();

                return ListView(
                  padding: const EdgeInsets.all(16),
                  children: <Widget>[
                    const Text(
                      'Subject Folders',
                      style: TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontSize: 24,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 10),
                    ...folders.map((String folder) {
                      final List<TestEntry> folderTests =
                          byFolder[folder] ?? <TestEntry>[];
                      double avgPercentile = 0;
                      double avgAccuracy = 0;
                      int count = 0;
                      for (final TestEntry test in folderTests) {
                        SubjectEntry? match;
                        for (final SubjectEntry subject in test.subjects) {
                          if (subject.name == folder) {
                            match = subject;
                            break;
                          }
                        }
                        avgPercentile += test.percentile;
                        if (match != null) {
                          final SubjectComputed computed = analytics
                              .computeSubject(match, scheme);
                          avgAccuracy += computed.accuracy;
                        }
                        count += 1;
                      }
                      if (count > 0) {
                        avgPercentile = avgPercentile / count;
                        avgAccuracy = avgAccuracy / count;
                      }

                      return Container(
                        margin: const EdgeInsets.only(bottom: 10),
                        padding: const EdgeInsets.all(14),
                        decoration: BoxDecoration(
                          color: const Color(0xFF1C1C1E),
                          borderRadius: BorderRadius.circular(18),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: <Widget>[
                            Text(
                              folder,
                              style: const TextStyle(
                                color: Color(0xFFFFFFFF),
                                fontSize: 18,
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                            const SizedBox(height: 6),
                            Text(
                              'Mocks: $count  Avg Percentile: ${avgPercentile.toStringAsFixed(2)}',
                              style: const TextStyle(
                                color: Color(0xFFD0D0D4),
                                fontSize: 14,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Avg Accuracy: ${percentText(avgAccuracy)}',
                              style: const TextStyle(
                                color: Color(0xFFB8B8BE),
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                      );
                    }),
                  ],
                );
              },
              loading: () => const Center(child: CupertinoActivityIndicator()),
              error: (Object error, StackTrace stackTrace) => Center(
                child: Text(
                  error.toString(),
                  style: const TextStyle(color: Color(0xFFFF453A)),
                ),
              ),
            );
          },
          loading: () => const Center(child: CupertinoActivityIndicator()),
          error: (Object error, StackTrace stackTrace) => Center(
            child: Text(
              error.toString(),
              style: const TextStyle(color: Color(0xFFFF453A)),
            ),
          ),
        ),
      ),
    );
  }
}
