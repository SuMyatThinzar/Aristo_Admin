package com.aristo.admin.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aristo.admin.data.dataholders.AdminDataHolder
import com.aristo.admin.databinding.ActivityHomeBinding
import com.aristo.admin.data.vos.AdminVO
import com.aristo.admin.view.adminProfile.EditActivity
import com.aristo.admin.view.categories.addProducts.CreateMainCategoryActivity
import com.aristo.admin.view.categories.getProducts.MainCategoriesActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class HomeActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpModel()

        AppCenter.start(
            application, "9e748b4f-85ad-454e-a939-60a8889e7808\"",
            Analytics::class.java, Crashes::class.java
        )

        fetchAdminData()
        setUpListeners()
    }

    private fun setUpListeners() {

        binding.btnEdit.setOnClickListener {
            if (isLoading){
                Toast.makeText(this, "Loading, Please wait...", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(applicationContext, EditActivity::class.java))
            }
        }

        binding.btnAddNewProduct.setOnClickListener {
            startActivity(Intent(applicationContext, CreateMainCategoryActivity::class.java))
        }

        binding.btnRecentProducts.setOnClickListener {
            startActivity(Intent(applicationContext, MainCategoriesActivity::class.java))
        }

        binding.btnHelpCenter.setOnClickListener {
            startActivity(Intent(applicationContext, HelpCenterActivity::class.java))
        }

        binding.btnAddNewItem.setOnClickListener {
            startActivity(Intent(applicationContext, AddNewItemActivity::class.java))
        }

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(applicationContext, SendNotificationActivity::class.java))
        }

        binding.btnAddPoint.setOnClickListener {
            startActivity(Intent(applicationContext, UserIdListActivity::class.java))
        }
    }

    private fun fetchAdminData(){
        // Edit Admin Information
        binding.progressBar.visibility = View.VISIBLE

        mFirebaseRealtimeModel.getAdmin { isSuccess, result ->

            if (isSuccess) {
                if (result != null) {
                    result as AdminVO
                    AdminDataHolder.instance.adminVO = result
                }
            } else {
//                Toast.makeText(applicationContext, result.toString(), Toast.LENGTH_LONG).show()
            }
            isLoading = false
            binding.progressBar.visibility = View.GONE
        }
    }
}