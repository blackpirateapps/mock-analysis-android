import 'package:flutter/widgets.dart';

import 'app/app.dart';
import 'app/app_state.dart';
import 'data/db/app_database.dart';
import 'data/repositories/mock_repository.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final database = AppDatabase.instance;
  final repository = MockRepository(database);
  final state = AppState(repository: repository);

  runApp(MockAnalysisApp(state: state));
}
