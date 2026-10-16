package com.upipulse.tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val liveSmsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Drain queue and push to WebView immediately
            val pendingJson = UpiSmsQueueStore.consumeAll(this@MainActivity)
            pushQueueToWebView(pendingJson)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
        }
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val pendingJson = UpiSmsQueueStore.consumeAll(this@MainActivity)
                pushQueueToWebView(pendingJson)
            }
        }

        webView.addJavascriptInterface(AndroidUpiBridge(this), "AndroidUpiBridge")
        webView.loadUrl("file:///android_asset/upi_expense_tracker.html")

        requestAndroid14PermissionsIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(UpiSmsQueueStore.ACTION_NEW_UPI_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(liveSmsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(liveSmsReceiver, filter)
        }
        val pendingJson = UpiSmsQueueStore.consumeAll(this)
        pushQueueToWebView(pendingJson)
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(liveSmsReceiver)
        } catch (_: Exception) {}
    }

    private fun pushQueueToWebView(pendingJson: String) {
        if (pendingJson == "[]" || pendingJson.isBlank()) return
        runOnUiThread {
            val escaped = JSONObject.quote(pendingJson)
            val js = """
                (function() {
                    try {
                        var list = JSON.parse($escaped);
                        if (Array.isArray(list) && window.onNativeUpiSmsReceived) {
                            list.forEach(function(item) {
                                window.onNativeUpiSmsReceived(item.body, item.sender);
                            });
                        }
                    } catch (e) { console.error(e); }
                })();
            """.trimIndent()
            webView.evaluateJavascript(js, null)
        }
    }

    private fun requestAndroid14PermissionsIfNeeded() {
        val needed = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1001)
        }
    }

    class AndroidUpiBridge(private val context: Context) {
        @JavascriptInterface
        fun consumePendingSmsQueue(): String {
            return UpiSmsQueueStore.consumeAll(context)
        }
    }
}
