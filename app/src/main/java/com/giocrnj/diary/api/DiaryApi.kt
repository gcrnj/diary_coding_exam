package com.giocrnj.diary.api

import com.giocrnj.diary.models.Diary
import com.giocrnj.diary.models.DiaryResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DiaryApi {

    @POST("/api/diary")
    fun post(@Body userData: Diary): Call<DiaryResponseModel>

}