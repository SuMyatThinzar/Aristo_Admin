package com.aristo.admin.view.categories.addProducts

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.aristo.admin.data.dataholders.CategoryDataHolder
import com.aristo.admin.data.dataholders.DataListHolder
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityAddSubCategoryDetailBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.view.AbstractBaseActivity
import com.bumptech.glide.Glide

class AddSubCategoryDetailActivity : AbstractBaseActivity(){

    private val binding by lazy {ActivityAddSubCategoryDetailBinding.inflate(layoutInflater) }

    lateinit var categoryVO : CategoryVO
    lateinit var title : String
    private var price : Int = 0
    private var selectedImageUri: Uri? = null
    private var isNew = false
    private var colorCode = ""
    private var type = ""
    private var isWithImage = true
    private var editCategoryVO : CategoryVO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupListeners()

        val receivedIntent = intent
        if (receivedIntent.hasExtra("Edit")) {
            editCategoryVO = receivedIntent.getSerializableExtra("Edit") as CategoryVO
            setUpData()
        }

        if (DataListHolder.getInstance().getIsType().isNotEmpty()){
            if (DataListHolder.getInstance().getIsType().last()){
                binding.countingLayout.visibility = View.GONE
                type = CategoryDataHolder.getInstance().countingType
            }
            else{
                binding.countingLayout.visibility = View.VISIBLE
            }
        }
        else{
            binding.countingLayout.visibility = View.VISIBLE
        }
    }

    private fun setupListeners(){

        // Click btnCreate button
        binding.btnCreate.setOnClickListener {

            binding.loading.visibility = View.VISIBLE

            title = binding.etTitle.text.toString()

            if (binding.etPrice.text.toString().isNotEmpty()) {
                price = binding.etPrice.text.toString().toInt()
            }

            if (binding.etColorCode.text.toString().isNotEmpty() ) {
                colorCode = binding.etColorCode.text.toString()

                if (!colorCode.startsWith("#")){
                    colorCode = "#$colorCode"
                }
            }

            if (binding.etType.text.toString().isNotEmpty()) {
                type = binding.etType.text.toString()
            }

            checkToUpload()

        }

        binding.ibBack.setOnClickListener {
            finish()
        }

        // Set selected image url
        val galleryImage = registerForActivityResult(
            ActivityResultContracts. GetContent(),
            ActivityResultCallback { uri ->

                if (uri != null) {
                    selectedImageUri = uri
                    binding.imagePicker.setImageURI(uri)
                    isWithImage = true
                }
            })

        // When select image from gallery
        binding.imagePicker.setOnClickListener {
            galleryImage.launch("image/*")
        }

        // Show Price or not
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)

            when (selectedRadioButton.text) {
                "ပြမည်" -> {
                    binding.priceLinear.visibility = View.VISIBLE
                }

                "မပြပါ" -> {
                    binding.priceLinear.visibility = View.GONE
                    price = 0
                }
            }
        }


        // Color Code or Image
        binding.showColorRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)

            when (selectedRadioButton.text) {
                "ပုံထည့်မည်" -> {
                    binding.imageLayout.visibility = View.VISIBLE
                    binding.colorLayout.visibility = View.GONE
                    isWithImage = true
                }

                "ကာလာထည့်မည်" -> {
                    binding.colorLayout.visibility = View.VISIBLE
                    binding.imageLayout.visibility = View.GONE
                    binding.imagePicker.setImageResource(R.drawable.ic_placeholder)
                    selectedImageUri = Uri.parse("")
                    isWithImage = false
                }
            }
        }

        // isNew or not
        binding.isNewCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isNew = isChecked
        }

    }

    // Upload Data to Firebase
    private fun uploadDataToFirebase(){

        if (editCategoryVO != null) {
            if (editCategoryVO!!.imageURL.isNotEmpty() && binding.etColorCode.text.toString().isEmpty() && !isWithImage) {
                categoryVO = CategoryVO(id = editCategoryVO!!.id, title = binding.etTitle.text.toString(), price = price, imageURL = editCategoryVO!!.imageURL, new = isNew, colorCode = "", type = type, subCategories = editCategoryVO!!.subCategories)

                mFirebaseRealtimeModel.updateCategory(categoryVO, CategoryDataHolder.getInstance().updatedCategoryListVO) { isSuccess, message ->
                    finish()
                }

            } else if (binding.etColorCode.text.toString().isNotEmpty() && !isWithImage) {
                categoryVO = CategoryVO(id = editCategoryVO!!.id, title = binding.etTitle.text.toString(), price = price, imageURL = "", new = isNew, colorCode = colorCode, type = type, subCategories = editCategoryVO!!.subCategories)

                mFirebaseRealtimeModel.updateCategory(categoryVO, CategoryDataHolder.getInstance().updatedCategoryListVO) { isSuccess, message ->
                    finish()
                }

            } else if (isWithImage){
                mFirebaseRealtimeModel.uploadImageToFirebase(imageUri = selectedImageUri!!) { isSuccessImage, imageString ->
                    if (isSuccessImage) {
                        if (imageString != null) {
                            categoryVO = CategoryVO(id = editCategoryVO!!.id, title = binding.etTitle.text.toString(), price = price, imageURL = imageString, new = isNew, colorCode = "", type = type, subCategories = editCategoryVO!!.subCategories)

                            mFirebaseRealtimeModel.updateCategory(categoryVO, CategoryDataHolder.getInstance().updatedCategoryListVO) { isSuccess, message ->
                                finish()
                            }
                        }
                    }
                }
            }
            if (isNew) {
                val newProductVO = NewProductVO(id = categoryVO.id)
                mFirebaseRealtimeModel.addNewProduct(newProductVO) { _, _ -> }
            }

            if (!isNew) {
                mFirebaseRealtimeModel.getNewProducts() { isSuccess, newList ->
                    if (isSuccess) {
                        newList?.forEach {
                            if (it.id == editCategoryVO!!.id) {
                                mFirebaseRealtimeModel.removeNewProduct(it) { _, _ -> }
                            }
                        }
                    }
                }
            }

        } else {
            categoryVO = CategoryVO(title = title, price = price, imageURL = selectedImageUri.toString(), new = isNew, colorCode = colorCode, type = type, subCategories = mapOf())

            mFirebaseRealtimeModel.updateDataToFirebase(this@AddSubCategoryDetailActivity, categoryVO, isWithImage) { isSuccess, errorMessage ->

                if (isSuccess) {
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_LONG).show()
                    binding.loading.visibility = View.GONE
                    finish()

                } else {
                    binding.loading.visibility = View.GONE
                }
            }

            if (isNew) {
                val newProductVO = NewProductVO(id = categoryVO.id)
                mFirebaseRealtimeModel.addNewProduct(newProductVO) { _, _ -> }
            }
        }
    }

    private fun checkToUpload() {

        val isTypeShow: Boolean

        if (DataListHolder.getInstance().getIsType().isNotEmpty()) {// There is no counting type
            // There is counting type
            isTypeShow = DataListHolder.getInstance().getIsType().last() != true
        } else {

            // There is no counting type
            isTypeShow = true
        }

        // When user select color code
        if (!isWithImage) {

            if (isTypeShow) {
                if (binding.etType.text.isNotEmpty() &&
                    binding.etTitle.text.isNotEmpty() &&
                    binding.etColorCode.text.isNotEmpty()
                ) {

                    Log.d("Color Code", "Color Code: ${binding.etColorCode.text.count()}")

                    if (binding.etColorCode.text.count() in 6..7) {
                        // Upload data to firebase
                        uploadDataToFirebase()
                    } else {
                        showToast("Color code will be 6 characters.")
                    }
                } else if (binding.etTitle.text.isNotEmpty() && editCategoryVO?.imageURL != "") {
                    uploadDataToFirebase()
                } else {
                    showToast("ကာလာနံပါတ်၊ အမျိုးအစားအမည်၊ ရေတွက်ပုံ တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
                }
            } else {
                if (binding.etTitle.text.isNotEmpty() &&
                    binding.etColorCode.text.isNotEmpty()
                ) {

                    Log.d("Color Code", "Color Code: ${binding.etColorCode.text.count()}")

                    if (binding.etColorCode.text.count() in 6..7) {
                        // Upload data to firebase
                        uploadDataToFirebase()
                    } else {
                        showToast("Color code will be 6 characters.")
                    }
                } else if (binding.etTitle.text.isNotEmpty() && editCategoryVO?.imageURL != "") {
                    uploadDataToFirebase()
                } else {
                    showToast("ကာလာနံပါတ်၊ အမျိုးအစားအမည် တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
                }
            }
        } else {
            if (isTypeShow) {
                if (binding.etType.text.isNotEmpty() &&
                    binding.etTitle.text.isNotEmpty()
                ) {
                    if (selectedImageUri != null || editCategoryVO?.imageURL != "") {
                        colorCode = ""
                        // Upload data to firebase
                        uploadDataToFirebase()
                    } else {
                        showToast("ပစ္စည်ှးဓာတ်ပုံံ၊ အမျိုးအစားအမည်၊ ရေတွက်ပုံ တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
                    }
                } else {

                    showToast("ပစ္စည်ှးဓာတ်ပုံံ၊ အမျိုးအစားအမည်၊ ရေတွက်ပုံ တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")

                }
            } else {
                if (binding.etTitle.text.isNotEmpty()) {

                    if (selectedImageUri != null || editCategoryVO?.imageURL != "") {

                        colorCode = ""

                        uploadDataToFirebase()
                    } else {
                        showToast("ပစ္စည်ှးဓာတ်ပုံ၊ အမျိုးအစားအမည် တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
                    }
                } else {
                    showToast("ပစ္စည်ှးဓာတ်ပုံ၊ အမျိုးအစားအမည် တို့ကိုပြည့်စုံအောင်ဖြည့်ပေးပါ။")
                }
            }
        }
    }



    private fun showToast(title : String){
        binding.loading.visibility = View.GONE
        Toast.makeText(this, title, Toast.LENGTH_LONG).show()
    }

    private fun setUpData() {
        editCategoryVO?.let {
            binding.rbAddPicture.isChecked = true

            if (it.imageURL.isNotEmpty()) {
                binding.rbAddPicture.isChecked = true
                Glide.with(this).load(it.imageURL).into(binding.imagePicker)
                binding.imageLayout.visibility = View.VISIBLE
                binding.colorLayout.visibility = View.GONE
                isWithImage = false

            } else if (it.colorCode.isNotEmpty()) {
                binding.rbAddColorCode.isChecked = true
                colorCode = it.colorCode
                binding.etColorCode.setText(it.colorCode)
                binding.colorLayout.visibility = View.VISIBLE
                binding.imageLayout.visibility = View.GONE
                selectedImageUri = Uri.parse("")
                isWithImage = false
            }

            binding.etTitle.setText(it.title)
            binding.etType.setText(it.type)
            if (it.price != 0) {
                binding.rbShown.isChecked = true
                binding.priceLinear.visibility = View.VISIBLE
                binding.etPrice.setText(it.price.toString())
            } else {
                binding.rbNotShown.isChecked = true
            }

            mFirebaseRealtimeModel.getNewProducts { isSuccess, data ->
                if (isSuccess) {
                    data?.forEach { newList ->
                        if (it.id == newList.id) {
                            binding.isNewCheckBox.isChecked = true
                            isNew = true
                        }
                    }
                }
            }
        }
    }
}