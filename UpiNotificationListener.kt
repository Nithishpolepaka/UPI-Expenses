package com.upipulse.tracker

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class UpiNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val pkg = sbn.packageName ?: return
        // Ignore our own notifications
        if (pkg == applicationContext.packageName) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: ""

        val combined = "$title $text".trim()
        if (UpiSmsQueueStore.isLikelyUpiMessage(combined)) {
            UpiSmsQueueStore.enqueueSms(applicationContext, title.ifEmpty { "Bank Alert" }, text)

            val localIntent = Intent(UpiSmsQueueStore.ACTION_NEW_UPI_SMS).apply {
                setPackage(applicationContext.packageName)
                putExtra("sender", title.ifEmpty { "UPI Alert" })
                putExtra("body", text)
            }
            applicationContext.sendBroadcast(localIntent)
        }
    }
}
