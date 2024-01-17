package com.aristo.admin.data.model

object FirebaseDatabaseModelImpl : FirebaseDatabaseModel, AbstractBaseModel() {

    override fun fetchUserDeviceTokens(completionHandler: (Boolean, ArrayList<String>?) -> Unit) {
        mFirebaseDatabaseApi.fetchUserDeviceTokens(completionHandler)
    }

    override fun fetchSpecificUserDeviceTokens(
        id: String,
        completionHandler: (Boolean, String) -> Unit
    ) {
        mFirebaseDatabaseApi.fetchSpecificUserDeviceTokens(id, completionHandler)
    }
}