import 'package:flutter_test/flutter_test.dart';
import 'package:mock_analysis_android/domain/entities/models.dart';
import 'package:mock_analysis_android/domain/services/stats_service.dart';

void main() {
  group('StatsService', () {
    const english = Category(
      id: 1,
      name: 'English mock',
      createdAt: DateTime(2025, 1, 1),
    );
    const reasoning = Category(
      id: 2,
      name: 'Reasoning mock',
      createdAt: DateTime(2025, 1, 2),
    );
    const maths = Category(
      id: 3,
      name: 'Maths mock',
      createdAt: DateTime(2025, 1, 3),
    );

    final entries = <MockEntry>[
      MockEntry(
        id: 1,
        mockName: 'Mock A',
        totalQuestions: 100,
        rightAnswers: 70,
        wrongAnswers: 20,
        createdAt: DateTime(2025, 1, 10),
        categories: const [english, reasoning],
      ),
      MockEntry(
        id: 2,
        mockName: 'Mock B',
        totalQuestions: 50,
        rightAnswers: 30,
        wrongAnswers: 15,
        createdAt: DateTime(2025, 1, 11),
        categories: const [maths],
      ),
    ];

    test('computes overall stats', () {
      const service = StatsService();
      final overall = service.computeOverall(entries);

      expect(overall.mockCount, 2);
      expect(overall.totalQuestions, 150);
      expect(overall.totalRight, 100);
      expect(overall.totalWrong, 35);
      expect(overall.unanswered, 15);
      expect(overall.accuracy, closeTo(66.666, 0.01));
    });

    test('computes category wise stats for many-to-many entries', () {
      const service = StatsService();
      final byCategory = service.computeByCategory(const [
        english,
        reasoning,
        maths,
      ], entries);

      final englishStats = byCategory.firstWhere((s) => s.category.id == 1);
      final reasoningStats = byCategory.firstWhere((s) => s.category.id == 2);
      final mathsStats = byCategory.firstWhere((s) => s.category.id == 3);

      expect(englishStats.mockCount, 1);
      expect(englishStats.totalQuestions, 100);
      expect(englishStats.totalRight, 70);
      expect(englishStats.totalWrong, 20);

      expect(reasoningStats.mockCount, 1);
      expect(reasoningStats.totalQuestions, 100);
      expect(reasoningStats.totalRight, 70);
      expect(reasoningStats.totalWrong, 20);

      expect(mathsStats.mockCount, 1);
      expect(mathsStats.totalQuestions, 50);
      expect(mathsStats.totalRight, 30);
      expect(mathsStats.totalWrong, 15);
    });
  });
}
