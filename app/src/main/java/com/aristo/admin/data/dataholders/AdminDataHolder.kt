package com.aristo.admin.data.dataholders

import com.aristo.admin.data.vos.AdminVO

class AdminDataHolder private constructor(){

    var adminVO : AdminVO? = null

    companion object {
        val instance: AdminDataHolder = AdminDataHolder()
    }

}