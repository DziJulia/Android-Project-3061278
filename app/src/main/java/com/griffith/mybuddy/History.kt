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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import java.lang.Float.min
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */
class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val selectedDate = remember { mutableStateOf(Date()) }
            val selectedButton = remember { mutableStateOf("D") }

            Box(modifier = Modifier.fillMaxSize()) {
                MyButtonsRow()
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("History", style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold))
                    AddSpacer(25.dp)
                    TimeSelectionCard(selectedDate, selectedButton)
                    AddSpacer(20.dp)
                    GraphCard(selectedDate, selectedButton)
                }
            }
        }
    }
}

/**
 * A composable function that displays a card for selecting time intervals, such as days, weeks, months, and years.
 */
@Composable
fun TimeSelectionCard(selectedDate: MutableState<Date>, selectedButton: MutableState<String>) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .padding(end = if (isLandscape()) 70.dp else 0.dp)
    ) {
        BoxWithConstraints {
            val buttonWidth = maxWidth / 4

            Row(modifier = Modifier.fillMaxWidth()) {
                TimeSelectionButton("D", buttonWidth, selectedButton.value == "D", selectedDate) { selectedButton.value = "D" }
                TimeSelectionButton("W", buttonWidth, selectedButton.value == "W", selectedDate) { selectedButton.value = "W" }
                TimeSelectionButton("M", buttonWidth, selectedButton.value == "M", selectedDate) { selectedButton.value = "M" }
                TimeSelectionButton("Y", buttonWidth, selectedButton.value == "Y", selectedDate) { selectedButton.value = "Y"}
            }
        }
    }
}

/**
 * A composable function that creates a clickable button with a custom background color and text.
 * @param text The text to be displayed on the button.
 * @param width The width of the button.
 * @param selected A boolean value that determines the background color of the button. If true, the background color is blue; otherwise, it's white.* @param selectedDate The date selected by the user.
 * This date is used to display the graph data for the selected date.
 * @param onClick A lambda function that is invoked when the button is clicked.
 */
@Composable
fun TimeSelectionButton(text: String, width: Dp, selected: Boolean = false, selectedDate: MutableState<Date>, onClick: () -> Unit) {
    val calendar = java.util.Calendar.getInstance()

    when (text) {
        "D" -> { calendar.time = Date() }
        "W" -> { calendar.add(java.util.Calendar.WEEK_OF_YEAR, -1) }
        "M" -> { calendar.set(java.util.Calendar.DAY_OF_MONTH, 1) }
        "Y" -> { calendar.add(java.util.Calendar.YEAR, -1) }
    }

    val newSelectedDate = calendar.time

    Box(
        modifier = Modifier
            .requiredWidth(width)
            .clickable(onClick = {
                onClick()
                selectedDate.value = newSelectedDate
                println("Selected date: $newSelectedDate")
            })
            .background(color = if (selected) colorResource(id = R.color.deepSkyBlueColor) else Color.White)
            .padding(top = 2.dp, bottom = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawLine(
                color = Color.LightGray,
                start = Offset(size.width - 1.dp.toPx(), 2.dp.toPx()),
                end = Offset(size.width - 1.dp.toPx(), size.height - 2.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
        })
        Text(
            text = text,
            color = if (selected) Color.Blue else Color.Black
        )
    }
}

/**
 * Constructs a row of navigation buttons for date selection.
 * @param selectedDate The date currently chosen.
 * @param selectedButton The button currently chosen. It represents the unit of time (Day, Week, Month, Year) to be changed.
 *
 * This function creates a row with two navigation buttons ("<" and ">") and a text field in the middle displaying the formatted date.
 * The navigation buttons update the selected date based on the chosen button and the direction indicated (-1 for "<", 1 for ">").
 */
@Composable
fun DateNavigationRow(selectedDate: MutableState<Date>, selectedButton: MutableState<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationButton(selectedDate, selectedButton, -1, "<")
        Text(
            text = getFormattedDate(selectedDate, selectedButton),
            textAlign = TextAlign.Center
        )
        NavigationButton(selectedDate, selectedButton, 1, ">")
    }
}

/**
 * Constructs a navigation button that modifies the selected date based on the selected button and direction.
 * @param selectedDate The currently selected date.
 * @param selectedButton The currently selected button.
 * @param direction The direction of navigation (forward or backward).
 * @param label The label to be displayed on the button.
 * @return A navigation button. When clicked, it updates the selected date according to the chosen
 * button and direction.
 */
