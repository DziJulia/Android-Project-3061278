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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                MyButtonsRow()
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

/**
 * A composable function that displays a card for selecting time intervals, such as days, weeks, months, and years.
 */
@Composable
fun TimeSelectionCard() {
    val (selectedButton, onButtonSelected) = remember { mutableStateOf("D") }

    Card(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .padding(end = if (isLandscape()) 70.dp else 0.dp)
    ) {
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

/**
 * A composable function that creates a clickable button with a custom background color and text.
 * @param text The text to be displayed on the button.
 * @param width The width of the button.
 * @param selected A boolean value that determines the background color of the button. If true, the background color is blue; otherwise, it's white.
 * @param lastButton A boolean value that determines whether to draw a line on the button. If true, no line is drawn; otherwise, a line is drawn.
 * @param onClick A lambda function that is invoked when the button is clicked.
 */
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

/**
 * A composable function that displays a card containing a graph and associated UI elements.
 */
@Composable
fun GraphCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLandscape()) 0.dp else 100.dp, end = if (isLandscape()) 70.dp else 0.dp)
            .wrapContentHeight(Alignment.CenterVertically),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .border(2.dp, Color(192, 226, 236)),
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

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color =  Color(192, 226, 236),
                thickness = 1.dp
            )

            Text("Total", textAlign = TextAlign.Start)
            Text("0 ml", textAlign = TextAlign.Start)
            // TODO: Add graph here
        }
    }
}

/**
 * Function that checks whether the current device orientation is landscape.
 * @return true if the device is in landscape orientation, false otherwise.
 */
@Composable
fun isLandscape(): Boolean {
    return LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
}
