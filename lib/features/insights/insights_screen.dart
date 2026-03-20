import 'dart:math' as math;

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart'
    show AlwaysStoppedAnimation, CircularProgressIndicator;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../app/providers.dart';
import '../../core/formatters.dart';
import '../../domain/entities/models.dart';
import '../../domain/services/analytics_service.dart';

class InsightsScreen extends ConsumerWidget {
  const InsightsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<List<TestEntry>> testsAsync = ref.watch(testsProvider);
    final AsyncValue<MarkingScheme> schemeAsync = ref.watch(
      markingSchemeProvider,
    );
    final AnalyticsService analytics = ref.watch(analyticsServiceProvider);

    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Insights')),
      child: SafeArea(
        child: testsAsync.when(
          data: (List<TestEntry> tests) {
            if (tests.isEmpty) {
              return const Center(
                child: Text(
                  'No mock data yet.\nAdd your first test in Log.',
                  textAlign: TextAlign.center,
                  style: TextStyle(color: Color(0xFFB8B8BE), fontSize: 16),
                ),
              );
            }
            return schemeAsync.when(
              data: (MarkingScheme scheme) {
                final TestEntry latest = tests.first;
                final TestComputed latestComputed = analytics.computeTest(
                  latest,
                  scheme,
                );
                final List<FlSpot> percentileSpots = tests.reversed
                    .toList()
                    .asMap()
                    .entries
                    .map(
                      (MapEntry<int, TestEntry> entry) =>
                          FlSpot(entry.key.toDouble(), entry.value.percentile),
                    )
                    .toList();
                final List<WeakSubjectStat> weakStats = analytics.weakSubjects(
                  tests,
                );

                return ListView(
                  padding: const EdgeInsets.all(16),
                  children: <Widget>[
                    _hero(latest, latestComputed),
                    const SizedBox(height: 12),
                    _segmentedRatio(latestComputed),
                    const SizedBox(height: 16),
                    const Text(
                      'Subjects',
                      style: TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontSize: 20,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 10),
                    ...latestComputed.subjects.map(
                      (SubjectComputed item) => _subjectRow(item),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'Percentile Trend',
                      style: TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontSize: 20,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 12),
                    SizedBox(height: 190, child: _lineChart(percentileSpots)),
                    const SizedBox(height: 16),
                    const Text(
                      'Historically Weak Subjects',
                      style: TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontSize: 20,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 8),
                    ...weakStats
                        .take(6)
                        .map(
                          (WeakSubjectStat item) => Container(
                            margin: const EdgeInsets.only(bottom: 8),
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: const Color(0xFF1C1C1E),
                              borderRadius: BorderRadius.circular(16),
                            ),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: <Widget>[
                                Expanded(
                                  child: Text(
                                    item.subjectName,
                                    style: const TextStyle(
                                      color: Color(0xFFFFFFFF),
                                    ),
                                  ),
                                ),
                                Text(
                                  percentText(item.averageAccuracy),
                                  style: const TextStyle(
                                    color: Color(0xFFFF453A),
                                    fontWeight: FontWeight.w700,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                    const SizedBox(height: 20),
                  ],
                );
              },
              loading: () => const Center(child: CupertinoActivityIndicator()),
              error: (Object error, StackTrace stackTrace) => Center(
                child: Text(
                  error.toString(),
                  style: const TextStyle(
                    color: Color(0xFFFF453A),
                    fontSize: 14,
                  ),
                ),
              ),
            );
          },
          loading: () => const Center(child: CupertinoActivityIndicator()),
          error: (Object error, StackTrace stackTrace) => Center(
            child: Text(
              error.toString(),
              style: const TextStyle(color: Color(0xFFFF453A), fontSize: 14),
            ),
          ),
        ),
      ),
    );
  }

  Widget _hero(TestEntry test, TestComputed computed) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            fixedScore(computed.score),
            style: const TextStyle(
              color: Color(0xFFFFFFFF),
              fontSize: 46,
              fontWeight: FontWeight.w800,
            ),
          ),
          const Text(
            'Overall Score',
            style: TextStyle(color: Color(0xFF8E8E93)),
          ),
          const SizedBox(height: 10),
          Row(
            children: <Widget>[
              Expanded(child: _miniMetric('Rank', test.rank.toString())),
              Expanded(
                child: _miniMetric(
                  'Percentile',
                  test.percentile.toStringAsFixed(1),
                ),
              ),
              SizedBox(
                width: 88,
                height: 88,
                child: Stack(
                  fit: StackFit.expand,
                  children: <Widget>[
                    CircularProgressIndicator(
                      value: computed.accuracy,
                      backgroundColor: const Color(0xFF2C2C2E),
                      valueColor: const AlwaysStoppedAnimation<Color>(
                        Color(0xFF34C759),
                      ),
                      strokeWidth: 8,
                    ),
                    Center(
                      child: Text(
                        percentText(computed.accuracy),
                        style: const TextStyle(
                          color: Color(0xFFFFFFFF),
                          fontWeight: FontWeight.w700,
                          fontSize: 11,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _miniMetric(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Text(
          value,
          style: const TextStyle(
            color: Color(0xFFFFFFFF),
            fontWeight: FontWeight.w700,
            fontSize: 20,
          ),
        ),
        const SizedBox(height: 2),
        Text(label, style: const TextStyle(color: Color(0xFF8E8E93))),
      ],
    );
  }

  Widget _segmentedRatio(TestComputed computed) {
    final int total =
        computed.totalCorrect + computed.totalWrong + computed.totalSkipped;
    final double correct = total == 0 ? 0 : computed.totalCorrect / total;
    final double wrong = total == 0 ? 0 : computed.totalWrong / total;
    final double skipped = total == 0 ? 0 : computed.totalSkipped / total;

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Row(
            children: <Widget>[
              Expanded(
                child: _ratioCell(
                  'Correct',
                  computed.totalCorrect,
                  const Color(0xFF34C759),
                ),
              ),
              Expanded(
                child: _ratioCell(
                  'Wrong',
                  computed.totalWrong,
                  const Color(0xFFFF3B30),
                ),
              ),
              Expanded(
                child: _ratioCell(
                  'Skipped',
                  computed.totalSkipped,
                  const Color(0xFF8E8E93),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          ClipRRect(
            borderRadius: BorderRadius.circular(999),
            child: SizedBox(
              height: 12,
              child: Row(
                children: <Widget>[
                  Expanded(
                    flex: math.max(1, (correct * 1000).round()),
                    child: Container(color: const Color(0xFF34C759)),
                  ),
                  Expanded(
                    flex: math.max(1, (wrong * 1000).round()),
                    child: Container(color: const Color(0xFFFF3B30)),
                  ),
                  Expanded(
                    flex: math.max(1, (skipped * 1000).round()),
                    child: Container(color: const Color(0xFF8E8E93)),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _ratioCell(String label, int value, Color color) {
    return Row(
      children: <Widget>[
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 6),
        Text(
          '$label $value',
          style: const TextStyle(color: Color(0xFFFFFFFF), fontSize: 12),
        ),
      ],
    );
  }

  Widget _subjectRow(SubjectComputed subject) {
    final int total =
        subject.subject.correct +
        subject.subject.wrong +
        subject.subject.skipped;
    final int c = subject.subject.correct;
    final int w = subject.subject.wrong;
    final int s = subject.subject.skipped;
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Expanded(
                child: Text(
                  subject.subject.name,
                  style: const TextStyle(
                    color: Color(0xFFFFFFFF),
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              Text(
                '${fixedScore(subject.score)}  ${percentText(subject.accuracy)}',
                style: const TextStyle(color: Color(0xFF8E8E93)),
              ),
            ],
          ),
          const SizedBox(height: 8),
          ClipRRect(
            borderRadius: BorderRadius.circular(999),
            child: SizedBox(
              height: 8,
              child: Row(
                children: <Widget>[
                  Expanded(
                    flex: total == 0 ? 1 : math.max(1, c),
                    child: Container(color: const Color(0xFF34C759)),
                  ),
                  Expanded(
                    flex: total == 0 ? 1 : math.max(1, w),
                    child: Container(color: const Color(0xFFFF3B30)),
                  ),
                  Expanded(
                    flex: total == 0 ? 1 : math.max(1, s),
                    child: Container(color: const Color(0xFF8E8E93)),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _lineChart(List<FlSpot> spots) {
    if (spots.isEmpty) {
      return const SizedBox.shrink();
    }
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(16),
      ),
      child: LineChart(
        LineChartData(
          minY: 0,
          maxY: 100,
          titlesData: const FlTitlesData(show: false),
          borderData: FlBorderData(show: false),
          gridData: const FlGridData(
            show: true,
            drawVerticalLine: false,
            horizontalInterval: 20,
          ),
          lineBarsData: <LineChartBarData>[
            LineChartBarData(
              spots: spots,
              isCurved: true,
              barWidth: 3,
              color: const Color(0xFF0A84FF),
              dotData: const FlDotData(show: false),
              belowBarData: BarAreaData(
                show: true,
                color: const Color(0x330A84FF),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
