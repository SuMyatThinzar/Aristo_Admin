package com.aristo.admin.view.categories.getProducts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.aristo.admin.data.dataholders.CategoryDataHolder
import com.aristo.admin.R
import com.aristo.admin.databinding.ActivityChildCategoriesBinding
import com.aristo.admin.databinding.BottomSheetMoreBinding
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.view.categories.addProducts.AddSubCategoryDetailActivity
import com.aristo.admin.adapters.ChildCategoryListAdapter
import com.aristo.admin.view.AbstractBaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChildCategoriesActivity : AbstractBaseActivity(), ChildCategoryListAdapter.ChildCategoryListener {

    private val binding by lazy { ActivityChildCategoriesBinding.inflate(layoutInflater) }
    private val mSubCategoryAdapter by lazy { ChildCategoryListAdapter(this,  "Child") }

    private var categoryVOList: List<CategoryVO> = listOf()
    private var newItemsList: List<NewProductVO> = listOf()
    private var subCategoryVO: CategoryVO? = null
    private val categoryListHolder = CategoryDataHolder.getInstance().updatedCategoryListVO
    private var isFound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CategoryDataHolder.getInstance().index += 1

        subCategoryVO = intent.getSerializableExtra("childCategoriesList") as CategoryVO?

        subCategoryVO?.let{

            if (categoryListHolder.count() == CategoryDataHolder.getInstance().index) {
                categoryListHolder[CategoryDataHolder.getInstance().index-1] = it
            } else if (categoryListHolder.count() == CategoryDataHolder.getInstance().index-1) {
                categoryListHolder.add(it)
            }
        }
        
        setUpModel()
        setUpListeners()
        setRecyclerViewAdapter()
        requestNetworkCall()
    }

    private fun requestNetworkCall() {

        mFirebaseRealtimeModel.getNewProducts { isSuccess, data ->
            data?.let{newItemsList = it}
        }

        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            isFound = false
            if (isSuccess) {
                data?.let { categoryVOList = it }
                data?.forEach { mainCategory ->
                    if (!isFound) {
                        findSelectedCategory(mainCategory, subCategoryVO)
                    }
                }
                subCategoryVO?.let {
                    mSubCategoryAdapter.setNewData(it.subCategories.values.toList())
                }
            }
        }

    }

    private fun setUpListeners() {

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun findSelectedCategory(rootCategoryVO: CategoryVO, currentCategoryVO: CategoryVO?) {
        if (rootCategoryVO.id == currentCategoryVO?.id) {
            isFound = true
            subCategoryVO = rootCategoryVO
        }

        for (subCategory in rootCategoryVO.subCategories.values) {
            findSelectedCategory(subCategory, currentCategoryVO)
        }
    }

    private fun setRecyclerViewAdapter(){
        binding.tvHeading.text = subCategoryVO?.title

        // Sub Categories Recycler View
        binding.rvSubCategories.layoutManager = GridLayoutManager(this,2)
        binding.rvSubCategories.adapter = mSubCategoryAdapter

    }

    override fun onBackPressed() {
        CategoryDataHolder.getInstance().index -= 1
        categoryListHolder.removeLast()
        super.onBackPressed()
    }

    override fun onTapMore(categoryVO: CategoryVO, type: String) {

        val newProductVO = NewProductVO(id = categoryVO.id)

        if (type == "Child") {
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
}