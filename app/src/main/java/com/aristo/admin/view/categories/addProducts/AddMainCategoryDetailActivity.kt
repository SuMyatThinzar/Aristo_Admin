package com.aristo.admin.view.categories.addProducts

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultCallback
import com.aristo.admin.databinding.ActivityAddMainCategoryDetailBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.view.AbstractBaseActivity

class AddMainCategoryDetailActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityAddMainCategoryDetailBinding.inflate(layoutInflater) }
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupListeners()
        setupImagePicker()
    }

    private fun setupListeners() {
        binding.btnCreate.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            checkToUpload()
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun setupImagePicker() {
        // Set selected image url
        val galleryImage = registerForActivityResult(
            ActivityResultContracts. GetContent(),
            ActivityResultCallback { uri ->

                if (uri != null) {
                    selectedImageUri = uri
                    binding.imagePicker.setImageURI(uri)
                }
            })

        binding.imagePicker.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    private fun checkToUpload(){

        // Check edit text are empty or not
        if (binding.etTitle.text.isNotEmpty() &&
            selectedImageUri != null){

            // Upload data to firebase
            uploadData()

        }
        else{
            showToast("အမျိုးအစားအမည်၊ ပစ္စည်ှးဓာတ်ပုံ တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
        }

    }

    private fun showToast(title : String){
        binding.loading.visibility = View.GONE
        Toast.makeText(this, title, Toast.LENGTH_LONG).show()
    }

    private fun uploadData(){
        // Create a new Category object
        val categoryVO = CategoryVO(title = binding.etTitle.text.toString(), price = 0, imageURL = selectedImageUri.toString(), new = false, subCategories = mapOf())

        SharedPreferencesManager.initialize(this)
        // Upload the category data to Firebase
        mFirebaseRealtimeModel.uploadDataToFirebase(categoryVO) { isSuccess, errorMessage ->
            if (isSuccess) {
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_LONG).show()
                binding.loading.visibility = View.GONE

                finish()
            } else {

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                binding.loading.visibility = View.GONE

            }
        }
    }

}
