import '../entities/models.dart';

class StatsService {
  const StatsService();

  OverallStats computeOverall(List<MockEntry> entries) {
    var totalQuestions = 0;
    var totalRight = 0;
    var totalWrong = 0;

    for (final entry in entries) {
      totalQuestions += entry.totalQuestions;
      totalRight += entry.rightAnswers;
      totalWrong += entry.wrongAnswers;
    }

    return OverallStats(
      mockCount: entries.length,
      totalQuestions: totalQuestions,
      totalRight: totalRight,
      totalWrong: totalWrong,
    );
  }

  List<CategoryStats> computeByCategory(
    List<Category> categories,
    List<MockEntry> entries,
  ) {
    final byCategory = <int, List<MockEntry>>{};

    for (final category in categories) {
      byCategory[category.id] = <MockEntry>[];
    }

    for (final entry in entries) {
      for (final category in entry.categories) {
        byCategory.putIfAbsent(category.id, () => <MockEntry>[]).add(entry);
      }
    }

    final stats = <CategoryStats>[];

    for (final category in categories) {
      final categoryEntries = byCategory[category.id] ?? <MockEntry>[];
      var totalQuestions = 0;
      var totalRight = 0;
      var totalWrong = 0;

      for (final entry in categoryEntries) {
        totalQuestions += entry.totalQuestions;
        totalRight += entry.rightAnswers;
        totalWrong += entry.wrongAnswers;
      }

      stats.add(
        CategoryStats(
          category: category,
          mockCount: categoryEntries.length,
          totalQuestions: totalQuestions,
          totalRight: totalRight,
          totalWrong: totalWrong,
        ),
      );
    }

    stats.sort((a, b) => b.accuracy.compareTo(a.accuracy));
    return stats;
  }
}
