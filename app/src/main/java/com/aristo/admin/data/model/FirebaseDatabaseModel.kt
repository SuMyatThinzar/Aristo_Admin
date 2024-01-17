package com.aristo.admin.data.model

interface FirebaseDatabaseModel {

    fun fetchUserDeviceTokens(
        completionHandler: (Boolean, ArrayList<String>?) -> Unit
    )

    fun fetchSpecificUserDeviceTokens(
        id : String,
        completionHandler: (Boolean, String) -> Unit
    )
}