package com.aristo.admin.data.dataholders

import com.aristo.admin.data.vos.CategoryVO

class CategoryDataHolder private constructor() {

    val updatedCategoryListVO: ArrayList<CategoryVO> = ArrayList()
    var index: Int = 0
    var countingType: String = ""

    companion object {
        private var instance: CategoryDataHolder? = null

        fun getInstance(): CategoryDataHolder {
            if (instance == null) {
                instance = CategoryDataHolder()
            }
            return instance!!
        }
    }
}
