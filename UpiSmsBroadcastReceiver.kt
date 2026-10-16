package com.upipulse.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class UpiSmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val fullBody = StringBuilder()
        var sender = "SMS"
        for (msg in messages) {
            sender = msg.displayOriginatingAddress ?: sender
            fullBody.append(msg.displayMessageBody ?: "")
        }

        val bodyText = fullBody.toString().trim()
        if (UpiSmsQueueStore.isLikelyUpiMessage(bodyText)) {
            UpiSmsQueueStore.enqueueSms(context, sender, bodyText)

            // Notify MainActivity if currently open in foreground
            val localIntent = Intent(UpiSmsQueueStore.ACTION_NEW_UPI_SMS).apply {
                setPackage(context.packageName)
                putExtra("sender", sender)
                putExtra("body", bodyText)
            }
            context.sendBroadcast(localIntent)
        }
    }
}
