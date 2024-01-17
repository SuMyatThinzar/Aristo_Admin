package com.aristo.admin.data.model

import com.aristo.admin.network.FirebaseDatabaseApi
import com.aristo.admin.network.FirebaseRealtimeApi

abstract class AbstractBaseModel {
    val mFirebaseRealtimeApi : FirebaseRealtimeApi = FirebaseRealtimeApi()
    val mFirebaseDatabaseApi : FirebaseDatabaseApi = FirebaseDatabaseApi()
}