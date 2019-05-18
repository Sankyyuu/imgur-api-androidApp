package com.epitech.epicture.dashboardgallery

import android.accounts.AuthenticatorDescription
import java.util.*

class Base {
    data class Data (
        val  data: List<Photos>
    )

    data class FavData (
        val data: List<Any>
    )

    data class Response (
        val data: String
    )

    data class Fav (
        val  id: String,
        val  title: String,
        val  description: String,
        val  datetime: Int,
        val  cover: String,
        val cover_width : Int,
        val cover_height : Int,
        val account_url: String,
        val account_id : Int,
        val privacy: Any,
        val layout: Any,
        val views : Int,
        val link: String,
        val ups: Int,
        val downs: Int,
        val points: Int,
        val score: Any,
        val is_album : Boolean,
        val vote : Any,
        val favorite : Boolean,
        val nsfw: Any,
        val section: Any,
        val comment_count : Any,
        val favorite_count : Any,
        val topic : Any,
        val topic_id : Any,
        val images_count: Int,
        val in_gallery: Boolean,
        val is_ad: Boolean,
        val tags: Any,
        val ad_type : Int,
        val ad_url : String,
        val in_most_viral : Boolean,
        val include_album_ads : Boolean,
        val images: List<Photos>
    )

    data class Photos (
        val  id: String,
        val  title: String,
        val  description: String,
        val  datetime: Int,
        val  type: String,
        val  animated: Boolean,
        val  width: Int,
        val  height: Int,
        val  size: Int,
        val  views: Int,
        val  bandwidth: Any,
        val  vote: String,
        val  favorite: Boolean,
        val  nsfw: String,
        val  section: String,
        val  account_url: String,
        val  account_id: Int,
        val  is_ad: Boolean,
        val  in_most_viral: Boolean,
        val  has_sound: Boolean,
        val  tags: List<String>,
        val  ad_type: Int,
        val  ad_url: String,
        val  in_gallery: Boolean,
        val  deletehash: String,
        val  name: String,
        val  link: String
    )
}