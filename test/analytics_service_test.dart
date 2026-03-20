import 'package:flutter_test/flutter_test.dart';

import 'package:mock_analysis_android/domain/entities/models.dart';
import 'package:mock_analysis_android/domain/services/analytics_service.dart';

void main() {
  const AnalyticsService service = AnalyticsService();
  const MarkingScheme scheme = MarkingScheme(correctMark: 2, wrongPenalty: 0.5);

  test('computes score and accuracy for a full test', () {
    final TestEntry test = TestEntry(
      id: 't1',
      timestamp: DateTime.parse('2026-03-20T18:01:09.000Z'),
      testName: 'Mock 1',
      percentile: 94.5,
      rank: 1200,
      totalCandidates: 45000,
      subjects: const <SubjectEntry>[
        SubjectEntry(
          subjectId: 's1',
          name: 'GI',
          attempted: 22,
          wrong: 3,
          skipped: 3,
        ),
        SubjectEntry(
          subjectId: 's2',
          name: 'Math',
          attempted: 20,
          wrong: 2,
          skipped: 5,
        ),
      ],
    );

    final TestComputed computed = service.computeTest(test, scheme);
    expect(computed.totalCorrect, 37);
    expect(computed.totalWrong, 5);
    expect(computed.totalAttempted, 42);
    expect(computed.accuracy, closeTo(37 / 42, 0.0001));
    expect(computed.score, closeTo((37 * 2) - (5 * 0.5), 0.0001));
  });

  test('returns weak subjects sorted by average accuracy ascending', () {
    final List<TestEntry> tests = <TestEntry>[
      TestEntry(
        id: '1',
        timestamp: DateTime.now(),
        testName: 'A',
        percentile: 80,
        rank: 1,
        totalCandidates: 1,
        subjects: const <SubjectEntry>[
          SubjectEntry(
            subjectId: 'a',
            name: 'Math',
            attempted: 10,
            wrong: 5,
            skipped: 0,
          ),
          SubjectEntry(
            subjectId: 'b',
            name: 'GI',
            attempted: 10,
            wrong: 1,
            skipped: 0,
          ),
        ],
      ),
      TestEntry(
        id: '2',
        timestamp: DateTime.now(),
        testName: 'B',
        percentile: 82,
        rank: 1,
        totalCandidates: 1,
        subjects: const <SubjectEntry>[
          SubjectEntry(
            subjectId: 'c',
            name: 'Math',
            attempted: 10,
            wrong: 4,
            skipped: 0,
          ),
          SubjectEntry(
            subjectId: 'd',
            name: 'GI',
            attempted: 10,
            wrong: 0,
            skipped: 0,
          ),
        ],
      ),
    ];

    final List<WeakSubjectStat> weak = service.weakSubjects(tests);
    expect(weak.first.subjectName, 'Math');
  });
}
