package com.giocrnj.diary.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.giocrnj.diary.BuildConfig
import com.giocrnj.diary.view_models.DiaryViewModel
import java.io.ByteArrayOutputStream
import java.util.Calendar


open class BaseActivity(
    private val viewModelClass: Class<DiaryViewModel>
) : AppCompatActivity() {

    //intent for camera
    private val cameraIntent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    lateinit var viewModel: DiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[viewModelClass]
    }

    //Check if permission is granted (Camera)
    fun launchCameraPermission() {
        requestPermissionCamera.launch(Manifest.permission.CAMERA)
    }

    //choose photo from camera
    private val choosePhotoFromCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            //Get URI
            val data: Intent? = result.data
            try {

                val uri = getImageUri(data?.extras?.get("data") as Bitmap)

                //uri is null
                if (uri == null) {
                    Toast.makeText(this, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                //ADD TO VIEW MODEL
                val vModelUri = viewModel.uris.value
                if (vModelUri == null) {
                    Toast.makeText(this, "Sorry, cannot find image", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                vModelUri.add(uri)
                viewModel.uris.value = vModelUri
            } catch (e: java.lang.Exception) {
                Log.e("Exception", e.message.toString(), e)
                Toast.makeText(this, "Sorry, something went wrong\n${e.message?:""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Request permission from camera
    private val requestPermissionCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //GRANTED
                //choose photo from camera
                choosePhotoFromCamera.launch(cameraIntent)
            } else {
                //DENIED
                //TODO SHOW DENIED DIALOG
            }
        }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            inImage,
            "${BuildConfig.APPLICATION_ID}_${Calendar.getInstance().timeInMillis}",
            null
        )
        return Uri.parse(path)
    }

}