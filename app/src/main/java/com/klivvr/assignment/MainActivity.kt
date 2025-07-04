package com.klivvr.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.klivvr.assignment.ui.composables.city.CityScreen
import com.klivvr.assignment.ui.theme.KlivvrAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlivvrAssignmentTheme {
                CityScreen()
            }
        }
    }
}

