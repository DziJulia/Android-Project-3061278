package com.griffith.mybuddy

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.Float.max
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */

/**
 * A reference to the DatabaseManager instance.
 */
private lateinit var databaseManager: DatabaseManager

/**
 * A reference to the SQLiteDatabase instance.
 */
private lateinit var database: SQLiteDatabase

/**
 * A CoroutineScope instance with IO dispatcher.
 * This scope is used to perform database operations off the main thread.
 */
private val scope = CoroutineScope(Dispatchers.IO)

/**
 * `History` is an activity that allows the user to view their hydration history.
 * It extends `ComponentActivity`, which is a base class for activities
 * that enables composition as a means of creating your UI.
 *
 * In this activity, users can see their previous water intake records
 * and a calendar graph for a visual representation of their hydration history.
 */
class History : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling `setContentView(int)` to inflate the activity's UI,
     * using `findViewById(int)` to programmatically interact with widgets in the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in `onSaveInstanceState(Bundle)`. Note: Otherwise it is null.
     */
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

    /**
     * Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack,
     * with user input going to it.
     */
    override fun onResume() {
        super.onResume()

        // Get the instance of databaseManager
        databaseManager = DatabaseManagerSingleton.getInstance(this)
        // Re-open the database connection in onResume
        database = databaseManager.writableDatabase
    }

    /**
     * This function is called before the activity is destroyed.
     * It closes the database connection.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Close the database connection in onDestroy
        database.close()
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
        .padding(end = if (CommonFun.isLandscape()) 70.dp else 0.dp)
    ) {
        BoxWithConstraints {
            val buttonWidth = maxWidth / 4

            Row(modifier = Modifier.fillMaxWidth()) {
                TimeSelectionButton("D", buttonWidth, selectedButton.value == "D", selectedDate) {
                    selectedButton.value = "D"
                    selectedDate.value = Date()
                    AppVariables.period.value = "day"
                }
                TimeSelectionButton("W", buttonWidth, selectedButton.value == "W", selectedDate) {
                    selectedButton.value = "W"
                    AppVariables.period.value = "week"
                }
                TimeSelectionButton("M", buttonWidth, selectedButton.value == "M", selectedDate) {
                    selectedButton.value = "M"
                    AppVariables.period.value = "month"
                }
                TimeSelectionButton("Y", buttonWidth, selectedButton.value == "Y", selectedDate) {
                    selectedButton.value = "Y"
                    AppVariables.period.value = "year"
                }
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
        "W" -> { calendar.add(java.util.Calendar.WEEK_OF_YEAR, -1) }
        "M" -> { calendar.set(java.util.Calendar.DAY_OF_MONTH, 1) }
        "Y" -> { calendar.add(java.util.Calendar.YEAR, -1) }
    }

    AppVariables.newSelectedDate.value = calendar.time

    Box(
        modifier = Modifier
            .requiredWidth(width)
            .clickable(onClick = {
                onClick()
                selectedDate.value = AppVariables.newSelectedDate.value
                println("Selected date: ${AppVariables.newSelectedDate.value}")
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

        AppVariables.newSelectedDate.value = selectedDate.value
        Log.d("NEWDATE", " AppVariables.newSelectedDate.value: ${ AppVariables.newSelectedDate.value}")
    }
}

/**
 * Constructs a navigation button that modifies the selected date based on the selected button and direction.
 * User cannot select dates or months or years in future only in the past.
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
                "D" -> {
                    if (direction < 0 || calendar.get(java.util.Calendar.DAY_OF_YEAR) < java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)) {
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, direction)
                    }
                    AppVariables.period.value = "day"
                }
                "W" -> if (direction < 0 || calendar.get(java.util.Calendar.WEEK_OF_YEAR) < java.util.Calendar.getInstance().get(java.util.Calendar.WEEK_OF_YEAR)) {
                    calendar.add(java.util.Calendar.WEEK_OF_YEAR, direction)
                }
                "M" -> if (direction < 0 || calendar.get(java.util.Calendar.MONTH) < java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)) {
                    calendar.add(java.util.Calendar.MONTH, direction)
                }
                "Y" -> if (direction < 0 || calendar.get(java.util.Calendar.YEAR) < java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
                    calendar.add(java.util.Calendar.YEAR, direction)
                }
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
                bottom = if (CommonFun.isLandscape()) 0.dp else 100.dp,
                end = if (CommonFun.isLandscape()) 70.dp else 0.dp
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
            if(AppVariables.period.value == "day") {
                Text("Total ", textAlign = TextAlign.Start, style = TextStyle(fontSize = 22.sp))
            } else {
                Text("Average ", textAlign = TextAlign.Start)
            }
            Text(
                "${AppVariables.hydrationLevelData} ml",
                textAlign = TextAlign.Start,
                style = TextStyle(fontSize = 25.sp),
                color = Color.Blue
            )
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
    val scope = rememberCoroutineScope()
    var hydrationData by remember { mutableStateOf(listOf<Triple<Int, Int, Int>>()) }

    LaunchedEffect(key1 = Unit) {
        hydrationData = scope.async { fetchHydrationData() }.await()
    }

    AppVariables.hydrationGoalData = hydrationData.sumOf { it.first }
    AppVariables.hydrationLevelData = hydrationData.sumOf { it.second }
    Log.d("DATAGOAL", "AppVariables.hydrationGoalData: ${AppVariables.hydrationGoalData}")
    Log.d("DATAGOAL", "AppVariables.hydrationLevelData : ${AppVariables.hydrationLevelData}")
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Spacer(modifier = Modifier.weight(1f))

        val remaining = AppVariables.hydrationLevelData.toFloat()
        if (AppVariables.period.value == "year") {
            DisplayYearlyWaterIntake(hydrationData)
        }else if (remaining > 0) {
            DisplayRemainingWaterIntake(remaining)
        }
    }
}

/**
 * This function displays the remaining water intake in a graphical form.
 * @param remaining The remaining amount of water intake needed to reach the goal.
 *
 * The function calculates the current increment of water intake and displays it as a part of a Box composable.
 * The height of the Box is proportional to the ratio of the current increment to the total goal.
 * If the current increment is greater than 0, a dashed divider and a text displaying the remaining amount in milliliters are added to the Box.
 */
