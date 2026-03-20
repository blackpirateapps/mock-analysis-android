import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:mock_analysis_android/app/app.dart';
import 'package:mock_analysis_android/app/providers.dart';
import 'package:mock_analysis_android/domain/entities/models.dart';

void main() {
  testWidgets('app root renders', (WidgetTester tester) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: <Override>[
          markingSchemeProvider.overrideWith((Ref ref) async {
            return const MarkingScheme(correctMark: 2, wrongPenalty: 0.5);
          }),
          testsProvider.overrideWith((Ref ref) async => <TestEntry>[]),
        ],
        child: const MockAnalysisApp(),
      ),
    );
    expect(find.text('Log'), findsOneWidget);
  });
}
