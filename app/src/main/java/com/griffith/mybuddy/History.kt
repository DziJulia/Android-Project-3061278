package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                val context = LocalContext.current
                val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                MyButtonsRow(isPortrait = isPortrait)
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("History", style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.size(25.dp))
                    TimeSelectionCard()
                    Spacer(modifier = Modifier.height(20.dp))
                    GraphCard()
                }
            }
        }
    }
}

@Composable
fun TimeSelectionCard() {
    val (selectedButton, onButtonSelected) = remember { mutableStateOf("D") }

    Card(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        BoxWithConstraints {
            val buttonWidth = maxWidth / 4

            Row(modifier = Modifier.fillMaxWidth()) {
                TimeSelectionButton("D", buttonWidth, selectedButton == "D") { onButtonSelected("D") }
                TimeSelectionButton("W", buttonWidth, selectedButton == "W") { onButtonSelected("W") }
                TimeSelectionButton("M", buttonWidth, selectedButton == "M") { onButtonSelected("M") }
                TimeSelectionButton("Y", buttonWidth, selectedButton == "Y") { onButtonSelected("Y")}
            }
        }
    }
}

@Composable
fun TimeSelectionButton(text: String, width: Dp, selected: Boolean = false, lastButton: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .requiredWidth(width)
            .clickable(onClick = onClick)
            .background(color = if (selected) Color.Blue else Color.White)
            .padding(top = 2.dp, bottom = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!lastButton) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(size.width - 1.dp.toPx(), 2.dp.toPx()),
                    end = Offset(size.width - 1.dp.toPx(), size.height - 2.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            })
        }
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun GraphCard() {
    Card(modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.CenterVertically)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /*TODO: Handle left button click*/ },
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("<")
                }

                Text("Today", textAlign = TextAlign.Center)

                Button(
                    onClick = { /*TODO: Handle right button click*/ },
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text(">")
                }
            }

            Text("Total", textAlign = TextAlign.Center)
            Text("0 ml", textAlign = TextAlign.Center)
            // TODO: Add graph here
        }
    }
}
