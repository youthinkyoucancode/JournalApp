package com.abir.journalapp

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * MainApplication class serves as the entry point for the application.
 * It initializes Firebase during the app's startup process.
 */
class MainApplication : Application() {

    /**
     * Called when the application is starting before any other application objects have been created.
     * This is where Firebase is initialized for the app.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase for the application context
        FirebaseApp.initializeApp(this)
    }
}