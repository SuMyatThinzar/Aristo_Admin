package com.aristo.admin.view.adminProfile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.aristo.admin.data.dataholders.AdminDataHolder
import com.aristo.admin.databinding.ActivityEditBinding
import com.aristo.admin.data.vos.AdminVO
import com.aristo.admin.view.AbstractBaseActivity

class EditActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }

    private var selectedImageUri: Uri? = null
    private var adminVO: AdminVO = AdminVO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpModel()

        binding.ibBack.setOnClickListener {
            finish()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.ivCompanyLogo.setImageURI(uri)
            }
        }

        binding.btnEditPhoto.setOnClickListener {
            galleryImage.launch("image/*")
        }

        if (AdminDataHolder.instance.adminVO == null) {
            mFirebaseRealtimeModel.getAdmin { isSuccess, admin ->
                if (isSuccess) {
                    if (admin != null) {
                        this.adminVO = admin as AdminVO
                        AdminDataHolder.instance.adminVO = admin
                        setUpData(admin)
                    }
                }
            }
        } else if (AdminDataHolder.instance.adminVO != null) {
            adminVO = AdminDataHolder.instance.adminVO!!
            setUpData(adminVO)
        }


        binding.btnSave.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE

            adminVO.companyName = adminVO.companyName
            adminVO.address = binding.etCompanyAddress.text.toString()
            adminVO.phone = binding.etCompanyPhone.text.toString()
            adminVO.viber = binding.etCompanyViberNo.text.toString()
            adminVO.fbPage = binding.etCompanyFbPage.text.toString()
            adminVO.fbPageLink = binding.etCompanyFbPageLink.text.toString()

            mFirebaseRealtimeModel.addAdmin(adminVO) { isSuccess, message ->
                binding.progressBar.visibility = View.GONE
                finish()
            }

//            if (selectedImageUri != null) {
//                    CategoryFirebase.uploadImageToFirebase(selectedImageUri!!) { isSuccessful, imageUrl ->
//
//                        if (isSuccessful) {
//                            admin.image = imageUrl
//                            CategoryFirebase.addAdmin(admin) { isSuccess, message ->
//                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//                                binding.progressBar.visibility = View.GONE
//                                finish()
//                            }
//                        }
//                    }
//                }
//                else if (selectedImageString != null){
//                    admin.image = selectedImageString
//                    CategoryFirebase.addAdmin(admin) { isSuccess, message ->
////                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//                        binding.progressBar.visibility = View.GONE
//                        finish()
//                    }
//                } else {
//                    admin.image = null
//                    CategoryFirebase.addAdmin(admin) { isSuccess, message ->
////                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//                        binding.progressBar.visibility = View.GONE
//                        finish()
//                    }
//                }
//            }
//        }
        }

    }

    private fun setUpData(adminVO: AdminVO) {

        binding.etCompanyAddress.setText(adminVO.address)
        binding.etCompanyPhone.setText(adminVO.phone)
        binding.etCompanyViberNo.setText(adminVO.viber)
        binding.etCompanyFbPage.setText(adminVO.fbPage)
        binding.etCompanyFbPageLink.setText(adminVO.fbPageLink)

    }

}