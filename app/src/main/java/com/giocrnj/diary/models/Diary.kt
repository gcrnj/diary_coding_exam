package com.giocrnj.diary.models

import android.net.Uri

data class Diary(
    val photos: MutableList<Uri>,
    val include_in_gallery: Boolean,
    val comment: String,
    val date: Long,
    val area: String,
    val category: String,
    val tags: List<String>,
    val link_to_event: Boolean,
)
