package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("History",
                    modifier = Modifier.align(Alignment.Center))
                val context = LocalContext.current
                val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                MyButtonsRow(isPortrait = isPortrait)
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}
