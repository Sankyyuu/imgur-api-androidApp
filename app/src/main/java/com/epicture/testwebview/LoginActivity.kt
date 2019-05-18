package com.epicture.testwebview

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.constraint.R.id.gone
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=e095cdb1aa5fd6c&response_type=token&state=APPLICATION_STATE")
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (Uri.parse(url).host == "example.com") {
                    val outerSplit = url!!.split("#")[1].split("&")
                    var username: String? = null
                    var accessToken: String? = null
                    var refreshToken: String? = null
                    var accountId: String? = null
                    var index = 0

                    for (s in outerSplit) {
                        val innerSplit = s.split("\\=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                        when (index) {
                            0 -> accessToken = innerSplit[1]

                            3 -> refreshToken = innerSplit[1]

                            4 -> username = innerSplit[1]

                            5 -> accountId = innerSplit[1]
                        }
                        index++
                    }
                    val intent = Intent(this@LoginActivity, GalleryManagerActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("accessToken", accessToken)
                    intent.putExtra("accountId", accountId)
                    startActivity(intent)
                    return false
                }
                return true
            }
        }
    }
}
