import 'package:flutter/cupertino.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:uuid/uuid.dart';

import '../../app/providers.dart';
import '../../core/formatters.dart';
import '../../domain/entities/models.dart';
import '../../domain/services/analytics_service.dart';

class LogScreen extends ConsumerStatefulWidget {
  const LogScreen({super.key});

  @override
  ConsumerState<LogScreen> createState() => _LogScreenState();
}

class _LogScreenState extends ConsumerState<LogScreen> {
  final Uuid _uuid = const Uuid();
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _folderController = TextEditingController();
  final TextEditingController _correctController = TextEditingController();
  final TextEditingController _wrongController = TextEditingController();
  final TextEditingController _skippedController = TextEditingController();
  final TextEditingController _rankController = TextEditingController();
  final TextEditingController _totalCandidatesController =
      TextEditingController();

  final DateTime _timestamp = DateTime.now();
  String _selectedFolder = '';
  String? _error;
  bool _isSaving = false;

  @override
  void dispose() {
    _nameController.dispose();
    _folderController.dispose();
    _correctController.dispose();
    _wrongController.dispose();
    _skippedController.dispose();
    _rankController.dispose();
    _totalCandidatesController.dispose();
    super.dispose();
  }

  TestEntry? _buildDraft() {
    final AnalyticsService analytics = ref.read(analyticsServiceProvider);
    final String testName = _nameController.text.trim();
    final String folder = _folderController.text.trim().isNotEmpty
        ? _folderController.text.trim()
        : _selectedFolder;
    final int? correct = int.tryParse(_correctController.text.trim());
    final int? wrong = int.tryParse(_wrongController.text.trim());
    final int? skipped = int.tryParse(_skippedController.text.trim());
    final int? rank = int.tryParse(_rankController.text.trim());
    final int? totalCandidates = int.tryParse(
      _totalCandidatesController.text.trim(),
    );

    if (testName.isEmpty ||
        folder.isEmpty ||
        correct == null ||
        wrong == null ||
        skipped == null ||
        rank == null ||
        totalCandidates == null ||
        correct < 0 ||
        wrong < 0 ||
        skipped < 0 ||
        rank <= 0 ||
        totalCandidates <= 0) {
      return null;
    }

    final int attempted = correct + wrong;
    if (wrong > attempted) {
      return null;
    }

    final double percentile = analytics.percentileFromRank(
      rank: rank,
      totalCandidates: totalCandidates,
    );

    final SubjectEntry subject = SubjectEntry(
      subjectId: _uuid.v4(),
      name: folder,
      attempted: attempted,
      wrong: wrong,
      skipped: skipped,
    );

    return TestEntry(
      id: _uuid.v4(),
      timestamp: _timestamp,
      testName: testName,
      percentile: percentile,
      rank: rank,
      totalCandidates: totalCandidates,
      subjects: <SubjectEntry>[subject],
    );
  }

