package com.griffith.mybuddy

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.util.regex.Pattern

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */
const val ERR_LEN = "Password must have at least eight characters!"
const val ERR_WHITESPACE = "Password must not contain whitespace!"
const val ERR_DIGIT = "Password must contain at least one digit!"
const val ERR_UPPER = "Password must have at least one uppercase letter!"
const val ERR_SPECIAL = "Password must have at least one special character, s uch as: _%-=+#@"
const val ERR_NOT_MATCH = "Password doesn't match!"
const val ERR_NOT_EMPTY = "Email cannot be empty!"
const val ERR_NOT_VALID = "Email is not valid!"

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppNavigation()
        }
    }

    /**
     * Responds to changes in the device's configuration, such as when the orientation is changed.
     * @param newConfig The new configuration that the system has changed to.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContent {
            MyAppNavigation()
        }
    }
}

val deepSkyBlueColor = Color(0xFF00BFFF)
val activityBackground = Modifier.background(color = Color(232, 244, 248))
val buttonBackgroundColor = Color(192, 219, 236)

/**
 * This function sets up the UI for the login screen. It adjusts the layout
 * based on the orientation of the device.
 */
@Composable
fun SetupUI(navController: NavController) {
    val context = LocalContext.current
    val emailAddress = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    if (!isLandscape()) {
        Box(modifier = Modifier.fillMaxSize().offset(y = 50.dp)) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopCenter)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Welcome!",
                    fontSize = 40.sp,
                    color = deepSkyBlueColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(
                    onClick = { navController.navigate("register") },
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Sing in to continue")
                }
                // TODO need to add validation if email exist in the database
                NameField(emailAddress)
                // TODO need to add validation if password match with the database one for email
                PasswordField(password)
                Button(
                    onClick = { /* TODO Handle forgot password*/ },
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Forgot Password")
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Button(
                    onClick = {
                        val intent = Intent(context, CurrentHydration::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        deepSkyBlueColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp)
                ) {
                    Text(text= "Login", fontSize = 25.sp)
                }
            }
        }
    } else if (isLandscape()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .offset(x = 50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(300.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = { navController.navigate("register") },
                        colors = ButtonDefaults.buttonColors(
                            Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Sing in to continue")
                    }
                    // TODO need to add validation if email exist in the database
                    NameField(emailAddress)
                    // TODO need to add validation if password match with the database one for email
                    PasswordField(password)
                    Button(
                        onClick = { /* TODO Handle forgot password*/ },
                        colors = ButtonDefaults.buttonColors(
                            Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Forgot Password")
                    }
                    Button(onClick = {
                        val intent = Intent(context, CurrentHydration::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Login")
                    }
                }
            }
        }
    }
}

/**
 * A composable function for managing navigation within the application.
 */
@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { SetupUI(navController) }
        composable("register") { RegistrationScreen(navController) }
    }
}


/**
 * A composable function that represents the Registration Screen in the application.
 * @param navController The NavController that this screen uses for navigation.
 */
@Composable
fun RegistrationScreen(navController: NavController) {
    val context = LocalContext.current
    val emailAddress = remember { mutableStateOf("") }
    val password1 = remember { mutableStateOf("") }
    val password2 = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                Color.Transparent,
                contentColor = Color.Black
            )
        ) {
            Text("Back to Login")
        }
        NameField(emailAddress)
        PasswordField(password1)
        PasswordField(password2)
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                // Validation of input
                // TODO need to add validation if email exist in the database
                if (emailAddress.value.isEmpty()){
                    Toast.makeText(context, ERR_NOT_EMPTY, Toast.LENGTH_SHORT).show()
                }
                else if (!emailAddress.value.isValidEmail()){
                    Toast.makeText(context, ERR_NOT_VALID, Toast.LENGTH_SHORT).show()
                }
                else if (password1.value.isValidatePassword().isNotEmpty()){
                    Toast.makeText(context, password1.value.isValidatePassword(), Toast.LENGTH_SHORT).show()
                }
                else if (password2.value.isValidatePassword().isNotEmpty()){
                    Toast.makeText(context, password2.value.isValidatePassword(), Toast.LENGTH_SHORT).show()
                }
                else if (password1.value != password2.value) {
                    Toast.makeText(context, ERR_NOT_MATCH, Toast.LENGTH_SHORT).show()
                } else {
                    // TODO Handle registration
                }
            },
           colors = ButtonDefaults.buttonColors(
               deepSkyBlueColor,
               contentColor = Color.Black
            ),
            modifier = Modifier.height(50.dp).width(200.dp)
        ) {
            Text("Register")
        }
    }
}

/**
 * A composable function to create a name field.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NameField(email: MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = email.value,
        onValueChange = { newValue ->
            // Prevent newline characters from being added to the text
            if (!newValue.contains('\n')) {
                email.value = newValue
            }
        },
        label = { Text("Email") },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            containerColor = buttonBackgroundColor
        ),
        // Hide keyboard when enter key is pressed
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
}

/**
 * Validates the password based on certain conditions.
 *
 * This function checks the following conditions:
 * - The password must be at least 8 characters long.
 * - The password must not contain any whitespace.
 * - The password must contain at least one digit.
 * - The password must contain at least one uppercase letter.
 * - The password must contain at least one special character.
 * @return A string containing an error message if the password is invalid. If
 * the password is valid, it returns an empty string.
 */
fun String.isValidatePassword(): String {
    return when {
        length < 8 -> ERR_LEN
        any { it.isWhitespace() } -> ERR_WHITESPACE
        none { it.isDigit() } -> ERR_DIGIT
        none { it.isUpperCase() } -> ERR_UPPER
        none { !it.isLetterOrDigit() } -> ERR_SPECIAL
        else -> ""
    }
}