@Composable
private fun DisplayRemainingWaterIntake(remaining: Float) {
    val totalIncrements = (AppVariables.hydrationGoalData.toFloat() / 1000).toInt()
    val remainingIncrement = AppVariables.hydrationGoalData.toFloat() % 1000

    BoxWithConstraints(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        val height = (constraints.maxHeight.toFloat() / 2)

        // Hydration level
        Box(
            modifier = Modifier
                .height(((remaining / AppVariables.hydrationGoalData.toFloat()) * height).dp)
                .fillMaxWidth()
                .background(if (remaining > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent)
                .align(Alignment.BottomStart)
        )

        // Hydration goal
        for (i in totalIncrements downTo 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -((height / totalIncrements * i).dp))
            ) {
                DashedDivider()
                Text(
                    "${i * 1000} ml",
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (remainingIncrement > 0) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DashedDivider()
                Text(
                    "${remainingIncrement.toInt()} ml",
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * This function displays the yearly water intake in a graphical form.
 * @param hydrationData The list of hydration data fetched from the database. Each element is a Triple where the first element is the hydration data, the second element is the hydration goal, and the third element is the month index.
 *
 * The function calculates the total hydration data and goal for each month and displays them as a part of a Box composable in a Row. The height of each Box is proportional to the ratio of the hydration data to the total goal for the corresponding month.
 * If the hydration data for a month is greater than 0, a dashed divider and a text displaying the hydration data in milliliters are added to the Box.
 * The month labels are displayed in a separate Row at the bottom of the Column.
 */
@Composable
private fun DisplayYearlyWaterIntake(hydrationData: List<Triple<Int, Int, Int>>) {
    val monthData = Array(12) { 0f }
    val goalDataArray = Array(12) { 0f }

    // Assign the hydration data and goal to the corresponding month
    hydrationData.forEach { triple ->
        val monthIndex = triple.third - 1
        monthData[monthIndex] += triple.second.toFloat()
        goalDataArray[monthIndex] += triple.first.toFloat()
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Constants.MONTHS.indices.forEach { index ->
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    DisplayMonthlyWaterIntake(monthData[index], goalDataArray[index])
                    Text(Constants.MONTHS[index])
                }
            }
        }
    }
}

/**
 * A composable function that displays the monthly water intake.
 * @param monthData The amount of water consumed in the month.
 * @param goalData The goal amount of water to be consumed in the month.
 *
 * The function creates a Column with the month name and a Box representing the water intake.
 * The height of the Box is proportional to the ratio of `monthData` to `goalData`.
 * If `monthData` is greater than 0, the Box is filled with a color (deepSkyBlueColor)
 * Otherwise, the Box is transparent.
 */
@Composable
private fun DisplayMonthlyWaterIntake(monthData: Float, goalData: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height((monthData / max(1f, goalData)) * 200.dp)
                .width(20.dp)
                .background(if (monthData > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent)
        )
    }
}

/**
 * Fetches hydration data from the database for a specific period.
 *
 * @return A list of Triple objects. Each Triple contains three Int values.
 *
 * The function fetches hydration data for the period specified in `AppVariables.period.value`.
 * The data is fetched for the user with the email address specified in `AppVariables.emailAddress.value`.
 * The date for which the data is fetched is specified in `AppVariables.newSelectedDate.value`.
 * The date is formatted using `AppVariables.sdf.format()`.
 *
 * The hydration data is fetched using `databaseManager.fetchHydrationDataForPeriod()`.
 */
private suspend fun fetchHydrationData(): List<Triple<Int, Int, Int>> {
    return withContext(scope.coroutineContext) {
        databaseManager.fetchHydrationDataForPeriod(
            AppVariables.emailAddress.value,
            AppVariables.period.value,
            AppVariables.sdf.format(AppVariables.newSelectedDate.value).toString()
        )
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