@Composable
fun NavigationButton(selectedDate: MutableState<Date>, selectedButton: MutableState<String>, direction: Int, label: String) {
    Button(
        onClick = {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = selectedDate.value
            when (selectedButton.value) {
                "D" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, direction)
                "W" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, direction)
                "M" -> calendar.add(java.util.Calendar.MONTH, direction)
                "Y" -> calendar.add(java.util.Calendar.YEAR, direction)
            }
            selectedDate.value = calendar.time
        },
        colors = ButtonDefaults.buttonColors(
            Color.Transparent,
            contentColor = Color.Black
        )
    ) {
        Text(label)
    }
}

/**
 * A composable function that displays a card with a graph.
 * @param selectedDate The currently selected date.
 * @param selectedButton The currently selected button.
 * @return A card with a graph, total amount, and date navigation.
 */
@Composable
fun GraphCard(selectedDate: MutableState<Date>, selectedButton: MutableState<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = if (isLandscape()) 0.dp else 100.dp,
                end = if (isLandscape()) 70.dp else 0.dp
            )
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
            DateNavigationRow(selectedDate, selectedButton)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color =  Color(192, 226, 236),
                thickness = 1.dp
            )
            Text("Total ", textAlign = TextAlign.Start)
            Text("$hydrationLevel ml", textAlign = TextAlign.Start)
            WaterIntakeGraph()
        }
    }
}

/**
 * This function displays a water intake graph. The graph shows the user's current hydration level
 * and their hydration goal. Each increment in the graph represents 1000ml of water.
 */
@Composable
fun WaterIntakeGraph() {
    val graphData = hydrationLevel.toFloat()
    val goalData = hydrationGoal.value.toFloat()

    // Calculate the number of increments (each increment is 1000ml)
    val increments = (goalData / 1000).toInt()
    val remaining = goalData % 1000

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Spacer(modifier = Modifier.weight(1f))

        if (remaining > 0) {
            val currentIncrement = min(remaining, graphData - increments * 1000)

            Box(
                modifier = Modifier
                    .height((currentIncrement / goalData) * 200.dp)
                    .fillMaxWidth()
                    .background(if (currentIncrement > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent),
                contentAlignment = Alignment.TopCenter
            ) {
                if (currentIncrement > 0) {
                    DashedDivider()
                    Text("${remaining.toInt()} ml", color = Color.DarkGray)
                }
            }
        }

        for (i in increments - 1 downTo 0) {
            val currentIncrement = min(1000f, graphData - i * 1000)
            val remainingGoal = (i + 1) * 1000f

            Box(
                modifier = Modifier
                    .height((currentIncrement / goalData) * 200.dp)
                    .fillMaxWidth()
                    .background(
                        if (remainingGoal > 0 && currentIncrement > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                if (remainingGoal > 0 && currentIncrement > 0) {
                    DashedDivider()
                    Text("${remainingGoal.toInt()} ml", color = Color.DarkGray)
                }
            }
        }
    }
}


/**
 * This function creates a dashed divider line using the Canvas composable in Jetpack Compose.
 */
@Composable
fun DashedDivider() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
        }
        drawPath(
            path = path,
            color = Color.DarkGray,
            style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        )
    }
}

/**
 * This function formats the selected date based on the selected button value.
 * @param selectedDate The selected date to be formatted. It's a mutable state of Date.
 * @param selectedButton The selected button value which determines the format of the date. It's a mutable state of String.
 * The value can be "D" for day, "W" for week, "M" for month, and "Y" for year.
 * @return A string representation of the formatted date.
 */
fun getFormattedDate(selectedDate: MutableState<Date>, selectedButton: MutableState<String>): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())
    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    return when (selectedButton.value) {
        "W" -> {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = selectedDate.value
            val startOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -startOfWeek)
            val start = calendar.time
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 6)
            val end = calendar.time
            dateFormat.format(start) + " - " + dateFormat.format(end)
        }
        "M" -> {
            monthFormat.format(selectedDate.value)
        }
        "Y" -> {
            yearFormat.format(selectedDate.value)
        }
        else -> dateFormat.format(selectedDate.value)
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
