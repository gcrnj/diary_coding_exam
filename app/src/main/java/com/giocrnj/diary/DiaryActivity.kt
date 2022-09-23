package com.giocrnj.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.giocrnj.diary.databinding.ActivityMainBinding

class DiaryActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding                  //This activity's binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)   //set binding
        setContentView(binding.root)                            //set layout

        initiateClickListeners()


    }

    private fun initiateClickListeners() {



    }
}