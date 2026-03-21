import 'dart:async';

import '../data/repositories/mock_repository.dart';
import '../domain/entities/models.dart';
import '../domain/services/stats_service.dart';

class AppState {
  AppState({
    required MockRepository repository,
    StatsService statsService = const StatsService(),
  }) : _repository = repository,
       _statsService = statsService;

  final MockRepository _repository;
  final StatsService _statsService;

  final StreamController<void> _controller = StreamController<void>.broadcast();

  Stream<void> get updates => _controller.stream;

  Future<List<Category>> loadCategories() => _repository.getCategories();

  Future<List<MockEntry>> loadEntries() => _repository.getEntries();

  Future<void> createCategory(String name) async {
    await _repository.createCategory(name);
    _notify();
  }

  Future<void> deleteCategory(int id) async {
    await _repository.deleteCategory(id);
    _notify();
  }

  Future<void> createEntry({
    required String mockName,
    required int totalQuestions,
    required int rightAnswers,
    required int wrongAnswers,
    required List<int> categoryIds,
  }) async {
    await _repository.createEntry(
      mockName: mockName,
      totalQuestions: totalQuestions,
      rightAnswers: rightAnswers,
      wrongAnswers: wrongAnswers,
      categoryIds: categoryIds,
    );
    _notify();
  }

  Future<void> deleteEntry(int id) async {
    await _repository.deleteEntry(id);
    _notify();
  }

  Future<OverallStats> loadOverallStats() async {
    final entries = await _repository.getEntries();
    return _statsService.computeOverall(entries);
  }

  Future<List<CategoryStats>> loadCategoryStats() async {
    final categories = await _repository.getCategories();
    final entries = await _repository.getEntries();
    return _statsService.computeByCategory(categories, entries);
  }

  void _notify() {
    if (!_controller.isClosed) {
      _controller.add(null);
    }
  }

  Future<void> dispose() async {
    await _controller.close();
  }
}
