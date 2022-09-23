package com.giocrnj.diary.view_models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel : ViewModel() {

    var uris: MutableLiveData<MutableList<Uri>> = MutableLiveData<MutableList<Uri>>()
        .also {
            it.value = mutableListOf()
        }

    var includeInGallery: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        .also {
            it.value = false
        }

    var comment: MutableLiveData<String> = MutableLiveData<String>()
        .also {
            it.value = ""
        }

    var date: MutableLiveData<String> = MutableLiveData<String>()
        .also {
            it.value = ""
        }

}