package com.epicture.testwebview

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.ViewGroup
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.new_post_activity.view.*
import okhttp3.*
import java.io.IOException

class GalleryActivity : AppCompatActivity() {
    private var httpClient = OkHttpClient.Builder().build()
    private lateinit var image : String
    private lateinit var id : String
    private lateinit var imageView: ImageView
    var response : String = ""

    fun getIsFav() : String {
        val requestBody = RequestBody.create(null, ByteArray(0))
        val accessToken = intent.extras.get("accessToken").toString()
        var request = Request.Builder()
            .url("https://api.imgur.com/3/image/" + id + "/favorite")
            .header("Authorization", "Bearer " + accessToken)
            .post(requestBody)
            .build()
        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                this@GalleryActivity.response = response.body()!!.string()
            }
        })
        while (this.response.isEmpty()) {
        }
        return this.response
    }

    fun deleteImage() {
        val requestBody = RequestBody.create(null, ByteArray(0))
        val accessToken = intent.extras.get("accessToken").toString()
        var request = Request.Builder()
            .url("https://api.imgur.com/3/image/" + id)
            .header("Authorization", "Bearer " + accessToken)
            .delete(requestBody)
            .build()
        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                this@GalleryActivity.response = response.body()!!.string()
            }
        })
        while (this.response.isEmpty()) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_photo)
        image = intent.extras.get("image").toString()
        id = image.substringAfterLast('/').substringBefore('.')
        getIsFav()
        response = ""
        var request = getIsFav()
        response = ""
        imageView = findViewById(R.id.iv_photo)
        var infoImage = fetchData(id)
        val titleImage = findViewById<View>(R.id.textView) as TextView
        titleImage.text = infoImage.substringAfter("title\":\"", "\"").substringBefore("\"")
        val buttonDelete = findViewById<View>(R.id.floatingActionButton) as FloatingActionButton
        val checkboxPng = findViewById<View>(R.id.checkBox) as CheckBox
        if (request.contains("unfavorited"))
            checkboxPng.isChecked = false
        else
            checkboxPng.isChecked = true
        checkboxPng.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getIsFav()
            } else {
                getIsFav()
            }
        }
        buttonDelete.setOnClickListener { buttonView ->
            deleteImage()
            val username = intent.extras.get("username").toString()
            val accessToken = intent.extras.get("accessToken").toString()
            val accountId = intent.extras.get("accountId").toString()
            val intent = Intent(this@GalleryActivity, GalleryManagerActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("accessToken", accessToken)
            intent.putExtra("accountId", accountId)
            startActivity(intent)
        }
    }

    private fun fetchData(id : String): String {
        val accessToken = intent.extras.get("accessToken").toString()
        val username = intent.extras.get("username").toString()
        var request = Request.Builder()
            .url("https://api.imgur.com/3/account/" + username + "/image/" + id)
            .header("Authorization", "Bearer " + accessToken)
            .build()

        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                this@GalleryActivity.response = response.body()!!.string()
            }
        })
        while (this.response.isEmpty()) {
        }
        return this.response
    }

    override fun onStart() {
        super.onStart()

        Picasso.get()
            .load(image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .fit()
            .priority(Picasso.Priority.HIGH)
            .into(imageView)
    }
    fun onPalette(palette: Palette?) {
        if (null != palette) {
            val parent = imageView.parent.parent as ViewGroup
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY))
        }
    }
}
