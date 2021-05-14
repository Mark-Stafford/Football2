package com.example.markassignment.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class FootballApp : Application(), AnkoLogger {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference




    override fun onCreate() {
        super.onCreate()
        info("Football App started")

    }
}

