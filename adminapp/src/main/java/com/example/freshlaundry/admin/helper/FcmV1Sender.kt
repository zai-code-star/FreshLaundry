package com.example.freshlaundry.admin.helper

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.concurrent.TimeUnit

object FcmV1Sender {

    fun sendNotificationToToken(
        context: Context,
        targetToken: String,
        title: String,
        body: String,
        orderId: String
    ) {
        val client = OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .build()

        try {
            // Baca file service_account.json dari folder assets
            val inputStream: InputStream =
                context.applicationContext.assets.open("service_account.json")
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            credentials.refreshIfExpired()
            inputStream.close()

            val accessToken = credentials.accessToken.tokenValue

            // Bangun JSON payload
            val messageJson = JsonObject().apply {
                addProperty("token", targetToken)

                // Fallback notification
                val notification = JsonObject().apply {
                    addProperty("title", title)
                    addProperty("body", body)
                }
                add("notification", notification)

                // Data payload
                val data = JsonObject().apply {
                    addProperty("title", title)
                    addProperty("body", body)
                    addProperty("orderId", orderId)
                }
                add("data", data)

                // Android config
                val androidNotification = JsonObject().apply {
                    addProperty("title", title)
                    addProperty("body", body)
                    addProperty("sound", "default")
                }

                val androidConfig = JsonObject().apply {
                    add("notification", androidNotification)
                    addProperty("priority", "high")
                }

                add("android", androidConfig)
            }

            val rootJson = JsonObject().apply {
                add("message", messageJson)
            }

            // Kirim request ke FCM v1 endpoint
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/fresh-laundry-67ffb/messages:send")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val responseBody = response.body?.string() ?: "No response body"
                println("Error sending FCM: ${response.code}, $responseBody")
            } else {
                println("Notification sent successfully!")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
