package com.griffith.mybuddy

/**
 * Data class to hold user profile information.
 *
 * @param name The name of the user.
 * @param gender The gender of the user.
 * @param activityLevel The activity level of the user.
 * @param height The height of the user.
 * @param weight The weight of the user.
 */
data class UserInfo(
    val name: String?,
    val gender: String?,
    val activityLevel: String?,
    val height: Float?,
    val weight: Float?
)
