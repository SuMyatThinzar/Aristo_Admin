package com.aristo.admin.data.vos

data class NewProductVO (
    var id: String = "",
    val timeStamp: Long = System.currentTimeMillis(),
    var productImage: String? = null,
)