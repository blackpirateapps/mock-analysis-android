import 'package:flutter/cupertino.dart';

import '../features/home/home_screen.dart';
import '../features/insights/insights_screen.dart';
import '../features/log/log_screen.dart';
import '../features/settings/settings_screen.dart';

class MockAnalysisApp extends StatelessWidget {
  const MockAnalysisApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const CupertinoApp(
      debugShowCheckedModeBanner: false,
      theme: CupertinoThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: Color(0xFF000000),
        primaryColor: Color(0xFF0A84FF),
        barBackgroundColor: Color(0xFF000000),
      ),
      home: RootTabScaffold(),
    );
  }
}

class RootTabScaffold extends StatelessWidget {
  const RootTabScaffold({super.key});

  @override
  Widget build(BuildContext context) {
    return CupertinoTabScaffold(
      tabBar: CupertinoTabBar(
        backgroundColor: const Color(0xFF000000),
        activeColor: const Color(0xFFFFFFFF),
        inactiveColor: const Color(0xFF8E8E93),
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(CupertinoIcons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(CupertinoIcons.add_circled),
            label: 'Log',
          ),
          BottomNavigationBarItem(
            icon: Icon(CupertinoIcons.chart_bar_alt_fill),
            label: 'Insights',
          ),
          BottomNavigationBarItem(
            icon: Icon(CupertinoIcons.settings),
            label: 'Settings',
          ),
        ],
      ),
      tabBuilder: (BuildContext context, int index) {
        switch (index) {
          case 0:
            return const HomeScreen();
          case 1:
            return const LogScreen();
          case 2:
            return const InsightsScreen();
          default:
            return const SettingsScreen();
        }
      },
    );
  }
}
