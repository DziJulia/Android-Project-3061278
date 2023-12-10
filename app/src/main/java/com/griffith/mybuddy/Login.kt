package com.griffith.mybuddy

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.colorResource
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */
private lateinit var databaseManager: DatabaseManager
private lateinit var database: SQLiteDatabase

/**
 * `Login` is an activity that serves as the entry point for user authentication.
 * It extends `ComponentActivity`, which is a base class for activities
 * that enables composition as a means of creating your UI.
 */
class Login : ComponentActivity() {

    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling `setContentView(int)` to inflate the activity's UI,
     * using `findViewById(int)` to programmatically interact with widgets in the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in `onSaveInstanceState(Bundle)`. Note: Otherwise it is null.
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContent {
            MyAppNavigation()
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
     * Called when the system is about to start resuming a previous activity.
     * This is typically used to commit unsaved changes to persistent data,
     * stop animations and other things that may be consuming CPU, etc.
     * Implementations of this method must be very quick because the next
     * activity will not be resumed until this method returns.
     *
     * @RequiresApi(Build.VERSION_CODES.O) - This annotation informs the
     * compiler that this method should only be called on the specified API
     * level (in this case, API level 26, which corresponds to Android 8.0 Oreo) or higher.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()

        //Only update on registration
        if(AppVariables.registration) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userId = databaseManager.insertUser(
                        AppVariables.emailAddressRegistration.value,
                        AppVariables.password1.value
                    )

                    if (userId != -1L) {
                        databaseManager.insertUserWithProfile(
                            AppVariables.emailAddressRegistration.value
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Log the exception or display an error message as needed
                }
            }
        }

