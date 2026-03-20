import 'package:flutter_test/flutter_test.dart';

import 'package:mock_analysis_android/app/app.dart';

void main() {
  testWidgets('app root renders', (WidgetTester tester) async {
    await tester.pumpWidget(const MockAnalysisApp());
    expect(find.text('Log'), findsOneWidget);
  });
}
