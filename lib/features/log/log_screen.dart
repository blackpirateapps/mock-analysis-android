import 'package:flutter/cupertino.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:uuid/uuid.dart';

import '../../app/providers.dart';
import '../../core/formatters.dart';
import '../../data/repositories/test_repository.dart';
import '../../domain/entities/models.dart';
import '../../domain/services/analytics_service.dart';

class LogScreen extends ConsumerStatefulWidget {
  const LogScreen({super.key});

  @override
  ConsumerState<LogScreen> createState() => _LogScreenState();
}

class _DraftSubject {
  _DraftSubject({
    required this.id,
    this.name = '',
    this.attempted = 0,
    this.wrong = 0,
    this.skipped = 0,
  });

  final String id;
  String name;
  int attempted;
  int wrong;
  int skipped;
  late final TextEditingController nameController = TextEditingController(
    text: name,
  );
  late final TextEditingController attemptedController = TextEditingController(
    text: attempted.toString(),
  );
  late final TextEditingController wrongController = TextEditingController(
    text: wrong.toString(),
  );
  late final TextEditingController skippedController = TextEditingController(
    text: skipped.toString(),
  );

  void dispose() {
    nameController.dispose();
    attemptedController.dispose();
    wrongController.dispose();
    skippedController.dispose();
  }

  SubjectEntry toSubjectEntry() => SubjectEntry(
    subjectId: id,
    name: name.trim(),
    attempted: attempted,
    wrong: wrong,
    skipped: skipped,
  );
}

class _LogScreenState extends ConsumerState<LogScreen> {
  final TextEditingController _testNameController = TextEditingController();
  final TextEditingController _percentileController = TextEditingController();
  final TextEditingController _rankController = TextEditingController();
  final TextEditingController _totalCandidatesController =
      TextEditingController();
  final Uuid _uuid = const Uuid();
  DateTime _timestamp = DateTime.now();
  String? _error;
  bool _isSaving = false;
  final List<_DraftSubject> _subjects = <_DraftSubject>[];

  @override
  void initState() {
    super.initState();
    _subjects.add(_DraftSubject(id: _uuid.v4()));
  }

  @override
  void dispose() {
    for (final _DraftSubject subject in _subjects) {
      subject.dispose();
    }
    _testNameController.dispose();
    _percentileController.dispose();
    _rankController.dispose();
    _totalCandidatesController.dispose();
    super.dispose();
  }

  TestEntry? _buildDraft() {
    final String testName = _testNameController.text.trim();
    final double? percentile = double.tryParse(
      _percentileController.text.trim(),
    );
    final int? rank = int.tryParse(_rankController.text.trim());
    final int? totalCandidates = int.tryParse(
      _totalCandidatesController.text.trim(),
    );
    if (testName.isEmpty ||
        percentile == null ||
        rank == null ||
        totalCandidates == null) {
      return null;
    }
    final List<SubjectEntry> parsed = _subjects
        .where((subj) => subj.name.trim().isNotEmpty)
        .map((subj) => subj.toSubjectEntry())
        .toList();
    if (parsed.isEmpty) {
      return null;
    }
    for (final SubjectEntry subject in parsed) {
      if (subject.wrong > subject.attempted ||
          subject.attempted < 0 ||
          subject.wrong < 0 ||
          subject.skipped < 0) {
        return null;
      }
    }
    return TestEntry(
      id: _uuid.v4(),
      timestamp: _timestamp,
      testName: testName,
      percentile: percentile,
      rank: rank,
      totalCandidates: totalCandidates,
      subjects: parsed,
    );
  }