        AppVariables.registration = false
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
 * This function sets up the UI for the login screen. It adjusts the layout
 * based on the orientation of the device.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupUI(navController: NavController) {
    val context = LocalContext.current
    val password = remember { mutableStateOf("") }

    if (!CommonFun.isLandscape()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .offset(y = 50.dp)) {
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
                    color = colorResource(id = R.color.deepSkyBlueColor),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(
                    onClick = { navController.navigate("register") },
                    colors = CommonFun.transparentButtonColors()
                ) {
                    Text("Sing in to continue")
                }
                NameField(AppVariables.emailAddress)
                PasswordField(password)
                ForgotPasswordButton(navController)
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Button(
                    onClick = {
                        if (AppVariables.emailAddress.value.isEmpty() || password.value.isEmpty()) {
                            // Either email or password is empty, show an error message
                            Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                        } else if (databaseManager.verifyLogin(AppVariables.emailAddress.value, password.value)) {
                            // Verification successful, proceed to CurrentHydration activity
                            val intent = Intent(context, CurrentHydration::class.java)
                            context.startActivity(intent)
                        }else {
                            // Verification failed, handle the error (e.g., show an error message)
                            // You can also clear the password field or take other actions as needed
                            // For simplicity, I'm using a Toast to show an error message.
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = CommonFun.customButtonColors(),
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp)
                ) {
                    Text(text= "Login", fontSize = 25.sp)
                }
            }
        }
    } else if (CommonFun.isLandscape()) {
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
                        colors = CommonFun.transparentButtonColors()
                    ) {
                        Text("Sing in to continue")
                    }
                    NameField(AppVariables.emailAddress)
                    PasswordField(password)
                    ForgotPasswordButton(navController)
                    Button(
                        onClick = {
                            if (databaseManager.verifyLogin(AppVariables.emailAddress.value, password.value)) {
                                // Verification successful, proceed to CurrentHydration activity
                                val intent = Intent(context, CurrentHydration::class.java)
                                context.startActivity(intent)
                            }else {
                                // Verification failed, handle the error (e.g., show an error message)
                                // You can also clear the password field or take other actions as needed
                                // For simplicity, I'm using a Toast to show an error message.
                                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = CommonFun.customButtonColors()
                    ) {
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { SetupUI(navController) }
        composable("register") { RegistrationScreen(navController) }
        composable("resetPassword") { ResetPasswordScreen(navController) }
    }
}


/**
 * A composable function that represents the Registration Screen in the application.
 * @param navController The NavController that this screen uses for navigation.
 */
@Composable
fun RegistrationScreen(navController: NavController) {
    val context = LocalContext.current

    CommonFun.LogoColumn {
        Button(
            onClick = { navController.popBackStack() },
            colors = CommonFun.transparentButtonColors()
        ) {
            Text(text= "Back to Login", fontSize = 20.sp)
        }
        NameField(AppVariables.emailAddressRegistration)
        PasswordField(AppVariables.password1)
        PasswordField(AppVariables.password2)
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                // Validation of input
                if (databaseManager.isEmailPresent(AppVariables.emailAddressRegistration.value)){
                    Toast.makeText(context, Constants.ERR_EXIST, Toast.LENGTH_SHORT).show()
                }
                else if(AppVariables.emailAddressRegistration.value.isEmpty()) {
                    Toast.makeText(context, Constants.ERR_NOT_EMPTY, Toast.LENGTH_SHORT).show()
                }
                else if(!AppVariables.emailAddressRegistration.value.isValidEmail()){
                Toast.makeText(context, Constants.ERR_NOT_VALID, Toast.LENGTH_SHORT).show()
                }
                else if (validatePassword(context)) {
                    AppVariables.registration = true
                    AppVariables.emailAddress = AppVariables.emailAddressRegistration
                    val intent = Intent(context, CurrentHydration::class.java)
                    context.startActivity(intent)
                }
            },
           colors = CommonFun.customButtonColors(),
            modifier = Modifier
                .height(50.dp)
                .width(200.dp)
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
        colors = CommonFun.textFieldColors(),
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
        length < 8 -> Constants.ERR_LEN
        any { it.isWhitespace() } -> Constants.ERR_WHITESPACE
        none { it.isDigit() } -> Constants.ERR_DIGIT
        none { it.isUpperCase() } -> Constants.ERR_UPPER
        none { !it.isLetterOrDigit() } -> Constants.ERR_SPECIAL
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
        colors = CommonFun.textFieldColors(),
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
            properties = DialogProperties(dismissOnClickOutside = false),
            containerColor = CommonFun.alertDialogParameters()["containerColor"] as Color,
            titleContentColor = CommonFun.alertDialogParameters()["titleContentColor"] as Color,
            modifier = CommonFun.alertDialogParameters()["modifier"] as Modifier
        )

        LaunchedEffect(showDialog.value) {
            delay(2000)
            showDialog.value = false
            AppVariables.emailAddress = mutableStateOf("")
            Log.d("EMAIL", "hydrationLevelApp: ${AppVariables.emailAddress}")
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
fun ButtonRowItem(text: String, destination: Class<*>, context: Context, isSelected: Boolean, onSelected: () -> Unit) {
    val iconColor = if (isSelected) colorResource(id = R.color.deepSkyBlueColor) else Color.Black
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

/**
 * Function that displays a row of buttons, with dynamic arrangements based on the screen orientation.
 */
@Composable
fun MyButtonsRow() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (CommonFun.isLandscape()) Arrangement.Center else Arrangement.Bottom,
        horizontalAlignment = if (CommonFun.isLandscape()) Alignment.End else Alignment.CenterHorizontally
    ) {
        if (!CommonFun.isLandscape()) {
            Divider(
                color = colorResource(id = R.color.deepSkyBlueColor),
                thickness = 3.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AddSpacer(30.dp)
                ButtonRowItem("C", CurrentHydration::class.java, context, AppVariables.selectedButtonMenu == "C") { AppVariables.selectedButtonMenu = "C" }
                AddSpacer(10.dp)
                ButtonRowItem("P", Profile::class.java, context, AppVariables.selectedButtonMenu == "P") { AppVariables.selectedButtonMenu = "P" }
                AddSpacer(10.dp)
                ButtonRowItem("H", History::class.java, context, AppVariables.selectedButtonMenu == "H") { AppVariables.selectedButtonMenu = "H" }
                AddSpacer(30.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.White),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                AddSpacer(30.dp)
                ButtonRowItem("C", CurrentHydration::class.java, context, AppVariables.selectedButtonMenu == "C") { AppVariables.selectedButtonMenu = "C" }
                ButtonRowItem("P", Profile::class.java, context, AppVariables.selectedButtonMenu == "P") { AppVariables.selectedButtonMenu = "P" }
                ButtonRowItem("H", History::class.java, context, AppVariables.selectedButtonMenu == "H") { AppVariables.selectedButtonMenu = "H" }
                AddSpacer(5.dp)
            }
        }
    }
}

/**
 * A composable function that displays a dialog for password recovery.
 *
 * The dialog includes a text field for the user to enter their email address and two buttons: "Send Email" and "Cancel".
 * When the "Send Email" button is clicked, it calls the `onSendEmail` function with the entered
 * email address as a parameter and then dismisses the dialog.
 * When the "Cancel" button is clicked, it simply dismisses the dialog.
 *
 * @param onDismiss A function to be called when the dialog is dismissed.
 * @param onSendEmail A function to be called when the "Send Email" button is clicked.
 * It takes the entered email address as a parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPopup(onDismiss: () -> Unit, onSendEmail: (String) -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommonFun.alertDialogParameters()["containerColor"] as Color,
        titleContentColor = CommonFun.alertDialogParameters()["titleContentColor"] as Color,
        modifier = CommonFun.alertDialogParameters()["modifier"] as Modifier,
        title = { Text("Forgot Password") },
        text = {
            Column {
                Text("Please enter your email address:")
                TextField(
                    value = AppVariables.forgotEmailAddress.value,
                    onValueChange = { AppVariables.forgotEmailAddress.value = it },
                    label = { Text("Email Address") },
                    colors = CommonFun.textFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (databaseManager.isEmailPresent(AppVariables.forgotEmailAddress.value)) {
                        onSendEmail(AppVariables.forgotEmailAddress.value)
                        onDismiss()
                    } else {
                        // Display an error message
                        Toast.makeText(context, "Email not found", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = CommonFun.customButtonColors()
            ) {
                Text("Send Email")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = CommonFun.customButtonColors()
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * A composable function that displays a "Forgot Password" button.
 * When the button is clicked, it sets `AppVariables.isForgotPasswordPopupVisible` to `true`,
 * which triggers the `ForgotPasswordPopup` to be displayed.
 */
@Composable
fun ForgotPasswordButton(navController: NavController) {
    var tokenVerified by remember { mutableStateOf(false) }

    Button(
        onClick = { AppVariables.isForgotPasswordPopupVisible = true },
        colors = CommonFun.transparentButtonColors()
    ) {
        Text("Forgot Password")
    }
    if (AppVariables.isForgotPasswordPopupVisible) {
        ForgotPasswordPopup(
            onDismiss = { AppVariables.isForgotPasswordPopupVisible = false },
            onSendEmail = { emailAddress ->
                val emailSender = EmailSender()
                emailSender.sendEmail(emailAddress)
                tokenVerified = true
            }
        )
    }

    if (tokenVerified) {
        TokenVerificationDialog(
            onDismiss = { tokenVerified = false },
            onTokenVerified = {
                navController.navigate("resetPassword")
            },
        )
    }
}

/**
 * A composable function that displays a screen for resetting the password.
 * This screen includes two password fields and an "Update Password" button.
 * When the button is clicked, it validates the entered passwords, updates the password in the database,
 * and navigates back to the login screen.
 *
 * @param navController The NavController used for navigation.
 * @requires The minimum API level required is O (API 26).
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResetPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    CommonFun.LogoColumn {
        Text(
            text = "Please enter your new password:",
            fontSize = 20.sp
        )

        PasswordField(AppVariables.password1)
        PasswordField(AppVariables.password2)

        Button(
            onClick = {
               if (validatePassword(context)) {
                   // Update the password in your database
                   databaseManager.updatePassword(AppVariables.forgotEmailAddress.value, AppVariables.password1.value)
                   // Navigate back to the login screen
                   navController.navigate("login")
                }
            },
            colors = CommonFun.customButtonColors()
        ) {
            Text("Update Password")
        }
    }
}

/**
 * Displays a dialog for token verification. The dialog includes a text field for the user to enter a token, a countdown timer, and a button to verify the token.
 *
 * @param onDismiss A callback function that gets called when the dialog is dismissed.
 * @param onTokenVerified A callback function that gets called when the entered token matches the expected token.
 *
 * @return Unit This function does not return a value. It displays a dialog and calls the appropriate callback function based on the user's actions.
 * @OptIn(ExperimentalMaterial3Api::class) This annotation indicates that this function uses APIs that are marked as experimental in the Material 3 library.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenVerificationDialog(onDismiss: () -> Unit, onTokenVerified: () -> Unit) {
    val context = LocalContext.current
    var enteredToken by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommonFun.alertDialogParameters()["containerColor"] as Color,
        titleContentColor = CommonFun.alertDialogParameters()["titleContentColor"] as Color,
        modifier = CommonFun.alertDialogParameters()["modifier"] as Modifier,
        title = { Text("Verify Code") },
        text = {
            Column {
                Text("Please enter the code sent to your email:")
                TextField(
                    value = enteredToken,
                    onValueChange = { enteredToken = it },
                    label = { Text("Code") },
                    colors = CommonFun.textFieldColors()
                )
                CommonFun.StartCountdown(onCountdownOver = onDismiss)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredToken == AppVariables.resetToken.value) {
                        onTokenVerified()
                        onDismiss()
                    } else {
                        // Display an error message
                        Toast.makeText(context, "Invalid code", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = CommonFun.customButtonColors()
            ) {
                Text("Verify Code")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = CommonFun.customButtonColors()
                ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * This function validates the password during user registration.
 *
 * It checks the following conditions:
 * 1. The email address field should not be empty.
 * 2. The email address should be valid.
 * 3. The first password field should meet the password requirements.
 * 4. The second password field should meet the password requirements.
 * 5. The first and second password fields should match.
 *
 * If any of these conditions are not met, it shows a Toast message with the appropriate error message and returns false.
 * If all conditions are met, it returns true.
 *
 * @param context The context
 **/
fun validatePassword(context: Context): Boolean {
    when {
        AppVariables.password1.value.isValidatePassword().isNotEmpty() -> {
            Toast.makeText(context, AppVariables.password1.value.isValidatePassword(), Toast.LENGTH_SHORT).show()
            return false
        }
        AppVariables.password2.value.isValidatePassword().isNotEmpty() -> {
            Toast.makeText(context, AppVariables.password2.value.isValidatePassword(), Toast.LENGTH_SHORT).show()
            return false
        }
        AppVariables.password1.value != AppVariables.password2.value -> {
            Toast.makeText(context, Constants.ERR_NOT_MATCH, Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return true
}