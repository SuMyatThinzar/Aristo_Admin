package com.aristo.admin.view.categories.addProducts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.admin.data.dataholders.DataListHolder
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityCreateMainCategoryBinding
import com.aristo.admin.databinding.BottomSheetMainCatDeleteBinding
import com.aristo.admin.adapters.MainCategoryListRecyclerViewAdapter
import com.aristo.admin.view.AbstractBaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class CreateMainCategoryActivity : AbstractBaseActivity(), OnFinishDeleteMainDelegate, BottomSheet{

    private val binding by lazy { ActivityCreateMainCategoryBinding.inflate(layoutInflater) }
    private val mainCategoryAdapter by lazy { MainCategoryListRecyclerViewAdapter(this, ArrayList()) }
    private val mainCatLayoutManager by lazy { LinearLayoutManager(this) }

    private var mainCategoryTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpModel()
        setupRecyclerView()
        setUpListeners()

        DataListHolder.getInstance().deleteAll()
        deleteSharedPreferences()

    }

    private fun setUpListeners() {

        binding.createNewCategory.setOnClickListener {
            val intent = Intent(this, AddMainCategoryDetailActivity::class.java)
            startActivity(intent)
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.mainLoading.visibility = View.VISIBLE

        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->

            binding.mainLoading.visibility = View.GONE

            if (isSuccess) {
                if (data != null) {

                    mainCategoryAdapter.updateData(data)
                }
            } else {
                Toast.makeText(this, "Can't retrieve datas.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setupRecyclerView() {
        // Set up the RecyclerView
        binding.rvMainCategory.layoutManager = mainCatLayoutManager
        binding.rvMainCategory.adapter = mainCategoryAdapter
    }

    private fun deleteSharedPreferences(){

        SharedPreferencesManager.initialize(this)
        SharedPreferencesManager.removeCategoryId()
        SharedPreferencesManager.removeMainIndex()
    }

    override fun onFinishActivity(message: String) {
        Toast.makeText(this, "$mainCategoryTitle has been $message", Toast.LENGTH_LONG).show()
    }

    override fun onShowBottomSheet(mainCatId: String, mainCatTitle : String) {

            val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
            val binding = BottomSheetMainCatDeleteBinding.inflate(layoutInflater)
            dialog.setContentView(binding.root)
            dialog.show()

            binding.btnDelete.setOnClickListener {

                val fragmentManager = supportFragmentManager
                val deleteDialog = DeleteMainCategoryDialogFragment()

                mainCategoryTitle = mainCatTitle

                val bundle = Bundle()
                bundle.putString("main_cat_id", mainCatId)
                deleteDialog.arguments = bundle
                deleteDialog.show(fragmentManager, "DeleteMainCategoryDialogFragment")

                dialog.dismiss()
            }
    }

}
