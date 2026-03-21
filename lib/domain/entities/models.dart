class Category {
  const Category({
    required this.id,
    required this.name,
    required this.createdAt,
  });

  final int id;
  final String name;
  final DateTime createdAt;
}

class MockEntry {
  const MockEntry({
    required this.id,
    required this.mockName,
    required this.totalQuestions,
    required this.rightAnswers,
    required this.wrongAnswers,
    required this.createdAt,
    required this.categories,
  });

  final int id;
  final String mockName;
  final int totalQuestions;
  final int rightAnswers;
  final int wrongAnswers;
  final DateTime createdAt;
  final List<Category> categories;

  int get unanswered => totalQuestions - rightAnswers - wrongAnswers;
  double get accuracy =>
      totalQuestions == 0 ? 0 : (rightAnswers / totalQuestions) * 100;
}

class OverallStats {
  const OverallStats({
    required this.mockCount,
    required this.totalQuestions,
    required this.totalRight,
    required this.totalWrong,
  });

  final int mockCount;
  final int totalQuestions;
  final int totalRight;
  final int totalWrong;

  int get unanswered => totalQuestions - totalRight - totalWrong;
  double get accuracy =>
      totalQuestions == 0 ? 0 : (totalRight / totalQuestions) * 100;
}

class CategoryStats {
  const CategoryStats({
    required this.category,
    required this.mockCount,
    required this.totalQuestions,
    required this.totalRight,
    required this.totalWrong,
  });

  final Category category;
  final int mockCount;
  final int totalQuestions;
  final int totalRight;
  final int totalWrong;

  int get unanswered => totalQuestions - totalRight - totalWrong;
  double get accuracy =>
      totalQuestions == 0 ? 0 : (totalRight / totalQuestions) * 100;
}
