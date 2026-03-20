import 'dart:convert';

import 'package:drift/drift.dart';

import '../../domain/entities/models.dart';
import '../db/app_database.dart';

enum ImportMode { merge, overwrite }

class TestRepository {
  TestRepository(this._db);

  final AppDatabase _db;

  Future<void> saveTest(TestEntry test) async {
    await _db.transaction(() async {
      await _db
          .into(_db.tests)
          .insertOnConflictUpdate(
            TestsCompanion.insert(
              id: test.id,
              timestamp: test.timestamp.toUtc().toIso8601String(),
              testName: test.testName,
              percentile: test.percentile,
              rank: test.rank,
              totalCandidates: test.totalCandidates,
            ),
          );

      await (_db.delete(
        _db.subjects,
      )..where((Subjects tbl) => tbl.testId.equals(test.id))).go();
      for (final SubjectEntry subject in test.subjects) {
        await _db
            .into(_db.subjects)
            .insert(
              SubjectsCompanion.insert(
                subjectId: subject.subjectId,
                testId: test.id,
                name: subject.name,
                attempted: subject.attempted,
                wrong: subject.wrong,
                skipped: subject.skipped,
              ),
            );
      }
    });
  }

  Future<List<TestEntry>> getAllTests() async {
    final List<Test> testRows =
        await (_db.select(_db.tests)..orderBy(<OrderingTerm Function(Tests)>[
              (Tests t) => OrderingTerm.desc(t.timestamp),
            ]))
            .get();
    final List<Subject> subjectRows = await _db.select(_db.subjects).get();
    final Map<String, List<SubjectEntry>> subjectMap =
        <String, List<SubjectEntry>>{};
    for (final Subject row in subjectRows) {
      subjectMap
          .putIfAbsent(row.testId, () => <SubjectEntry>[])
          .add(
            SubjectEntry(
              subjectId: row.subjectId,
              name: row.name,
              attempted: row.attempted,
              wrong: row.wrong,
              skipped: row.skipped,
            ),
          );
    }

    return testRows
        .map(
          (Test row) => TestEntry(
            id: row.id,
            timestamp: DateTime.parse(row.timestamp),
            testName: row.testName,
            percentile: row.percentile,
            rank: row.rank,
            totalCandidates: row.totalCandidates,
            subjects: subjectMap[row.id] ?? <SubjectEntry>[],
          ),
        )
        .toList();
  }

  Future<String> exportBackupJson() async {
    final List<TestEntry> tests = await getAllTests();
    final Map<String, dynamic> body = <String, dynamic>{
      'tests': tests.map((TestEntry item) => item.toJson()).toList(),
    };
    return const JsonEncoder.withIndent('  ').convert(body);
  }

  Future<void> importBackupJson(String raw, ImportMode mode) async {
    final dynamic decoded = jsonDecode(raw);
    if (decoded is! Map<String, dynamic>) {
      throw const FormatException('Backup root must be a JSON object.');
    }
    final dynamic testsRaw = decoded['tests'];
    if (testsRaw is! List<dynamic>) {
      throw const FormatException('Backup must contain a tests array.');
    }
    final List<TestEntry> incoming = testsRaw
        .map((dynamic item) => TestEntry.fromJson(item as Map<String, dynamic>))
        .toList();

    if (mode == ImportMode.overwrite) {
      await _db.delete(_db.subjects).go();
      await _db.delete(_db.tests).go();
    }
    for (final TestEntry test in incoming) {
      await saveTest(test);
    }
  }
}
