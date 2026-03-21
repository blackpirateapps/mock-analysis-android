import '../../domain/entities/models.dart';
import '../db/app_database.dart';

class MockRepository {
  MockRepository(this._database);

  final AppDatabase _database;

  Future<List<Category>> getCategories() async {
    final db = await _database.database;
    final rows = await db.query(
      'categories',
      orderBy: 'name COLLATE NOCASE ASC',
    );
    return rows
        .map(
          (row) => Category(
            id: row['id'] as int,
            name: row['name'] as String,
            createdAt: DateTime.fromMillisecondsSinceEpoch(
              row['created_at'] as int,
            ),
          ),
        )
        .toList(growable: false);
  }

  Future<int> createCategory(String name) async {
    final normalized = name.trim();
    if (normalized.isEmpty) {
      throw ArgumentError('Category name cannot be empty');
    }

    final db = await _database.database;
    return db.insert('categories', {
      'name': normalized,
      'created_at': DateTime.now().millisecondsSinceEpoch,
    });
  }

  Future<void> deleteCategory(int id) async {
    final db = await _database.database;
    final linkedRows = await db.rawQuery(
      'SELECT COUNT(*) AS c FROM entry_categories WHERE category_id = ?',
      [id],
    );
    final linkedCount = (linkedRows.first['c'] as int?) ?? 0;
    if (linkedCount > 0) {
      throw StateError(
        'Cannot delete category with linked entries. Remove entry links first.',
      );
    }
    await db.delete('categories', where: 'id = ?', whereArgs: [id]);
  }

  Future<List<MockEntry>> getEntries() async {
    final db = await _database.database;
    final entryRows = await db.query(
      'mock_entries',
      orderBy: 'created_at DESC, id DESC',
    );
    if (entryRows.isEmpty) return <MockEntry>[];

    final categoryRows = await db.query('categories');
    final categoriesById = {
      for (final row in categoryRows)
        row['id'] as int: Category(
          id: row['id'] as int,
          name: row['name'] as String,
          createdAt: DateTime.fromMillisecondsSinceEpoch(
            row['created_at'] as int,
          ),
        ),
    };

    final links = await db.query('entry_categories');
    final categoryIdsByEntry = <int, List<int>>{};
    for (final link in links) {
      final entryId = link['entry_id'] as int;
      final categoryId = link['category_id'] as int;
      categoryIdsByEntry.putIfAbsent(entryId, () => <int>[]).add(categoryId);
    }

    return entryRows
        .map((row) {
          final entryId = row['id'] as int;
          final ids = categoryIdsByEntry[entryId] ?? <int>[];
          final categories = ids
              .map((id) => categoriesById[id])
              .whereType<Category>()
              .toList(growable: false);

          return MockEntry(
            id: entryId,
            mockName: row['mock_name'] as String,
            totalQuestions: row['total_questions'] as int,
            rightAnswers: row['right_answers'] as int,
            wrongAnswers: row['wrong_answers'] as int,
            createdAt: DateTime.fromMillisecondsSinceEpoch(
              row['created_at'] as int,
            ),
            categories: categories,
          );
        })
        .toList(growable: false);
  }

  Future<void> createEntry({
    required String mockName,
    required int totalQuestions,
    required int rightAnswers,
    required int wrongAnswers,
    required List<int> categoryIds,
  }) async {
    final name = mockName.trim();
    _validateEntry(
      mockName: name,
      totalQuestions: totalQuestions,
      rightAnswers: rightAnswers,
      wrongAnswers: wrongAnswers,
      categoryIds: categoryIds,
    );

    final db = await _database.database;
    await db.transaction((txn) async {
      final entryId = await txn.insert('mock_entries', {
        'mock_name': name,
        'total_questions': totalQuestions,
        'right_answers': rightAnswers,
        'wrong_answers': wrongAnswers,
        'created_at': DateTime.now().millisecondsSinceEpoch,
      });

      for (final categoryId in categoryIds.toSet()) {
        await txn.insert('entry_categories', {
          'entry_id': entryId,
          'category_id': categoryId,
        });
      }
    });
  }

  Future<void> deleteEntry(int id) async {
    final db = await _database.database;
    await db.delete('mock_entries', where: 'id = ?', whereArgs: [id]);
  }

  void _validateEntry({
    required String mockName,
    required int totalQuestions,
    required int rightAnswers,
    required int wrongAnswers,
    required List<int> categoryIds,
  }) {
    if (mockName.isEmpty) {
      throw ArgumentError('Mock name is required');
    }
    if (totalQuestions <= 0) {
      throw ArgumentError('Total questions must be greater than 0');
    }
    if (rightAnswers < 0 || wrongAnswers < 0) {
      throw ArgumentError('Right and wrong answers cannot be negative');
    }
    if (rightAnswers + wrongAnswers > totalQuestions) {
      throw ArgumentError('Right + wrong cannot exceed total questions');
    }
    if (categoryIds.isEmpty) {
      throw ArgumentError('At least one category is required');
    }
  }
}
