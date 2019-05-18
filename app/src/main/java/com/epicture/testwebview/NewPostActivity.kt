package com.epicture.testwebview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.v7.app.AppCompatActivity
import android.view.View
import java.io.FileDescriptor
import android.support.design.widget.TextInputLayout
import android.util.Base64
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import okhttp3.MultipartBody
import android.widget.ImageView
import okhttp3.RequestBody




class NewPostActivity : AppCompatActivity() {
    private var REQUEST_CODE = 42
    private var httpClient = OkHttpClient.Builder().build()
    private var accessToken : String = ""
    private var selectedImage : String = ""
    private var accountId : String = ""
    private var username : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_post_activity)

        accessToken = intent.extras.get("accessToken").toString()
        accountId = intent.extras.get("accountId").toString()
        username = intent.extras.get("username").toString()
    }

    fun startSearch(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val parcelFileDescriptor: ParcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                val byteArrayOutputStream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

                val byteArray = byteArrayOutputStream.toByteArray()
                val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
                selectedImage = encoded

                val myImage = findViewById(R.id.imageSelected) as ImageView
                myImage.setImageBitmap(image)
            }
        }
    }

    fun postImage(view: View) {
        val title = findViewById(R.id.titleText) as TextInputLayout
        val description = findViewById(R.id.descriptionText) as TextInputLayout
        val title1 = title.editText!!.text.toString()
        val description1 = description.editText!!.text.toString()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", selectedImage)
            .addFormDataPart("title", title1)
            .addFormDataPart("description", description1)
            .build()

        var request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .header("Authorization", "Bearer " + accessToken)
            .post(requestBody)
            .build()

        val reqbody = RequestBody.create(null, ByteArray(0))

        val image = findViewById(R.id.creating) as ImageView
        image.visibility =  View.VISIBLE

        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val intent = Intent(this@NewPostActivity, GalleryManagerActivity::class.java)
                        intent.putExtra("username", username)
                        intent.putExtra("accessToken", accessToken)
                        intent.putExtra("accountId", accountId)
                        startActivity(intent)
                    }
        })
    }
}