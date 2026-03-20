import '../../core/constants.dart';
import '../../domain/entities/models.dart';
import '../db/app_database.dart';

class SettingsRepository {
  SettingsRepository(this._db);

  final AppDatabase _db;

  Future<MarkingScheme> getMarkingScheme() async {
    final String? correctRaw = await _db.getSetting(SettingKeys.correctMark);
    final String? wrongRaw = await _db.getSetting(SettingKeys.wrongPenalty);
    final double correct = double.tryParse(correctRaw ?? '') ?? 2.0;
    final double wrong = double.tryParse(wrongRaw ?? '') ?? 0.5;
    return MarkingScheme(correctMark: correct, wrongPenalty: wrong);
  }

  Future<void> saveMarkingScheme(MarkingScheme scheme) async {
    await _db.upsertSetting(
      SettingKeys.correctMark,
      scheme.correctMark.toString(),
    );
    await _db.upsertSetting(
      SettingKeys.wrongPenalty,
      scheme.wrongPenalty.toString(),
    );
  }
}
