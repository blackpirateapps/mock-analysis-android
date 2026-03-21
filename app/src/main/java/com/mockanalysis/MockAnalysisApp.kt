package com.mockanalysis

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Mock Analysis app.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class MockAnalysisApp : Application()
