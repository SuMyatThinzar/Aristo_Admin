package com.aristo.admin.view.categories.getProducts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.admin.data.dataholders.CategoryDataHolder
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityMainCategoriesBinding
import com.aristo.admin.databinding.BottomSheetMoreBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.view.categories.addProducts.AddSubCategoryDetailActivity
import com.aristo.admin.adapters.MainCategoryListAdapter
import com.aristo.admin.adapters.ChildCategoryListAdapter
import com.aristo.admin.view.AbstractBaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainCategoriesActivity : AbstractBaseActivity(), MainCategoryListAdapter.MainCategoriesRecyclerViewListener, ChildCategoryListAdapter.ChildCategoryListener {

    private val binding by lazy { ActivityMainCategoriesBinding.inflate(layoutInflater) }

    private lateinit var mMainCategoryAdapter: MainCategoryListAdapter
    private lateinit var mSubCategoryAdapter: ChildCategoryListAdapter

    private var categoryVOList: List<CategoryVO> = listOf()
    private var newItemsList: List<NewProductVO> = listOf()
    private var currentPosition = 0

    private val categoryListHolder = CategoryDataHolder.getInstance().updatedCategoryListVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CategoryDataHolder.getInstance().index += 1

        setRecyclerViewAdapter()
        setUpListeners()
        setUpModel()
        fetchNetworkData()

    }

    private fun fetchNetworkData() {

        mFirebaseRealtimeModel.getNewProducts { isSuccess, data ->
            data?.let{newItemsList = it}
        }

        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            if (isSuccess) {
                if (data != null) {
                    categoryVOList = data

                    if (categoryVOList.isNotEmpty()){
                        if (categoryListHolder.isEmpty()) {
                            categoryListHolder.add(categoryVOList[currentPosition])
                        } else {
                            categoryListHolder[0] = categoryVOList[currentPosition]
                        }

                        mMainCategoryAdapter.setNewData(data)
                        mSubCategoryAdapter.setNewData(data[currentPosition].subCategories.values.toList())
                    }
                }
            } else {
                Toast.makeText(this, "Can't retrieve data.", Toast.LENGTH_LONG).show()
            }

            binding.mainLoading.visibility = View.GONE
        }

    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerViewAdapter(){

        // Main Categories Recycler View
        mMainCategoryAdapter = MainCategoryListAdapter(this)
        binding.rvMainCategories.layoutManager = LinearLayoutManager(this)
        binding.rvMainCategories.adapter = mMainCategoryAdapter

        // Sub Categories Recycler View
        mSubCategoryAdapter = ChildCategoryListAdapter(this,  "Main")
        binding.rvSubCategories.layoutManager = GridLayoutManager(this,2)
        binding.rvSubCategories.adapter = mSubCategoryAdapter
    }

    // Reload Sub Categories Recycler View when select main categories recycler view
    override fun reloadSubCategoriesRecyclerView(index : Int) {
        mSubCategoryAdapter.setNewData(categoryVOList[index].subCategories.values.toList())

        currentPosition = index
        if (categoryListHolder.isNotEmpty()) {
            categoryListHolder[0] = categoryVOList[currentPosition]
        }
    }

    override fun onTapMore(categoryVO: CategoryVO, type: String) {

        val newProductVO = NewProductVO(id = categoryVO.id)

        if (type == "Main") {
            val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
            val dialogBinding = BottomSheetMoreBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.show()

            dialogBinding.btnEdit.setOnClickListener {
                val intent = Intent(this, AddSubCategoryDetailActivity::class.java)
                intent.putExtra("Edit", categoryVO)
                startActivity(intent)
                dialog.dismiss()
            }

            newItemsList.forEach {
                if (categoryVO.id == it.id) {
                    dialogBinding.cbNew.isChecked = true
                }
            }

            dialogBinding.cbNew.setOnCheckedChangeListener { _, isChecked ->
                categoryVO.new = isChecked
                dialogBinding.progressBar.visibility = View.VISIBLE
                dialogBinding.llViews.visibility = View.INVISIBLE

                if (isChecked) {
                    mFirebaseRealtimeModel.addNewProduct(newProductVO) { _, message ->
                            dialogBinding.progressBar.visibility = View.GONE
                            dialogBinding.llViews.visibility = View.VISIBLE
                            dialog.dismiss()
                        }
                } else {
                    mFirebaseRealtimeModel.removeNewProduct(newProductVO) { _, message ->
                        dialogBinding.progressBar.visibility = View.GONE
                        dialogBinding.llViews.visibility = View.VISIBLE
                        dialog.dismiss()
                    }
                }
            }

            dialogBinding.btnDelete.setOnClickListener {
                mFirebaseRealtimeModel.deleteCategory(categoryVO) { _, message ->
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                mFirebaseRealtimeModel.removeNewProduct(newProductVO) { _, message ->
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onBackPressed() {
        CategoryDataHolder.getInstance().updatedCategoryListVO.clear()
        CategoryDataHolder.getInstance().index = 0
        finish()
    }
}