package com.griffith.mybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class CurrentHydration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Current Hydration")
            MyButtonsRow()
        }
    }
}

@Composable
fun MyButtonsRow() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(650.dp))
        Divider(color = Color.Blue, thickness = 2.dp)
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, CurrentHydration::class.java)
                    context.startActivity(intent)
                },
                shape = CircleShape,
                modifier = Modifier.size(100.dp)
            ) {
                Text("C")
            }

            Button(
                onClick = {
                    val intent = Intent(context, Profile::class.java)
                    context.startActivity(intent)
                },
                shape = CircleShape,
                modifier = Modifier.size(100.dp)
            ) {
                Text("P")
            }

            Button(
                onClick = {
                    val intent = Intent(context, Profile::class.java)
                    context.startActivity(intent)
                },
                shape = CircleShape,
                modifier = Modifier.size(100.dp)
            ) {
                Text("H")
            }
        }
    }
}