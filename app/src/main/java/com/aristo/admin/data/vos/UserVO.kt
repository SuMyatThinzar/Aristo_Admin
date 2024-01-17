package com.aristo.admin.data.vos

import java.io.Serializable

data class UserVO (
    val address : String = "",
    val password : String = "",
    val phone : String = "",
    val point : Int = 0,
    val userId : Long = 0,
    val userName : String = ""
): Serializable {
}