import 'package:flutter/cupertino.dart';

import '../features/categories/categories_screen.dart';
import '../features/entries/entries_screen.dart';
import '../features/statistics/statistics_screen.dart';
import 'app_state.dart';

class MockAnalysisApp extends StatelessWidget {
  const MockAnalysisApp({super.key, required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    return CupertinoApp(
      debugShowCheckedModeBanner: false,
      theme: const CupertinoThemeData(
        brightness: Brightness.light,
        primaryColor: CupertinoColors.activeBlue,
      ),
      home: CupertinoTabScaffold(
        tabBar: const CupertinoTabBar(
          items: [
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.doc_text),
              label: 'Entries',
            ),
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.folder),
              label: 'Categories',
            ),
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.chart_bar),
              label: 'Statistics',
            ),
          ],
        ),
        tabBuilder: (context, index) {
          return CupertinoTabView(
            builder: (context) {
              switch (index) {
                case 0:
                  return EntriesScreen(state: state);
                case 1:
                  return CategoriesScreen(state: state);
                case 2:
                  return StatisticsScreen(state: state);
                default:
                  return EntriesScreen(state: state);
              }
            },
          );
        },
      ),
    );
  }
}
