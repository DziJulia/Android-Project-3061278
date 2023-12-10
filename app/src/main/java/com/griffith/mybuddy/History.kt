package com.griffith.mybuddy

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.lang.Float.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */

private lateinit var databaseManager: DatabaseManager
private lateinit var database: SQLiteDatabase
class History : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
    val calendar = Calendar.getInstance()

    when (text) {
        "W" -> { calendar.add(Calendar.WEEK_OF_YEAR, -1) }
        "M" -> { calendar.set(Calendar.DAY_OF_MONTH, 1) }
        "Y" -> { calendar.add(Calendar.YEAR, -1) }
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
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate.value
            when (selectedButton.value) {
                "D" -> {
                    if (direction < 0 || calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                        calendar.add(Calendar.DAY_OF_YEAR, direction)
                    }
                    AppVariables.period.value = "day"
                }
                "W" -> if (direction < 0 || calendar.get(Calendar.WEEK_OF_YEAR) < Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) {
                    calendar.add(Calendar.WEEK_OF_YEAR, direction)
                }
                "M" -> if (direction < 0 || calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH) < Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance().get(Calendar.MONTH)) {
                    calendar.add(Calendar.MONTH, direction)
                }
                "Y" -> if (direction < 0 || calendar.get(Calendar.YEAR) < Calendar.getInstance().get(Calendar.YEAR)) {
                    calendar.add(Calendar.YEAR, direction)
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraphCard(selectedDate: MutableState<Date>, selectedButton: MutableState<String>) {
    val text = if (AppVariables.period.value == "day") "Total " else "Average "

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 100.dp
            )
            .wrapContentHeight(Alignment.CenterVertically),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .border(2.dp, colorResource(id = R.color.graphColor)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DateNavigationRow(selectedDate, selectedButton)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color =  colorResource(id = R.color.graphColor),
                thickness = 1.dp
            )
            Text(text, textAlign = TextAlign.Start)
            Text(
                "${AppVariables.hydrationAverageData} ml",
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterIntakeGraph() {
    val hydrationData = fetchHydrationData()
    //Added a check to see if hydrationData has changed before recalculating the sums and averages.
    //This way, if hydrationData hasn’t changed, you avoid doing unnecessary work.
    if (hydrationData != AppVariables.hydrationData) {
        AppVariables.hydrationData.value = hydrationData
        AppVariables.hydrationGoalData = hydrationData.sumOf { it.first }
        AppVariables.hydrationLevelData = hydrationData.sumOf { it.second }
        AppVariables.hydrationAverageData = if (hydrationData.isNotEmpty()) AppVariables.hydrationLevelData / hydrationData.size else 0
        AppVariables.hydrationGoalAverageData = if (hydrationData.isNotEmpty()) hydrationData.maxOf { it.first } else 0
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Spacer(modifier = Modifier.weight(1f))

        when(AppVariables.period.value) {
            "week" -> {
                GraphDivider()
                DisplayWaterIntake(hydrationData, true)
            }
            "month" -> {
                GraphDivider()
                DisplayWaterIntake(hydrationData, false)
            }
            "year" -> {
                GraphDivider()
                DisplayYearlyWaterIntake(hydrationData)
            }
            else -> {
                DisplayWaterIntakeForDay()
            }
        }
    }
}

/**
 * This function displays the water intake for a day in a graphical form.
 *
 * The function calculates the current increment of water intake and displays it as a part of a Box composable.
 * The height of the Box is proportional to the ratio of the current increment to the total goal.
 * If the current increment is greater than 0, a dashed divider and a text displaying the remaining amount in milliliters are added to the Box.
 */
@Composable
private fun DisplayWaterIntakeForDay() {
    val heightRatio = remember { mutableFloatStateOf(0f) }
    val half = remember { mutableIntStateOf(0) }

    BoxWithConstraints(
        modifier = Modifier
            .height(240.dp)
            .fillMaxWidth()
    ) {
        val height = constraints.maxHeight.toFloat() / 2

        LaunchedEffect(AppVariables.hydrationLevelData) {
            if (AppVariables.hydrationGoalData == 0 || AppVariables.hydrationLevelData == 0) {
                heightRatio.floatValue = 0f
                AppVariables.hydrationAverageData = 0
                half.intValue = 0
                return@LaunchedEffect
            }

            heightRatio.floatValue = (AppVariables.hydrationLevelData / AppVariables.hydrationGoalData.toFloat()) * height
            half.intValue = heightRatio.floatValue.toInt() / 2
        }

        // Hydration level
        Box(
            modifier = Modifier
                .height(heightRatio.floatValue.dp)
                .fillMaxWidth()
                .background(if (AppVariables.hydrationLevelData.toFloat() > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent)
                .align(Alignment.BottomStart)
        )

        // Hydration goal
        for (i in heightRatio.floatValue.toInt() downTo 1) {
            val lineHeight = if (i == half.intValue) (height/2)-20 else height
            Log.d("HEIGHT OF LINE", "  lineHeight: $lineHeight")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -lineHeight.dp)
            ) {
                if (i == heightRatio.floatValue.toInt() ) {
                    DayGraphDivider()
                } else if (i == half.intValue)
                {
                    DashedDivider()
                    Text(
                        "${AppVariables.hydrationGoalData / 2} ml",
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}

/**
 * A Composable function that displays the water intake based on the provided hydration data.
 *
 * This function creates a UI layout that represents each day of the current period (week or month) with the corresponding water intake.
 * Each day is represented by a box, and if hydration data is available for that day, the water intake is displayed.
 *
 * @param hydrationData A list of Triple objects, where each Triple represents a day's hydration data.
 * The first element of the Triple is the total water intake, the second is the goal, and the third is the date.
 * @param isWeekly A boolean indicating whether the display is for a week (true) or a month (false).
 * @RequiresApi(Build.VERSION_CODES.O) This function requires API level 26 (Android 8.0, Oreo) or higher.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DisplayWaterIntake(hydrationData: List<Triple<Int, Int, Int>>, isWeekly: Boolean) {
    val column = if (isWeekly) 25 else 10
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Iterate over all days of the period
            hydrationData.forEachIndexed { index, triple ->
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    DisplayColumnWaterIntake(triple.second.toFloat(), triple.first.toFloat(), column)
                    if (isWeekly) {
                        Text(Constants.DAYS[index])
                    } else if (Constants.DAYS_FOR_MONTH.contains((index + 1).toString())) {
                        Text((index + 1).toString())
                    }
                }
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
    for ((index, value) in monthData.withIndex()) {
        Log.d("hydrationGoalAverageData", "Value at index $index : $value")
    }
    AppVariables.hydrationGoalAverageData = (goalDataArray.maxOrNull() ?: 0).toInt()
    Log.d("hydrationGoalAverageData", "  hydrationGoalAverageData: $hydrationData")
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
                    DisplayColumnWaterIntake(monthData[index], goalDataArray[index], 20)
                    Text(Constants.MONTHS[index])
                }
            }
        }
    }
}

/**
 * A composable function that displays the column for water intake.
 * @param timeUnitValue The amount of water consumed in the dat/month.
 * @param goalData The goal amount of water to be consumed in the date/month.
 * @param boxWidth The width of each column
 *
 * The function creates a Column with the month name and a Box representing the water intake.
 * The height of the Box is proportional to the ratio of `timeUnitValue` to `goalData`.
 * If `timeUnitValue` is greater than 0, the Box is filled with a color (deepSkyBlueColor)
 * Otherwise, the Box is transparent.
 */
@Composable
private fun DisplayColumnWaterIntake(timeUnitValue: Float, goalData: Float, boxWidth: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height(calculateBoxHeight(timeUnitValue, goalData))
                .width(boxWidth.dp)
                .padding(bottom = 18.dp, end = 2.dp)
                .background(if (timeUnitValue > 0) colorResource(id = R.color.deepSkyBlueColor) else Color.Transparent)
        )
    }
}

/**
 * Calculates the height of a Box based on the given time unit value and goal data.
 *
 * The height is determined by the formula: (timeUnitValue / max(1f, goalData)) * 280.dp,
 * ensuring that the division is safe by avoiding division by zero when goalData is 0.
 * @param timeUnitValue The value representing the time unit.
 * @param goalData The goal data used to calculate the height.
 * @return The calculated height as a Dp (Density-independent pixels).
 */
@Composable
private fun calculateBoxHeight(timeUnitValue: Float, goalData: Float): Dp {
    // Check if goalData is 0 to avoid division by zero
    return if (goalData != 0f) {
        ((timeUnitValue / max(1f, goalData)) * 280).dp
    } else {
        0.dp
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
private fun fetchHydrationData(): List<Triple<Int, Int, Int>> {
    Log.d("DATABASE", "  fetch:")
    return databaseManager.fetchHydrationDataForPeriod(
        AppVariables.emailAddress.value,
        AppVariables.period.value,
        AppVariables.sdf.format(AppVariables.newSelectedDate.value).toString()
    )
}

/**
 * This function creates a divider line for Graph
 */
@Composable
fun DayGraphDivider() {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            "${AppVariables.hydrationGoalAverageData} ml",
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 5.dp)
        )

        Divider(
            color = colorResource(id = R.color.graphColor),
            modifier = Modifier
                .padding(horizontal = 10.dp),
            thickness = 3.dp
        )
    }
}

/**
 * This function creates a divider line for Graph
 */
@Composable
fun GraphDivider() {
    Text(
        "${AppVariables.hydrationGoalAverageData} ml",
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 5.dp)
    )

    Divider(
        color = colorResource(id = R.color.graphColor),
        modifier = Modifier
            .padding(horizontal = 10.dp),
        thickness = 3.dp
    )
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
            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.time = selectedDate.value
            val startOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek
            calendar.add(Calendar.DAY_OF_YEAR, -startOfWeek)
            val start = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 6)
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
