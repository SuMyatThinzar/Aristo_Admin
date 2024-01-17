package com.aristo.admin.view.categories.addProducts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.admin.data.dataholders.DataListHolder
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.databinding.ActivityCreateSubCategoryBinding
import com.aristo.admin.adapters.SubCategoryListRecyclerViewAdapter
import com.aristo.admin.view.AbstractBaseActivity

class CreateSubCategoryActivity : AbstractBaseActivity(){

    private val binding by lazy { ActivityCreateSubCategoryBinding.inflate(layoutInflater) }
    private val subCategoryAdapter by lazy { SubCategoryListRecyclerViewAdapter( ArrayList()) }
    private val subCatLayoutManager by lazy { LinearLayoutManager(this) }
    private var mainIndex : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        SharedPreferencesManager.initialize(this)
        mainIndex = SharedPreferencesManager.getMainIndex()

        setUpModel()
        setRecyclerViewAdapter()
        setUpListeners()
    }

    private fun setUpListeners() {

        binding.createSubCategory.setOnClickListener {
            val intent = Intent(this, AddSubCategoryDetailActivity::class.java)
            startActivity(intent)

        }

        binding.ibBack.setOnClickListener {
            removeIndex()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.subLoading.visibility = View.VISIBLE
        SharedPreferencesManager.initialize(this)

        mFirebaseRealtimeModel.getCategoriesData { isSuccess, data ->
            if (isSuccess) {
                if (data != null) {

                    subCategoryAdapter.updateData(data)
                }
            } else {
                Toast.makeText(this, "Can't retrieve datas.", Toast.LENGTH_LONG).show()
            }
            binding.subLoading.visibility = View.GONE
        }
    }
    private fun setRecyclerViewAdapter() {
        // Set up the RecyclerView
        binding.rvSubCategory.layoutManager = subCatLayoutManager
        binding.rvSubCategory.adapter = subCategoryAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        removeIndex()
    }

    private fun removeIndex(){
        if (DataListHolder.getInstance().getSubIndexList().isNotEmpty()){

            DataListHolder.getInstance().getSubIndexList().removeLast()
        }
        if (DataListHolder.getInstance().getSubIDList().isNotEmpty()){

            DataListHolder.getInstance().getSubIDList().removeLast()
        }
        if (DataListHolder.getInstance().getChildPath().isNotEmpty()){

            DataListHolder.getInstance().getChildPath().removeLast()
        }
        if (DataListHolder.getInstance().getIsType().isNotEmpty()){

            DataListHolder.getInstance().getIsType().removeLast()
        }
    }

}