import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/db/app_database.dart';
import '../data/repositories/settings_repository.dart';
import '../data/repositories/test_repository.dart';
import '../domain/entities/models.dart';
import '../domain/services/analytics_service.dart';

final Provider<AppDatabase> databaseProvider = Provider<AppDatabase>((Ref ref) {
  final AppDatabase db = AppDatabase();
  ref.onDispose(db.close);
  return db;
});

final Provider<TestRepository> testRepositoryProvider =
    Provider<TestRepository>((Ref ref) {
      return TestRepository(ref.watch(databaseProvider));
    });

final Provider<SettingsRepository> settingsRepositoryProvider =
    Provider<SettingsRepository>((Ref ref) {
      return SettingsRepository(ref.watch(databaseProvider));
    });

final Provider<AnalyticsService> analyticsServiceProvider =
    Provider<AnalyticsService>((Ref ref) {
      return const AnalyticsService();
    });

final FutureProvider<MarkingScheme> markingSchemeProvider =
    FutureProvider<MarkingScheme>((Ref ref) async {
      return ref.watch(settingsRepositoryProvider).getMarkingScheme();
    });

final FutureProvider<List<TestEntry>> testsProvider =
    FutureProvider<List<TestEntry>>((Ref ref) async {
      return ref.watch(testRepositoryProvider).getAllTests();
    });
