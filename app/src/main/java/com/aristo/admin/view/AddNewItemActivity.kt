package com.aristo.admin.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityAddNewItemBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.utils.processColorCode
import com.aristo.admin.adapters.CustomAutoCompleteAdapter
import com.bumptech.glide.Glide

class AddNewItemActivity : AbstractBaseActivity(), CustomAutoCompleteAdapter.OnNoMatchingItemFoundListener {

    private val binding by lazy { ActivityAddNewItemBinding.inflate(layoutInflater) }

    private val MIN_CHARACTERS_REQUIRED = 0
    private val allCategoryListVO = arrayListOf<CategoryVO>()
    private var selectedProduct: CategoryVO? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpModel()
        requestNetworkCall()
        setupImagePicker()
        setUpListeners()
    }

    private fun requestNetworkCall() {
        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            if (isSuccess) {
                data?.forEach { mainCategory ->
                    addAllCategories(mainCategory)
                }
            }
        }
    }

    private fun addAllCategories(rootCategoryVO: CategoryVO) {
        allCategoryListVO.add(rootCategoryVO)

        for (subCategory in rootCategoryVO.subCategories.values) {
            addAllCategories(subCategory)
        }
    }

    private fun setupImagePicker() {
        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                selectedImageUri = it
                binding.imagePicker.setImageURI(it)
            }
        }
        binding.imagePicker.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        val adapter = CustomAutoCompleteAdapter(this, allCategoryListVO, this)
        binding.etTitle.setAdapter(adapter)
        binding.etTitle.threshold = 0

        binding.etTitle.setOnItemClickListener { _, _, position, _ ->

            binding.errorNotFound.visibility = View.GONE

            val selectedItem = adapter.getItem(position) as CategoryVO
            selectedProduct = selectedItem
            val selectedItemName = selectedItem.title
            val selectedImageResId = selectedItem.imageURL
            val selectedImageCode = selectedItem.colorCode

            binding.tvHeaderTitle.text = "အမျိုးအစားအမည်"
            binding.etTitle.visibility = View.GONE
            binding.tvSelectedTitle.text = selectedItemName
            binding.tvSelectedTitle.visibility = View.VISIBLE
            binding.ivProductImage.visibility = View.VISIBLE

            if (selectedImageCode.isNotEmpty()) {
                binding.ivProductImage.foreground = ColorDrawable(Color.parseColor(processColorCode(selectedImageCode)))
            } else {
                Glide.with(this).load(selectedImageResId).placeholder(R.drawable.ic_placeholder).into(binding.ivProductImage)
            }
        }

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check the length of the text entered
                val typedTextLength = s?.length ?: 0

                // You can perform actions based on the length of the typed text
                if (typedTextLength == MIN_CHARACTERS_REQUIRED) {
                    // Do something when the typed text has reached a certain length
                    binding.errorNotFound.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnCreate.setOnClickListener {
            if (selectedProduct != null) {
                val newProductVO = NewProductVO()
                newProductVO.id = selectedProduct!!.id

                binding.progressBar.visibility = View.VISIBLE

                if (selectedImageUri != null) {
                    mFirebaseRealtimeModel.uploadImageToFirebase(selectedImageUri!!) { success, result ->
                        if (success) {
                            if (result != null) {
                                newProductVO.productImage = result

                                mFirebaseRealtimeModel.addNewProduct(newProductVO) { _, message ->
                                    message?.let {
                                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                    }
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    mFirebaseRealtimeModel.addNewProduct(newProductVO) { _, message ->
                        message?.let {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }

            } else {
                Toast.makeText(this, "Please select the product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNoMatchingItemFound() {
        binding.errorNotFound.visibility = View.VISIBLE
    }
}