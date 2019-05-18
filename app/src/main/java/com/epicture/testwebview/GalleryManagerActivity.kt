package com.epicture.testwebview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import com.epitech.epicture.dashboardgallery.Base
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import android.widget.CheckBox
import com.google.gson.internal.LinkedTreeMap
import java.nio.file.Files.size
import java.nio.file.Path


class GalleryManagerActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageGalleryAdapter: ImageGalleryAdapter

    private var httpClient = OkHttpClient.Builder().build()
    private lateinit var datajson : Base.Data
    private lateinit var datajsonFav : Base.FavData
    var response : String = ""
    private var isFav: Boolean = false

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        val menuItem : MenuItem = menu.findItem(R.id.action_search)
        val searchView : SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        var tab = changeTabWithQuery(query)

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        imageGalleryAdapter = ImageGalleryAdapter(this, tab)
        recyclerView.adapter = imageGalleryAdapter
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        lateinit var tab : MutableList<String>

        if (query!!.isNotEmpty()) {
            tab = changeTabWithQuery(query)
        } else {
            if (isFav)
                tab = totablinkFav()
            else
                tab = totablink()
        }
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        imageGalleryAdapter = ImageGalleryAdapter(this, tab)
        recyclerView.adapter = imageGalleryAdapter
        return true
    }

    private fun changeTabWithQuery(query: String?) : MutableList<String> {
        var tab : MutableList<String> = arrayListOf()
        var i = 0
        while (datajson.data.count() > i) {
            if (datajson.data[i].title != null && datajson.data[i].title?.toLowerCase().contains(query.toString().toLowerCase()))
                tab.add(datajson.data[i].link)
            i++
        }
        return tab
    }

    private fun checkTypejpg() : MutableList<String> {
        val tab: MutableList<String> = arrayListOf()
        var i = 0
        while (datajson.data.count() > i) {
            if (datajson.data[i].type == "image/jpeg"){
                tab.add(datajson.data[i].link)
            }
            i++
        }
        return tab
    }

    private fun checkTypepng() : MutableList<String> {
        val tab: MutableList<String> = arrayListOf()
        var i = 0
        while (datajson.data.count() > i) {
            if (datajson.data[i].type == "image/png"){
                tab.add(datajson.data[i].link)
            }
            i++
        }
        return tab
    }

    private fun checkTypejpgFav() : MutableList<String> {
        val tab: MutableList<String> = arrayListOf()
        var i = 0
        lateinit var tmp: LinkedTreeMap<String, Base.Fav>
        while (datajsonFav.data.count() > i) {
            tmp = datajsonFav.data[i] as LinkedTreeMap<String, Base.Fav>
            if (tmp.size == 35) {
                var lol: ArrayList<Any> = tmp["images"] as ArrayList<Any>
                var tmp2: LinkedTreeMap<String, Base.Fav> = lol[0] as LinkedTreeMap<String, Base.Fav>
                var type: String = tmp2["type"] as String
                if (type == "image/jpeg")
                    tab.add(tmp2["link"] as String)
            } else {
                var type: String = tmp["type"] as String
                if (type == "image/jpeg")
                    tab.add(tmp["link"] as String)
            }
            i++
        }
        return tab
    }

    private fun checkTypepngFav() : MutableList<String> {
        val tab: MutableList<String> = arrayListOf()
        var i = 0
        lateinit var tmp : LinkedTreeMap<String, Base.Fav>
        while (datajsonFav.data.count() > i) {
            tmp = datajsonFav.data[i] as LinkedTreeMap<String, Base.Fav>
            if (tmp.size == 35) {
                var lol : ArrayList<Any> = tmp["images"] as ArrayList<Any>
                var tmp2 : LinkedTreeMap<String, Base.Fav> = lol[0] as LinkedTreeMap<String, Base.Fav>
                var type : String = tmp2["type"] as String
                if (type == "image/png")
                    tab.add(tmp2["link"] as String)
            } else {
                var type : String = tmp["type"] as String
                if (type == "image/png")
                    tab.add(tmp["link"] as String)
            }
            i++
        }
        return tab
    }

    private fun checkHyped() : MutableList<String> {
        val tab: MutableList<String> = arrayListOf()
        var i = 0
        while (datajson.data.count() > i) {
            if (datajson.data[i].views > 500){
                tab.add(datajson.data[i].link)
            }
            i++
        }
        return tab
    }

    fun newPost(view: View) {
        val username = intent.extras.get("username").toString()
        val accessToken = intent.extras.get("accessToken").toString()
        val accountId =  intent.extras.get("accountId").toString()

        val intent = Intent(this@GalleryManagerActivity, NewPostActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("accessToken", accessToken)
        intent.putExtra("accountId", accountId)
        startActivity(intent)
    }

    private fun fetchData(): String {
        val accessToken = intent.extras.get("accessToken").toString()
        val username = intent.extras.get("username").toString()
        var request = Request.Builder()
            .url("https://api.imgur.com/3/account/" + username + "/images")
            .header("Authorization", "Bearer " + accessToken)
            .build()

        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                this@GalleryManagerActivity.response = response.body()!!.string()
            }
        })
        while (this.response.isEmpty()) {
        }
        return this.response
    }

    private fun fetchDataFavoris(): String {
        val accessToken = intent.extras.get("accessToken").toString()
        val username = intent.extras.get("username").toString()
        var request = Request.Builder()
            .url("https://api.imgur.com/3/account/" + username + "/favorites")
            .header("Authorization", "Bearer " + accessToken)
            .build()

        this.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                this@GalleryManagerActivity.response = response.body()!!.string()
            }
        })
        while (this.response.isEmpty()) {
        }
        return this.response
    }

    private fun totablink() : MutableList<String> {
        var tab : MutableList<String> = arrayListOf()
        var i = 0
        while (datajson.data.count() > i) {
            tab.add(datajson.data[i].link)
            i++
        }
        return tab
    }

    private fun totablinkFav() : MutableList<String> {
        var tab : MutableList<String> = arrayListOf()
        var i = 0
        lateinit var tmp : LinkedTreeMap<String, Base.Fav>
        while (datajsonFav.data.count() > i) {
            tmp = datajsonFav.data[i] as LinkedTreeMap<String, Base.Fav>
            if (tmp.size == 35) {
                var lol : ArrayList<Any> = tmp["images"] as ArrayList<Any>
                var tmp2 : LinkedTreeMap<String, Base.Fav> = lol[0] as LinkedTreeMap<String, Base.Fav>
                tab.add(tmp2["link"] as String)
            } else {
                tab.add(tmp["link"] as String)
            }
            i++
        }
        return tab
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_manager_activity)
        var res : String = fetchData()
        var gson = Gson()
        datajson = gson?.fromJson(res, Base.Data::class.java)
        var tab = totablink()

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        imageGalleryAdapter = ImageGalleryAdapter(this, tab)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val checkboxPng = findViewById<View>(R.id.filterPng) as CheckBox
        checkboxPng.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //Do Whatever you want in isChecked
                val checkboxJpg = findViewById<View>(R.id.filterJpg) as CheckBox
                if (checkboxJpg.isChecked == true) {
                    checkboxJpg.isChecked = false
                }
                if (isFav) {
                    var tab2 : MutableList<String> = checkTypepngFav()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                } else {
                    var tab2 : MutableList<String> = checkTypepng()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                }
            } else {
                if (isFav) {
                var tab2 : MutableList<String> = totablinkFav()

                val layoutManager = GridLayoutManager(this, 2)
                recyclerView = findViewById(R.id.rv_images)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                recyclerView.adapter = imageGalleryAdapter
            } else {
                var tab2 : MutableList<String> = totablink()

                val layoutManager = GridLayoutManager(this, 2)
                recyclerView = findViewById(R.id.rv_images)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                recyclerView.adapter = imageGalleryAdapter
            }
            }
        }

        val checkboxJpg = findViewById<View>(R.id.filterJpg) as CheckBox
        checkboxJpg.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val checkboxPng = findViewById<View>(R.id.filterPng) as CheckBox
                if (checkboxPng.isChecked == true) {
                    checkboxPng.isChecked = false
                }
                //Do Whatever you want in isChecked
                if (isFav) {
                    var tab2 : MutableList<String> = checkTypejpgFav()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                } else {
                    var tab2 : MutableList<String> = checkTypejpg()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                }
            } else {
                if (isFav) {
                    var tab2 : MutableList<String> = totablinkFav()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                } else {
                    var tab2 : MutableList<String> = totablink()

                    val layoutManager = GridLayoutManager(this, 2)
                    recyclerView = findViewById(R.id.rv_images)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    imageGalleryAdapter = ImageGalleryAdapter(this, tab2)
                    recyclerView.adapter = imageGalleryAdapter
                }
            }
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gallery -> {
                this.isFav = false
                this.response = ""
                var res = fetchData()
                var gson = Gson()
                datajson = gson?.fromJson(res, Base.Data::class.java)
                var tab : MutableList<String> = totablink()

                val layoutManager = GridLayoutManager(this, 2)
                recyclerView = findViewById(R.id.rv_images)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                imageGalleryAdapter = ImageGalleryAdapter(this, tab)
                recyclerView.adapter = imageGalleryAdapter

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_albums -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favoris -> {
                this.isFav = true
                this.response = ""
                var res = fetchDataFavoris()
                var gson = Gson()
                datajsonFav = gson?.fromJson(res, Base.FavData::class.java)
                var tab : MutableList<String> = totablinkFav()

                val layoutManager = GridLayoutManager(this, 2)
                recyclerView = findViewById(R.id.rv_images)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                imageGalleryAdapter = ImageGalleryAdapter(this, tab)
                recyclerView.adapter = imageGalleryAdapter
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onStart() {
        super.onStart()
        recyclerView.adapter = imageGalleryAdapter
    }

    private inner class ImageGalleryAdapter(val context: Context, val tab: MutableList<String>)
        : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val photoView = inflater.inflate(R.layout.item_image, parent, false)
            return MyViewHolder(photoView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val sunsetPhoto = tab.get(position)
            val imageView = holder.photoImageView

            Picasso.get()
                .load(sunsetPhoto)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .tag(context)
                .into(imageView)

        }

        override fun getItemCount(): Int {
            return tab.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            var photoImageView: ImageView = itemView.findViewById(R.id.iv_photo)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val accessToken = intent.extras.get("accessToken").toString()
                    val username = intent.extras.get("username").toString()
                    val accountId = intent.extras.get("accountId").toString()
                    val sunsetPhoto = tab.get(position)
                    val intent = Intent(context, GalleryActivity::class.java).apply {
                        putExtra("image", sunsetPhoto)
                        putExtra("username", username)
                        putExtra("accessToken", accessToken)
                        putExtra("accountId", accountId)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}