/**
 * Checks if the given string is a valid email address.
 *
 * The function uses a regular expression to check if the string is a valid email address.
 * The regular expression checks for the following conditions:
 * - The string must start with one or more alphanumeric characters, and can include '+', '.', '_', '%', '-', and '+'.
 * - This is followed by an '@' symbol.
 * - Then, there must be one or more alphanumeric characters, which can include a '-'.
 * - This is followed by one or more groups that:
 *   - start with a '.'
 *   - then have one or more alphanumeric characters, which can include a '-'.
 * @return `true` if the string matches the regular expression, `false` otherwise.
 */
fun String.isValidEmail(): Boolean {
    val emailAddress = Pattern.compile(
        "[a-zA-Z0-9+_.%-]{1,256}" +
                "@" +
                "[a-zA-Z0-9-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9-]{0,25}" +
                ")+"
    )

    return emailAddress.matcher(this).matches()
}

/**
 * A composable function that creates a password field with a toggle for password visibility.
 * The keyboard is hidden when the user clicks outside the TextField or presses the enter key.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(password: MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordVisibility = remember { mutableStateOf(false) }

    TextField(
        value = password.value,
        onValueChange = { newValue ->
            // Prevent newline characters from being added to the text
            if (!newValue.contains('\n')) {
                password.value = newValue
            }
        },
        label = { Text("Password") },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            containerColor =  buttonBackgroundColor
        ),
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                passwordVisibility.value = !passwordVisibility.value
            }) {
                if (passwordVisibility.value) EyeOpenIcon() else EyeClosedIcon()
            }
        },
        // Hide keyboard when enter key is pressed
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
}

/**
 * This function uses the `Icon` composable from the Material Design library to display an open eye icon.
 * The icon is loaded from a drawable resource with the id `R.drawable.open_eye`.
 * The size of the icon is set to 20.dp using a `Modifier`.
 */
@Composable
fun EyeOpenIcon() {
    val imageVector = painterResource(id = R.drawable.open_eye)
    Icon(
        painter = imageVector,
        contentDescription = "Open Eye Icon",
        modifier = Modifier.size(20.dp)
    )
}

/**
 * This function uses the `Icon` composable from the Material Design library to display a closed eye icon.
 * The icon is loaded from a drawable resource with the id `R.drawable.closed_eye`.
 */
@Composable
fun EyeClosedIcon() {
    val imageVector = painterResource(id = R.drawable.closed_eye)
    Icon(
        painter = imageVector,
        contentDescription = "Closed Eye Icon"
    )
}


/**
 * This is a composable function that creates a LogOut button. When the button is clicked,
 * an AlertDialog is shown to the user. After a delay, the user is redirected to the Login screen.
 * @param modifier Modifier for styling the LogOut button. Default value is Modifier.
 */
@Composable
fun LogOutButton(modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Log Out") },
            text = { Text(text = "You have been successfully logged out!") },
            confirmButton = { Row { } },
            properties = DialogProperties(dismissOnClickOutside = false)
        )

        LaunchedEffect(showDialog.value) {
            delay(2000)
            showDialog.value = false
            val intent = Intent(context, Login::class.java)
            context.startActivity(intent)
        }
    }

    Button(
        onClick = { showDialog.value = true },
        modifier = modifier.padding(top = 5.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
    ) {
        IconImage(
            R.drawable.logout,
            "Logout",
            Color.Black)
    }
}

//Menu for the app
/**
 * @Composable function to create an image icon.
 * @param resourceId The resource id of the image.
 * @param contentDescription The content description of the image.
 * @param color The color filter to apply to the image.
 */
@Composable
fun IconImage(resourceId: Int, contentDescription: String, color: Color) {
    val size = if (contentDescription == "Logout") 24.dp else 60.dp

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.tint(color)
    )
}

/**
 * @Composable function to create a button item in the row.
 * @param text The text to display on the button.
 * @param destination The destination class when the button is clicked.
 * @param context The current context.
 * @param isSelected Whether the button is currently selected.
 * @param onSelected The action to perform when the button is selected.
 */
@Composable
fun ButtonRowItem(text: String, destination: Class<*>, context: android.content.Context, isSelected: Boolean, onSelected: () -> Unit) {
    val iconColor = if (isSelected) deepSkyBlueColor else Color.Black
    val iconResource = when (text) {
        "C" -> R.drawable.drop
        "P" -> R.drawable.profile
        "H" -> R.drawable.history
        else -> null
    }

    Button(
        onClick = {
            val intent = Intent(context, destination)
            context.startActivity(intent)
            onSelected()
        },
        shape = CircleShape,
        modifier = Modifier.size(70.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (iconResource != null) {
            IconImage(iconResource, text, iconColor)
        } else {
            Text(text)
        }
    }
}


// A mutable state variable that holds the value of the currently
// selected button. It is initially set to "C".
var selectedButton by mutableStateOf("C")

/**
 * Function that displays a row of buttons, with dynamic arrangements based on the screen orientation.
 */
@Composable
fun MyButtonsRow() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (isLandscape()) Arrangement.Center else Arrangement.Bottom,
        horizontalAlignment = if (isLandscape()) Alignment.End else Alignment.CenterHorizontally
    ) {
        if (!isLandscape()) {
            Divider(color = deepSkyBlueColor, thickness = 3.dp)
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AddSpacer(30.dp)
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                AddSpacer(10.dp)
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                AddSpacer(10.dp)
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
                AddSpacer(30.dp)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxHeight().background(Color.White),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                AddSpacer(30.dp)
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
                AddSpacer(5.dp)
            }
        }
    }
}

