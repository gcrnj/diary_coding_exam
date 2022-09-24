package com.giocrnj.diary.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.giocrnj.diary.R
import com.giocrnj.diary.api.DiaryApi
import com.giocrnj.diary.databinding.ActivityMainBinding
import com.giocrnj.diary.models.Diary
import com.giocrnj.diary.models.DiaryResponseModel
import com.giocrnj.diary.view_models.DiaryViewModel
import com.giocrnj.diary.views.BasicAdapter
import com.giocrnj.diary.views.PhotosAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class DiaryActivity : BaseActivity(DiaryViewModel::class.java) {

    //View Binding and View Model
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //set View Binding and View Model
        binding = ActivityMainBinding.inflate(layoutInflater)

        //set layout
        setContentView(binding.root)

        initiateLoseFocusListener()
    }

    private fun initiateLoseFocusListener() {
        binding.layoutComments.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) { //lose focus
                viewModel.comment.value = binding.layoutComments.editText?.text.toString()
            }
        }

        binding.layoutTags.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) { //lose focus
                viewModel.tags.value = binding.layoutTags.editText?.text.toString()
            }
        }

        (binding.layoutArea.editText as MaterialAutoCompleteTextView).setOnItemClickListener { parent, view, position, id ->
        }

        //set view model's observers
        initiateObservers()
    }

    //View Models
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

        //Area Adapter
        viewModel.areaAdapter.observe(this) { areaAdapter ->
            (binding.layoutArea.editText as MaterialAutoCompleteTextView).setAdapter(areaAdapter)
        }

        //Category Adapter
        viewModel.categoryAdapter.observe(this) { categoryAdapter ->
            (binding.layoutTaskCategory.editText as MaterialAutoCompleteTextView).setAdapter(
                categoryAdapter
            )
        }

        //include in photo camera
        viewModel.includeInGallery.observe(this) { willInclude ->
            //Set to avoid app crash when changing orientation
            binding.checkboxIncludeInCamera.isChecked = willInclude
        }

        //Comments
        viewModel.comment.observe(this) {
            binding.layoutComments.editText?.setText(it)
        }

        //Date
        viewModel.date.observe(this) {
            binding.aCDateDetails.setText(it)
        }

        //Area
        viewModel.area.observe(this) { position ->
            val autoComplete = (binding.layoutArea.editText as MaterialAutoCompleteTextView)
            autoComplete.setAdapter(viewModel.areaAdapter.value)
            val adapter = autoComplete.adapter
            binding.layoutArea.editText?.setText(
                adapter.getItem(position).toString()
            )

            //reset adapter
            viewModel.areaAdapter.value = BasicAdapter(
                this,
                resources.getStringArray(R.array.areas)
            )
        }

        //Task Category
        viewModel.category.observe(this) { position ->
            val autoComplete = (binding.layoutTaskCategory.editText as MaterialAutoCompleteTextView)
            autoComplete.setAdapter(viewModel.categoryAdapter.value)
            val adapter = autoComplete.adapter
            binding.layoutTaskCategory.editText?.setText(
                adapter.getItem(position).toString()
            )

            //reset adapter
            viewModel.categoryAdapter.value = BasicAdapter(
                this,
                resources.getStringArray(R.array.categories)
            )
        }

        //Task Category
        viewModel.tags.observe(this) {
            binding.layoutTags.editText?.setText(it)
        }

        //Set Adapters
        initiateAdapters()
    }

    private fun initiateAdapters() {
        viewModel.areaAdapter.value = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.areas) //From strings.xml
        )

        viewModel.categoryAdapter.value = BasicAdapter(
            this,
            resources.getStringArray(R.array.categories)
        )

        //Click listeners
        initiateClickListeners()
    }

    //Buttons
    private fun initiateClickListeners() {

        //Photos
        binding.btnAddPhoto.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            ) {
                //User denied it once and using Android M and up
                //REQUEST ANOTHER PERMISSION BUT EXPLAIN IT FIRST
                val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                alertDialog.apply {
                    setTitle("You are about to upload files from your Camera.")
                    setMessage("This application needs your permission to access your external storage.")
                    //"OK"
                    setPositiveButton("Accept") { _, _ ->
                        launchCameraPermission()
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

            launchCameraPermission()
        }

        //Include in photo camera?
        binding.checkboxIncludeInCamera.setOnCheckedChangeListener { _, isChecked ->
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

                        //Date
                        viewModel.date.value = "$year-$selectedMonth-$selectedDay"

                        //Time millis
                        val myCalendar = Calendar.getInstance()
                        calendar.set(year, monthOfYear, dayOfMonth)
                        viewModel.dateTimeMillis.value = myCalendar.timeInMillis
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

        //Autocomplete Areas
        (binding.layoutArea.editText as MaterialAutoCompleteTextView)
            .setOnItemClickListener { _, _, position, _ ->
                viewModel.area.value = position
            }

        //Autocomplete Categories
        (binding.layoutTaskCategory.editText as MaterialAutoCompleteTextView)
            .setOnItemClickListener { _, _, position, _ ->
                viewModel.category.value = position
            }

        //Link to existing event?
        binding.checkboxLinkToEvent.setOnCheckedChangeListener { _, isChecked ->
            Log.d("check", "$isChecked")
            viewModel.linkToExistingEvent.value = isChecked
        }

        //Next
        binding.btnNext.setOnClickListener {
            //Set edit texts values
            viewModel.tags.value = binding.layoutTags.editText?.text.toString()
            viewModel.comment.value = binding.layoutComments.editText?.text.toString()

            //Create new dialog
            AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                .apply {
                    setTitle("Submit Form")
                    setMessage("Are you sure that you want to submit all these information?")
                    //"OK"
                    setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        if (viewModel.uris.value == null ||
                            viewModel.uris.value?.isEmpty() == true ||
                            viewModel.includeInGallery.value == null ||
                            viewModel.comment.value == null ||
                            viewModel.date.value == null ||
                            viewModel.area.value == null ||
                            viewModel.category.value == null ||
                            viewModel.tags.value == null ||
                            viewModel.linkToExistingEvent.value == null){

                            //Show dialog
                            AlertDialog.Builder(
                                this@DiaryActivity,
                                android.R.style.Theme_Material_Dialog
                            )
                                .apply {
                                    //TITLE
                                    setTitle("Warning")

                                    //MESSAGE
                                    setMessage("Please fillup all the information")
                                    //END OF MESSAGE

                                    //"OK"
                                    setPositiveButton("Okay") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    //SHOW
                                    show()

                                }
                        } else {
                            //POST DATA
                            addDataToApi()
                        }
                    }
                    //"CANCEL"
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    //SHOW
                    show()

                }
        }
    }

    private fun addDataToApi() {
        val diary = Diary(
            viewModel.uris.value!!,
            viewModel.includeInGallery.value!!,
            viewModel.comment.value!!,
            viewModel.dateTimeMillis.value!!,
            viewModel.areaAdapter.value?.getItem(viewModel.area.value!!)!!,
            viewModel.categoryAdapter.value?.getItem(viewModel.category.value!!)!!,
            viewModel.tags.value!!.split(" "),
            viewModel.linkToExistingEvent.value!!
        )
        Log.d("DiaryParams", diary.toString())

        val diaryApi = Retrofit.Builder().baseUrl(getString(R.string.api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(DiaryApi::class.java)

        val response = diaryApi.post(diary)

        response.enqueue(object : Callback<DiaryResponseModel> {
            override fun onResponse(
                call: Call<DiaryResponseModel>,
                response: Response<DiaryResponseModel>
            ) {
                when (response.code()) {
                    201 -> { //SUCCESS

                        Log.d("DiaryResponse", response.body().toString())

                        //Show dialog
                        AlertDialog.Builder(
                            this@DiaryActivity,
                            android.R.style.Theme_Material_Dialog
                        )
                            .apply {
                                //TITLE
                                setTitle("Success")

                                //MESSAGE
                                setMessage(

                                    "All information are submitted\n\n" +
                                            "•Photos: ${diary.photos.size}\n\n" +
                                            "•Include in Photo Gallery: ${if (diary.include_in_gallery) "Yes" else "No"}\n\n" +
                                            "•Comment: ${diary.comment}\n\n" +
                                            "•Date: ${viewModel.date.value}\n\n" +
                                            "•Area: ${diary.area}\n\n" +
                                            "•Task Category: ${diary.category}\n\n" +
                                            "•Tags: ${diary.tags.joinToString(" / ")}\n\n" +
                                            "•Link to Existing Event: ${if (diary.link_to_event) "Yes" else "No"}\n"
                                )
                                //END OF MESSAGE

                                //"OK"
                                setPositiveButton("Okay") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                //SHOW
                                show()

                            }

                    }
                    else -> {
                        Toast.makeText(
                            this@DiaryActivity,
                            "Something went wrong.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<DiaryResponseModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

}