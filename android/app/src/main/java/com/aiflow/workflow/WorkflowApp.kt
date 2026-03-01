package com.aiflow.workflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WorkflowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}