  Future<void> _save() async {
    setState(() {
      _error = null;
    });
    final TestEntry? draft = _buildDraft();
    if (draft == null) {
      setState(() {
        _error = 'Please fill valid test and subject data.';
      });
      return;
    }

    setState(() {
      _isSaving = true;
    });
    try {
      await ref.read(testRepositoryProvider).saveTest(draft);
      ref.invalidate(testsProvider);
      if (!mounted) {
        return;
      }
      setState(() {
        _testNameController.clear();
        _percentileController.clear();
        _rankController.clear();
        _totalCandidatesController.clear();
        for (final _DraftSubject subject in _subjects) {
          subject.dispose();
        }
        _subjects
          ..clear()
          ..add(_DraftSubject(id: _uuid.v4()));
        _timestamp = DateTime.now();
      });
      showCupertinoDialog<void>(
        context: context,
        builder: (BuildContext context) {
          return CupertinoAlertDialog(
            title: const Text('Saved'),
            content: const Text('Mock entry stored locally.'),
            actions: <Widget>[
              CupertinoDialogAction(
                onPressed: () => Navigator.of(context).pop(),
                child: const Text('OK'),
              ),
            ],
          );
        },
      );
    } finally {
      if (mounted) {
        setState(() {
          _isSaving = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
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
            _Section(
              title: 'Timestamp',
              child: CupertinoButton(
                padding: const EdgeInsets.symmetric(
                  vertical: 12,
                  horizontal: 14,
                ),
                color: const Color(0xFF1C1C1E),
                borderRadius: BorderRadius.circular(16),
                onPressed: () async {
                  await showCupertinoModalPopup<void>(
                    context: context,
                    builder: (BuildContext context) {
                      DateTime temp = _timestamp;
                      return Container(
                        color: const Color(0xFF1C1C1E),
                        height: 280,
                        child: Column(
                          children: <Widget>[
                            Align(
                              alignment: Alignment.centerRight,
                              child: CupertinoButton(
                                onPressed: () {
                                  setState(() {
                                    _timestamp = temp;
                                  });
                                  Navigator.of(context).pop();
                                },
                                child: const Text('Done'),
                              ),
                            ),
                            Expanded(
                              child: CupertinoDatePicker(
                                mode: CupertinoDatePickerMode.dateAndTime,
                                initialDateTime: _timestamp,
                                onDateTimeChanged: (DateTime value) {
                                  temp = value;
                                },
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  );
                },
                child: Text(_timestamp.toLocal().toString()),
              ),
            ),
            _Section(
              title: 'Test Metadata',
              child: Column(
                children: <Widget>[
                  _field(_testNameController, 'Test Name'),
                  const SizedBox(height: 10),
                  _field(_percentileController, 'Percentile', isNumber: true),
                  const SizedBox(height: 10),
                  _field(_rankController, 'Rank', isNumber: true),
                  const SizedBox(height: 10),
                  _field(
                    _totalCandidatesController,
                    'Total Candidates',
                    isNumber: true,
                  ),
                ],
              ),
            ),
            _Section(
              title: 'Subjects',
              child: Column(
                children: <Widget>[
                  for (int i = 0; i < _subjects.length; i++) _subjectCard(i),
                  const SizedBox(height: 8),
                  CupertinoButton(
                    color: const Color(0xFF0A84FF),
                    borderRadius: BorderRadius.circular(999),
                    onPressed: () => setState(
                      () => _subjects.add(_DraftSubject(id: _uuid.v4())),
                    ),
                    child: const Text('Add Subject'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            if (draft != null)
              schemeAsync.when(
                data: (MarkingScheme scheme) {
                  final TestComputed computed = analytics.computeTest(
                    draft,
                    scheme,
                  );
                  return _Section(
                    title: 'Preview',
                    child: Text(
                      'Score ${fixedScore(computed.score)}  Accuracy ${percentText(computed.accuracy)}',
                      style: const TextStyle(
                        color: Color(0xFFFFFFFF),
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                  );
                },
                loading: () =>
                    const Center(child: CupertinoActivityIndicator()),
                error: (Object error, StackTrace stackTrace) =>
                    const SizedBox.shrink(),
              ),
            if (_error != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: Text(
                  _error!,
                  style: const TextStyle(color: Color(0xFFFF3B30)),
                ),
              ),
            CupertinoButton.filled(
              borderRadius: BorderRadius.circular(999),
              onPressed: _isSaving ? null : _save,
              child: _isSaving
                  ? const CupertinoActivityIndicator()
                  : const Text('Save Mock'),
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  Widget _subjectCard(int index) {
    final _DraftSubject subject = _subjects[index];
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        children: <Widget>[
          _field(
            subject.nameController,
            'Subject Name',
            onChanged: (String value) {
              subject.name = value;
            },
          ),
          const SizedBox(height: 8),
          Row(
            children: <Widget>[
              Expanded(
                child: _field(
                  subject.attemptedController,
                  'Attempted',
                  isNumber: true,
                  onChanged: (String value) {
                    subject.attempted = int.tryParse(value.trim()) ?? 0;
                  },
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _field(
                  subject.wrongController,
                  'Wrong',
                  isNumber: true,
                  onChanged: (String value) {
                    subject.wrong = int.tryParse(value.trim()) ?? 0;
                  },
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: _field(
                  subject.skippedController,
                  'Skipped',
                  isNumber: true,
                  onChanged: (String value) {
                    subject.skipped = int.tryParse(value.trim()) ?? 0;
                  },
                ),
              ),
            ],
          ),
          if (_subjects.length > 1)
            Align(
              alignment: Alignment.centerRight,
              child: CupertinoButton(
                padding: const EdgeInsets.all(0),
                onPressed: () => setState(() {
                  final _DraftSubject removed = _subjects.removeAt(index);
                  removed.dispose();
                }),
                child: const Text(
                  'Remove',
                  style: TextStyle(color: Color(0xFFFF3B30)),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _field(
    TextEditingController controller,
    String placeholder, {
    bool isNumber = false,
    void Function(String value)? onChanged,
  }) {
    return CupertinoTextField(
      controller: controller,
      keyboardType: isNumber
          ? const TextInputType.numberWithOptions(decimal: true, signed: false)
          : TextInputType.text,
      placeholder: placeholder,
      onChanged: (String value) => setState(() => onChanged?.call(value)),
      decoration: BoxDecoration(
        color: const Color(0xFF2C2C2E),
        borderRadius: BorderRadius.circular(14),
      ),
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
      style: const TextStyle(color: Color(0xFFFFFFFF)),
      placeholderStyle: const TextStyle(color: Color(0xFF8E8E93)),
    );
  }
}

class _Section extends StatelessWidget {
  const _Section({required this.title, required this.child});

  final String title;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Text(
              title,
              style: const TextStyle(
                color: Color(0xFFFFFFFF),
                fontSize: 18,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          child,
        ],
      ),
    );
  }
}
