class SubjectEntry {
  const SubjectEntry({
    required this.subjectId,
    required this.name,
    required this.attempted,
    required this.wrong,
    required this.skipped,
  });

  final String subjectId;
  final String name;
  final int attempted;
  final int wrong;
  final int skipped;

  int get correct => attempted - wrong;

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'subjectId': subjectId,
      'name': name,
      'attempted': attempted,
      'wrong': wrong,
      'skipped': skipped,
    };
  }

  factory SubjectEntry.fromJson(Map<String, dynamic> json) {
    return SubjectEntry(
      subjectId: json['subjectId'] as String,
      name: json['name'] as String,
      attempted: (json['attempted'] as num).toInt(),
      wrong: (json['wrong'] as num).toInt(),
      skipped: (json['skipped'] as num).toInt(),
    );
  }
}

class TestEntry {
  const TestEntry({
    required this.id,
    required this.timestamp,
    required this.testName,
    required this.percentile,
    required this.rank,
    required this.totalCandidates,
    required this.subjects,
  });

  final String id;
  final DateTime timestamp;
  final String testName;
  final double percentile;
  final int rank;
  final int totalCandidates;
  final List<SubjectEntry> subjects;

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'id': id,
      'timestamp': timestamp.toUtc().toIso8601String(),
      'testName': testName,
      'percentile': percentile,
      'rank': rank,
      'totalCandidates': totalCandidates,
      'subjects': subjects.map((subject) => subject.toJson()).toList(),
    };
  }

  factory TestEntry.fromJson(Map<String, dynamic> json) {
    final List<dynamic> subjectList =
        (json['subjects'] as List<dynamic>? ?? <dynamic>[]);
    return TestEntry(
      id: json['id'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      testName: json['testName'] as String,
      percentile: (json['percentile'] as num).toDouble(),
      rank: (json['rank'] as num).toInt(),
      totalCandidates: (json['totalCandidates'] as num).toInt(),
      subjects: subjectList
          .map(
            (dynamic item) =>
                SubjectEntry.fromJson(item as Map<String, dynamic>),
          )
          .toList(),
    );
  }
}

class MarkingScheme {
  const MarkingScheme({required this.correctMark, required this.wrongPenalty});

  final double correctMark;
  final double wrongPenalty;
}
