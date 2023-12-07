package com.griffith.mybuddy

/**
 * `Constants` is an object that holds all the constant values used throughout the application.
 * It provides a centralized location for constants, making the code more maintainable.
 */
object Constants {
    //API KEYS
    const val API_KEY_WEATHER = "5e99e2e828c2a3d4b57fab4f8772528f"
    // const val API_KEY_LOCATION = "64d3b25aa4da48c7a43665b24067b2e7"

    val MONTHS = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    //PROFILE CONSTANTS
    const val MAX_TEMPERATURE = 25
    const val HIGHLY_ACTIVE = 500f
    const val MODERATELY_ACTIVE = 250f
    const val LIGHTLY_ACTIVE = 100f
    const val NO_ACTIVE = 0f

    //ERROR CONSTANTS
    const val ERR_EXIST = "Email already exist! Please Login!"
    const val ERR_LEN = "Password must have at least eight characters!"
    const val ERR_WHITESPACE = "Password must not contain whitespace!"
    const val ERR_DIGIT = "Password must contain at least one digit!"
    const val ERR_UPPER = "Password must have at least one uppercase letter!"
    const val ERR_SPECIAL = "Password must have at least one special character, s uch as: _%-=+#@"
    const val ERR_NOT_MATCH = "Password doesn't match!"
    const val ERR_NOT_EMPTY = "Email cannot be empty!"
    const val ERR_NOT_VALID = "Email is not valid!"
}