package com.giocrnj.diary.activities

import android.net.Uri
import android.os.Bundle
import com.giocrnj.diary.databinding.ActivityFulImageBinding
import com.giocrnj.diary.view_models.DiaryViewModel

class FullImageActivity : BaseActivity(DiaryViewModel::class.java) {

    lateinit var binding: ActivityFulImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFulImageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if(intent.hasExtra("IMAGE")){
            binding.imgFullScreen.setImageURI(Uri.parse(intent.getStringExtra("IMAGE")))
        }
    }
}