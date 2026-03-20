import 'dart:io';

import 'package:cross_file/cross_file.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';

import '../../app/providers.dart';
import '../../data/repositories/test_repository.dart';
import '../../domain/entities/models.dart';

class SettingsScreen extends ConsumerStatefulWidget {
  const SettingsScreen({super.key});

  @override
  ConsumerState<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends ConsumerState<SettingsScreen> {
  final TextEditingController _correctController = TextEditingController();
  final TextEditingController _wrongController = TextEditingController();
  String? _status;
  bool _busy = false;

  @override
  void dispose() {
    _correctController.dispose();
    _wrongController.dispose();
    super.dispose();
  }

  Future<void> _saveScheme() async {
    final double? correct = double.tryParse(_correctController.text.trim());
    final double? wrong = double.tryParse(_wrongController.text.trim());
    if (correct == null || wrong == null || correct <= 0 || wrong < 0) {
      setState(() => _status = 'Invalid marking values.');
      return;
    }
    setState(() {
      _busy = true;
      _status = null;
    });
    try {
      await ref
          .read(settingsRepositoryProvider)
          .saveMarkingScheme(
            MarkingScheme(correctMark: correct, wrongPenalty: wrong),
          );
      ref.invalidate(markingSchemeProvider);
      setState(() => _status = 'Marking scheme saved.');
    } finally {
      if (mounted) {
        setState(() => _busy = false);
      }
    }
  }

  Future<void> _exportJson() async {
    setState(() {
      _busy = true;
      _status = null;
    });
    try {
      final String raw = await ref
          .read(testRepositoryProvider)
          .exportBackupJson();
      final Directory dir = await getTemporaryDirectory();
      final File file = File('${dir.path}/mock_analysis_backup.json');
      await file.writeAsString(raw);
      await Share.shareXFiles(<XFile>[
        XFile(file.path),
      ], text: 'mock_analysis_backup.json');
      setState(() => _status = 'Backup exported to share sheet.');
    } finally {
      if (mounted) {
        setState(() => _busy = false);
      }
    }
  }

  Future<void> _importJson(ImportMode mode) async {
    setState(() {
      _busy = true;
      _status = null;
    });
    try {
      final FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: <String>['json'],
      );
      final String? path = result?.files.single.path;
      if (path == null) {
        setState(() => _status = 'Import cancelled.');
        return;
      }
      final String raw = await File(path).readAsString();
      await ref.read(testRepositoryProvider).importBackupJson(raw, mode);
      ref.invalidate(testsProvider);
      setState(
        () => _status = mode == ImportMode.merge
            ? 'Backup merged.'
            : 'Backup overwritten.',
      );
    } on FormatException catch (e) {
      setState(() => _status = 'Invalid backup: ${e.message}');
    } finally {
      if (mounted) {
        setState(() => _busy = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final AsyncValue<MarkingScheme> schemeAsync = ref.watch(
      markingSchemeProvider,
    );
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Settings')),
      child: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: <Widget>[
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFF1C1C1E),
                borderRadius: BorderRadius.circular(16),
              ),
              child: schemeAsync.when(
                data: (MarkingScheme scheme) {
                  if (_correctController.text.isEmpty) {
                    _correctController.text = scheme.correctMark.toString();
                    _wrongController.text = scheme.wrongPenalty.toString();
                  }
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      const Text(
                        'Global Marking Scheme',
                        style: TextStyle(
                          color: Color(0xFFFFFFFF),
                          fontWeight: FontWeight.w700,
                          fontSize: 18,
                        ),
                      ),
                      const SizedBox(height: 10),
                      CupertinoTextField(
                        controller: _correctController,
                        keyboardType: const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        placeholder: 'Correct marks (e.g. 2)',
                      ),
                      const SizedBox(height: 10),
                      CupertinoTextField(
                        controller: _wrongController,
                        keyboardType: const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        placeholder: 'Wrong penalty (e.g. 0.5)',
                      ),
                      const SizedBox(height: 10),
                      CupertinoButton.filled(
                        borderRadius: BorderRadius.circular(999),
                        onPressed: _busy ? null : _saveScheme,
                        child: const Text('Save Marking'),
                      ),
                    ],
                  );
                },
                loading: () => const CupertinoActivityIndicator(),
                error: (Object error, StackTrace stackTrace) => Text(
                  error.toString(),
                  style: const TextStyle(color: Color(0xFFFF3B30)),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFF1C1C1E),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  const Text(
                    'Data Portability',
                    style: TextStyle(
                      color: Color(0xFFFFFFFF),
                      fontWeight: FontWeight.w700,
                      fontSize: 18,
                    ),
                  ),
                  const SizedBox(height: 10),
                  CupertinoButton(
                    color: const Color(0xFF0A84FF),
                    borderRadius: BorderRadius.circular(999),
                    onPressed: _busy ? null : _exportJson,
                    child: const Text('Export JSON'),
                  ),
                  const SizedBox(height: 8),
                  CupertinoButton(
                    color: const Color(0xFF34C759),
                    borderRadius: BorderRadius.circular(999),
                    onPressed: _busy
                        ? null
                        : () => _importJson(ImportMode.merge),
                    child: const Text('Import JSON (Merge)'),
                  ),
                  const SizedBox(height: 8),
                  CupertinoButton(
                    color: const Color(0xFFFF3B30),
                    borderRadius: BorderRadius.circular(999),
                    onPressed: _busy
                        ? null
                        : () => _importJson(ImportMode.overwrite),
                    child: const Text('Import JSON (Overwrite)'),
                  ),
                ],
              ),
            ),
            if (_status != null)
              Padding(
                padding: const EdgeInsets.only(top: 14),
                child: Text(
                  _status!,
                  style: const TextStyle(color: Color(0xFF8E8E93)),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
