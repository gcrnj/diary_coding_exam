package com.giocrnj.diary.view_models

import android.app.Application
import android.net.Uri
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.giocrnj.diary.R

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    var uris: MutableLiveData<MutableList<Uri>> = MutableLiveData<MutableList<Uri>>()
        .apply {
            value = mutableListOf()
        }

    var areaAdapter: MutableLiveData<ArrayAdapter<String>> = MutableLiveData<ArrayAdapter<String>>()

    var categoryAdapter: MutableLiveData<ArrayAdapter<String>> =
        MutableLiveData<ArrayAdapter<String>>()

    var includeInGallery: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        .apply {
            value = false
        }

    var comment: MutableLiveData<String> = MutableLiveData<String>()
        .apply {
            value = ""
        }

    var date: MutableLiveData<String> = MutableLiveData<String>()
        .apply {
            value = ""
        }

    var dateTimeMillis: MutableLiveData<Long> = MutableLiveData<Long>()

    var area: MutableLiveData<Int> = MutableLiveData<Int>()
        .apply {
            value = 0
        }

    var category: MutableLiveData<Int> = MutableLiveData<Int>()
        .apply {
            value = 0
        }

    var tags: MutableLiveData<String> = MutableLiveData<String>()
        .apply {
            value = ""
        }

    var linkToExistingEvent: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        .apply {
            value = false
        }

}