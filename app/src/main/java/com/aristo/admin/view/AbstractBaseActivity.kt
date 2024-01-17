package com.aristo.admin.view

import androidx.appcompat.app.AppCompatActivity
import com.aristo.admin.data.model.FirebaseRealtimeModel
import com.aristo.admin.data.model.FirebaseRealtimeModelImpl

abstract class AbstractBaseActivity : AppCompatActivity()  {
    lateinit var mFirebaseRealtimeModel: FirebaseRealtimeModel

    fun setUpModel() {
        mFirebaseRealtimeModel = FirebaseRealtimeModelImpl
    }
}