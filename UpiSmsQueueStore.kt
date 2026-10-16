package com.upipulse.tracker

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object UpiSmsQueueStore {
    private const val PREF_NAME = "upi_pulse_native_queue"
    private const val KEY_QUEUE = "pending_sms_json"
    const val ACTION_NEW_UPI_SMS = "com.upipulse.tracker.ACTION_NEW_UPI_SMS"

    fun isLikelyUpiMessage(text: String): Boolean {
        val lower = text.lowercase()
        val hasCurrency = lower.contains("rs") || lower.contains("inr") || lower.contains("₹")
        val hasAction = lower.contains("debited") || lower.contains("credited") ||
                lower.contains("upi") || lower.contains("vpa") ||
                lower.contains("spent") || lower.contains("received") || lower.contains("paid")
        return hasCurrency && hasAction
    }

    @Synchronized
    fun enqueueSms(context: Context, sender: String, body: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_QUEUE, "[]") ?: "[]"
        val arr = try { JSONArray(raw) } catch (e: Exception) { JSONArray() }

        val obj = JSONObject().apply {
            put("sender", sender)
            put("body", body)
            put("timestamp", System.currentTimeMillis())
        }
        arr.put(obj)
        prefs.edit().putString(KEY_QUEUE, arr.toString()).apply()
    }

    @Synchronized
    fun consumeAll(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_QUEUE, "[]") ?: "[]"
        prefs.edit().putString(KEY_QUEUE, "[]").apply()
        return raw
    }
}
