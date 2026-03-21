import 'package:flutter/cupertino.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('cupertino smoke test', (tester) async {
    await tester.pumpWidget(
      const CupertinoApp(
        home: CupertinoPageScaffold(
          navigationBar: CupertinoNavigationBar(middle: Text('Smoke')),
          child: Center(child: Text('OK')),
        ),
      ),
    );

    expect(find.text('Smoke'), findsOneWidget);
    expect(find.text('OK'), findsOneWidget);
  });
}
