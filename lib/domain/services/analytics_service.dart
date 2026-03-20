import '../entities/models.dart';

class SubjectComputed {
  const SubjectComputed({
    required this.subject,
    required this.score,
    required this.accuracy,
    required this.correct,
  });

  final SubjectEntry subject;
  final double score;
  final double accuracy;
  final int correct;
}

class TestComputed {
  const TestComputed({
    required this.score,
    required this.accuracy,
    required this.totalAttempted,
    required this.totalWrong,
    required this.totalSkipped,
    required this.totalCorrect,
    required this.subjects,
  });

  final double score;
  final double accuracy;
  final int totalAttempted;
  final int totalWrong;
  final int totalSkipped;
  final int totalCorrect;
  final List<SubjectComputed> subjects;
}

class WeakSubjectStat {
  const WeakSubjectStat({
    required this.subjectName,
    required this.averageAccuracy,
  });

  final String subjectName;
  final double averageAccuracy;
}

class AnalyticsService {
  const AnalyticsService();

  double percentileFromRank({required int rank, required int totalCandidates}) {
    if (rank <= 0 || totalCandidates <= 0) {
      return 0;
    }
    final int safeRank = rank > totalCandidates ? totalCandidates : rank;
    final double percentile =
        ((totalCandidates - safeRank + 1) / totalCandidates) * 100;
    return percentile.clamp(0, 100).toDouble();
  }

  SubjectComputed computeSubject(SubjectEntry subject, MarkingScheme scheme) {
    final int correct = subject.correct;
    final int attempted = subject.attempted;
    final double score =
        (correct * scheme.correctMark) - (subject.wrong * scheme.wrongPenalty);
    final double accuracy = attempted > 0 ? correct / attempted : 0;
    return SubjectComputed(
      subject: subject,
      score: score,
      accuracy: accuracy,
      correct: correct,
    );
  }

  TestComputed computeTest(TestEntry test, MarkingScheme scheme) {
    final List<SubjectComputed> subjects = test.subjects
        .map((subject) => computeSubject(subject, scheme))
        .toList();
    final int totalAttempted = test.subjects.fold(
      0,
      (int sum, SubjectEntry item) => sum + item.attempted,
    );
    final int totalWrong = test.subjects.fold(
      0,
      (int sum, SubjectEntry item) => sum + item.wrong,
    );
    final int totalSkipped = test.subjects.fold(
      0,
      (int sum, SubjectEntry item) => sum + item.skipped,
    );
    final int totalCorrect = subjects.fold(
      0,
      (int sum, SubjectComputed item) => sum + item.correct,
    );
    final double score = subjects.fold(
      0,
      (double sum, SubjectComputed item) => sum + item.score,
    );
    final double accuracy = totalAttempted > 0
        ? totalCorrect / totalAttempted
        : 0;
    return TestComputed(
      score: score,
      accuracy: accuracy,
      totalAttempted: totalAttempted,
      totalWrong: totalWrong,
      totalSkipped: totalSkipped,
      totalCorrect: totalCorrect,
      subjects: subjects,
    );
  }

  List<WeakSubjectStat> weakSubjects(List<TestEntry> tests) {
    final Map<String, List<double>> perSubjectAccuracies =
        <String, List<double>>{};
    for (final TestEntry test in tests) {
      for (final SubjectEntry subject in test.subjects) {
        final double accuracy = subject.attempted > 0
            ? subject.correct / subject.attempted
            : 0;
        perSubjectAccuracies
            .putIfAbsent(subject.name, () => <double>[])
            .add(accuracy);
      }
    }

    final List<WeakSubjectStat> stats = perSubjectAccuracies.entries.map((
      MapEntry<String, List<double>> entry,
    ) {
      final List<double> values = entry.value;
      final double avg = values.isEmpty
          ? 0
          : values.reduce((double a, double b) => a + b) / values.length;
      return WeakSubjectStat(subjectName: entry.key, averageAccuracy: avg);
    }).toList();

    stats.sort(
      (WeakSubjectStat a, WeakSubjectStat b) =>
          a.averageAccuracy.compareTo(b.averageAccuracy),
    );
    return stats;
  }
}
