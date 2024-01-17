package com.aristo.admin.data.vos

import java.io.Serializable

data class CategoryVO(
    var id: String = "",
    val title: String ="",
    var price: Int = 0,
    var imageURL: String = "",
    var new: Boolean = false,
    val colorCode : String = "",
    val type : String = "",
    val timeStamp : Long = System.currentTimeMillis(),
    //var subCategories: ArrayList<Category> = ArrayList()
    var subCategories: Map<String, CategoryVO> = mapOf()
): Serializable{
}
