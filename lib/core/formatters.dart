String percentText(double value) {
  return '${(value * 100).toStringAsFixed(1)}%';
}

String fixedScore(double value) {
  if (value == value.roundToDouble()) {
    return value.toStringAsFixed(0);
  }
  return value.toStringAsFixed(2);
}