  Future<void> _save() async {
    final TestEntry? draft = _buildDraft();
    if (draft == null) {
      setState(() => _error = 'Fill all fields with valid values.');
      return;
    }

    setState(() {
      _isSaving = true;
      _error = null;
    });
    try {
      await ref.read(testRepositoryProvider).saveTest(draft);
      ref.invalidate(testsProvider);
      if (!mounted) {
        return;
      }
      setState(() {
        _nameController.clear();
        _correctController.clear();
        _wrongController.clear();
        _skippedController.clear();
        _rankController.clear();
        _totalCandidatesController.clear();
      });
    } finally {
      if (mounted) {
        setState(() => _isSaving = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final AsyncValue<List<TestEntry>> testsAsync = ref.watch(testsProvider);
    final AsyncValue<MarkingScheme> schemeAsync = ref.watch(
      markingSchemeProvider,
    );
    final AnalyticsService analytics = ref.watch(analyticsServiceProvider);
    final TestEntry? draft = _buildDraft();

    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(middle: Text('Log')),
      child: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: <Widget>[
            _section(
              'Entry',
              Column(
                children: <Widget>[
                  _field(_nameController, 'Mock name'),
                  const SizedBox(height: 10),
                  testsAsync.when(
                    data: (List<TestEntry> tests) {
                      final List<String> folders =
                          tests
                              .expand((TestEntry t) => t.subjects)
                              .map((SubjectEntry s) => s.name)
                              .toSet()
                              .toList()
                            ..sort();
                      return _folderPicker(folders);
                    },
                    loading: () => const CupertinoActivityIndicator(),
                    error: (Object _, StackTrace __) =>
                        _folderPicker(const <String>[]),
                  ),
                  const SizedBox(height: 10),
                  Row(
                    children: <Widget>[
                      Expanded(
                        child: _field(
                          _correctController,
                          'Correct',
                          isNumber: true,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: _field(
                          _wrongController,
                          'Wrong',
                          isNumber: true,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: _field(
                          _skippedController,
                          'Skipped',
                          isNumber: true,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  _field(_rankController, 'Rank', isNumber: true),
                  const SizedBox(height: 10),
                  _field(
                    _totalCandidatesController,
                    'Total candidates',
                    isNumber: true,
                  ),
                ],
              ),
            ),
            if (draft != null)
              schemeAsync.when(
                data: (MarkingScheme scheme) {
                  final TestComputed computed = analytics.computeTest(
                    draft,
                    scheme,
                  );
                  return _section(
                    'Quick Preview',
                    Text(
                      'Percentile ${draft.percentile.toStringAsFixed(2)} | Score ${fixedScore(computed.score)} | Accuracy ${percentText(computed.accuracy)}',
                      style: const TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  );
                },
                loading: () => const SizedBox.shrink(),
                error: (Object _, StackTrace __) => const SizedBox.shrink(),
              ),
            if (_error != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: Text(
                  _error!,
                  style: const TextStyle(color: Color(0xFFFF453A)),
                ),
              ),
            CupertinoButton.filled(
              borderRadius: BorderRadius.circular(999),
              onPressed: _isSaving ? null : _save,
              child: _isSaving
                  ? const CupertinoActivityIndicator()
                  : const Text('Save Mock Data'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _folderPicker(List<String> folders) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF2C2C2E),
        borderRadius: BorderRadius.circular(14),
      ),
      child: Column(
        children: <Widget>[
          CupertinoButton(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
            onPressed: folders.isEmpty
                ? null
                : () => showCupertinoModalPopup<void>(
                    context: context,
                    builder: (BuildContext context) {
                      return Container(
                        height: 260,
                        color: const Color(0xFF1C1C1E),
                        child: CupertinoPicker(
                          itemExtent: 34,
                          onSelectedItemChanged: (int index) {
                            setState(() => _selectedFolder = folders[index]);
                          },
                          children: folders
                              .map(
                                (String name) => Center(
                                  child: Text(
                                    name,
                                    style: const TextStyle(
                                      color: Color(0xFFFFFFFF),
                                    ),
                                  ),
                                ),
                              )
                              .toList(),
                        ),
                      );
                    },
                  ),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(
                _selectedFolder.isEmpty
                    ? 'Pick existing subject folder'
                    : _selectedFolder,
                style: const TextStyle(color: Color(0xFFFFFFFF)),
              ),
            ),
          ),
          const SizedBox(height: 8),
          _field(_folderController, 'Or create new subject folder'),
        ],
      ),
    );
  }

  Widget _field(
    TextEditingController controller,
    String placeholder, {
    bool isNumber = false,
  }) {
    return CupertinoTextField(
      controller: controller,
      keyboardType: isNumber
          ? const TextInputType.numberWithOptions(decimal: false, signed: false)
          : TextInputType.text,
      decoration: BoxDecoration(
        color: const Color(0xFF2C2C2E),
        borderRadius: BorderRadius.circular(14),
      ),
      style: const TextStyle(color: Color(0xFFFFFFFF), fontSize: 15),
      placeholderStyle: const TextStyle(color: Color(0xFFB8B8BE), fontSize: 15),
      placeholder: placeholder,
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
    );
  }

  Widget _section(String title, Widget child) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            title,
            style: const TextStyle(
              color: Color(0xFFFFFFFF),
              fontSize: 19,
              fontWeight: FontWeight.w700,
            ),
          ),
          const SizedBox(height: 8),
          child,
        ],
      ),
    );
  }
}
