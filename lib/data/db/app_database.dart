import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

class AppDatabase {
  AppDatabase._();

  static final AppDatabase instance = AppDatabase._();
  static const _dbName = 'mock_analysis.db';

  Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _open();
    return _database!;
  }

  Future<Database> _open() async {
    final dbPath = await getDatabasesPath();
    final path = p.join(dbPath, _dbName);

    return openDatabase(
      path,
      version: 1,
      onConfigure: (db) async {
        await db.execute('PRAGMA foreign_keys = ON;');
      },
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE categories (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE,
            created_at INTEGER NOT NULL
          );
        ''');

        await db.execute('''
          CREATE TABLE mock_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            mock_name TEXT NOT NULL,
            total_questions INTEGER NOT NULL,
            right_answers INTEGER NOT NULL,
            wrong_answers INTEGER NOT NULL,
            created_at INTEGER NOT NULL
          );
        ''');

        await db.execute('''
          CREATE TABLE entry_categories (
            entry_id INTEGER NOT NULL,
            category_id INTEGER NOT NULL,
            PRIMARY KEY (entry_id, category_id),
            FOREIGN KEY (entry_id) REFERENCES mock_entries(id) ON DELETE CASCADE,
            FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
          );
        ''');

        await db.execute(
          'CREATE INDEX idx_entry_categories_entry ON entry_categories(entry_id);',
        );
        await db.execute(
          'CREATE INDEX idx_entry_categories_category ON entry_categories(category_id);',
        );
      },
    );
  }
}
