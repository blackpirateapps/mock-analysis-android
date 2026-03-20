import 'package:drift/drift.dart';
import 'package:drift_flutter/drift_flutter.dart';

part 'app_database.g.dart';

class Tests extends Table {
  TextColumn get id => text()();
  TextColumn get timestamp => text()();
  TextColumn get testName => text()();
  RealColumn get percentile => real()();
  IntColumn get rank => integer()();
  IntColumn get totalCandidates => integer()();

  @override
  Set<Column<Object>> get primaryKey => <Column<Object>>{id};
}

class Subjects extends Table {
  TextColumn get subjectId => text()();
  TextColumn get testId =>
      text().references(Tests, #id, onDelete: KeyAction.cascade)();
  TextColumn get name => text()();
  IntColumn get attempted => integer()();
  IntColumn get wrong => integer()();
  IntColumn get skipped => integer()();

  @override
  Set<Column<Object>> get primaryKey => <Column<Object>>{subjectId};
}

class AppSettings extends Table {
  TextColumn get key => text()();
  TextColumn get value => text()();

  @override
  Set<Column<Object>> get primaryKey => <Column<Object>>{key};
}

@DriftDatabase(tables: <Type>[Tests, Subjects, AppSettings])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  Future<void> upsertSetting(String key, String value) async {
    await into(appSettings).insertOnConflictUpdate(
      AppSettingsCompanion.insert(key: key, value: value),
    );
  }

  Future<String?> getSetting(String key) async {
    final AppSetting? row = await (select(
      appSettings,
    )..where((AppSettings tbl) => tbl.key.equals(key))).getSingleOrNull();
    return row?.value;
  }
}

QueryExecutor _openConnection() {
  return driftDatabase(
    name: 'mock_analysis.sqlite',
    native: const DriftNativeOptions(),
  );
}
