package com.giocrnj.diary

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.giocrnj.diary.databinding.ActivityMainBinding
import com.giocrnj.diary.view_models.DiaryViewModel
import com.giocrnj.diary.views.PhotosAdapter
import java.util.*


class DiaryActivity : AppCompatActivity() {

    //View Binding and View Model
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DiaryViewModel

    //intent for gallery
    private val galleryIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //set View Binding and View Model
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        //set layout
        setContentView(binding.root)

        //set view model's observers
        initiateObservers()


    }

    private fun initiateObservers() {

        //Add photos to site diary
        viewModel.uris.observe(this) { uris ->
            //remove item listener
            binding.recyclerVPhotosToDiary.adapter = PhotosAdapter(uris) { removedItem ->
                //Remove Item
                uris.removeAt(removedItem)
                //set new value
                viewModel.uris.value = uris
            }
        }

        //include in photo gallery
        viewModel.includeInGallery.observe(this) { willInclude ->
            //Set to avoid app crash when changing orientation
            binding.checkboxIncludeInGallery.isChecked = willInclude
        }

        //Comments
        viewModel.comment.observe(this) {
            binding.layoutComments.editText?.setText(it)
        }

        //Comments
        viewModel.comment.observe(this) {
            binding.layoutComments.editText?.setText(it)
        }

        //Comments
        viewModel.date.observe(this) {
            binding.tIDateDetails.editText?.setText(it)
        }

        //Click listeners
        initiateClickListeners()
    }

    private fun initiateClickListeners() {

        //Photos
        binding.btnAddPhoto.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                //User denied it once and using Android M and up
                //REQUEST ANOTHER PERMISSION BUT EXPLAIN IT FIRST
                val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                alertDialog.apply {
                    setTitle("You are about to upload files from your Gallery.")
                    setMessage("This application needs your permission to access your external storage.")
                    //"OK"
                    setPositiveButton("Accept") { _, _ ->
                        launchGalleryPermission()
                    }
                    //"CANCEL"
                    setNegativeButton("Decline") { dialog, _ ->
                        dialog.dismiss()
                    }
                    //SHOW
                    alertDialog.show()
                }
                return@setOnClickListener
            }

            launchGalleryPermission()
        }

        //Include in photo gallery?
        binding.checkboxIncludeInGallery.setOnCheckedChangeListener { _, isChecked ->
            Log.d("check", "$isChecked")
            viewModel.includeInGallery.value = isChecked
        }

        //Date Picker Dialog
        binding.aCDateDetails.setOnClickListener {
            //-8 years to the date
            val calendar = Calendar.getInstance()

            //Selecting Dates
            val datePickerDialog = DatePickerDialog(
                this,
                object : OnDateSetListener {
                    private var year = 0
                    private var month = 0
                    private var day = 0
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        if (this.year == year && month == monthOfYear && day == dayOfMonth) return

                        //Month - add 0 at front if 1 digit
                        val selectedMonth =
                            if (monthOfYear < 10)
                                "0$monthOfYear"
                            else
                                monthOfYear.toString()

                        //Day - add 0 at front if 1 digit
                        val selectedDay =
                            if (dayOfMonth < 10)
                                "0$dayOfMonth"
                            else
                                dayOfMonth.toString()


                        viewModel.date.value = "$year-$selectedMonth-$selectedDay"
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            //Maximum Date
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            datePickerDialog.show()
        }

    }

    //Check if permission is granted
    private fun launchGalleryPermission() {
        requestPermissionGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //choose photo from gallery
    private val choosePhotoFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            //Uri
            val uri = it.data?.data
            if (uri == null) {
                Toast.makeText(this, "Sorry, cannot find image", Toast.LENGTH_SHORT).show()
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
        }
    }

    //Request permission from gallery
    private val requestPermissionGallery =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //GRANTED
                //choose photo from gallery
                choosePhotoFromGallery.launch(galleryIntent)
            } else {
                //DENIED
                //TODO SHOW DENIED DIALOG
            }
        }
}