package com.griffith.mybuddy

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties
import java.util.Random

/**
 * This class is responsible for sending an email with a reset password token.
 * It uses SMTP for email sending and generates a random token for password resetting.
 */
class EmailSender {
    /**
     * Sends an email to the specified recipient with a reset password token.
     * @param to The email address of the recipient.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun sendEmail(to: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val props = Properties()

            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"

            val session = Session.getDefaultInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        Log.d("EMAIL", "email: ${PasswordAuthentication(Constants.EMAIL, Constants.EMAIL_PASSWORD)}")
                        return PasswordAuthentication(Constants.EMAIL, Constants.EMAIL_PASSWORD)
                    }
                })

            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(Constants.EMAIL))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                message.subject = "Reset Password"
                AppVariables.resetToken.value = generateResetToken()
                val name = AppVariables.name.value.ifEmpty { "User" }
                message.setText("""
                    Dear $name,

                    We received a request to reset your password. If you didn't make this request, just ignore this email. Otherwise, you can reset your password using this code:

                    Code: ${AppVariables.resetToken.value}

                    Thanks,
                    Your Team DrinkUp!
                """.trimIndent())

                Transport.send(message)

                println("Done")

            } catch (e: MessagingException) {
                throw RuntimeException(e)
            }
        }
    }

    /**
     * Generates a random token consisting of 6 characters.
     * The characters can be lowercase letters, uppercase letters, or digits.
     * @return A string representing the generated token.
     */
    private fun generateResetToken(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        var token = ""
        val random = Random()
        for (i in 0 until 6) {
            token += chars[random.nextInt(chars.length)]
        }
        return token
    }
}
