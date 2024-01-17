package com.aristo.admin.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aristo.admin.data.model.FirebaseDatabaseModelImpl
import com.aristo.admin.databinding.ActivityAddPointBinding
import com.aristo.admin.data.vos.UserVO
import com.aristo.admin.view.Notification.NotificationData
import com.aristo.admin.view.Notification.PushNotification
import com.aristo.admin.view.Notification.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPointActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivityAddPointBinding.inflate(layoutInflater)}
    private val mFirebaseDatabaseModel by lazy { FirebaseDatabaseModelImpl}

    private lateinit var userVO : UserVO
    private var point : Int? = null
    private var deviceToken : String = ""
    private var notificationsSentCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userVO = intent.getSerializableExtra("user") as UserVO
        point = userVO.point

        bindData()
        setUpModel()
        setUpListeners()
    }

    private fun bindData() {
        mFirebaseDatabaseModel.fetchSpecificUserDeviceTokens(userVO.userId.toString()) {success, data ->
            if (success) {
                deviceToken = data
            }
        }
        binding.tvId.text = "${userVO.userId}"
        binding.tvCurrentPoint.text = "${userVO.point}"
    }

    private fun setUpListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.btnAddPoint.setOnClickListener {

            binding.addProgress.visibility = View.VISIBLE

            if (binding.etPoint.text.isNotEmpty()){
                point = binding.etPoint.text.toString().toInt() + userVO.point

                mFirebaseRealtimeModel.addPoints(point!!, userVO.phone){ success, message ->

                    if (success) {
                        binding.tvCurrentPoint.text = "$point"
                        sendNotification()
                        Toast.makeText(this, "Points Added Successfully.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Points Added Failed.", Toast.LENGTH_LONG).show()
                        binding.addProgress.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(this, "Enter points to add", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendNotification(){
        val title = "Point Added"
        val message = "သင့် account သို့ " + binding.etPoint.text.toString() + " points ထည့်သွင်းလိုက်ပါပြီ"
        notificationsSentCount = 0

        if (message.isNotEmpty()) {

            PushNotification(
                NotificationData(title, "", message),
                deviceToken
            ).also {
                sendNotification(it)
            }
        }
    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d("Response:", "Notification Sent Successfully")
            } else {
                // Handle the case when notification sending fails
                Log.e("Response:", "Failed to send notification: ${response.errorBody().toString()}")

                // Perform actions accordingly, e.g., show an error message to the user
//                runOnUiThread {
//                    // Show an error message to the user
//                    binding.progressBar.visibility = View.GONE
//                    showErrorToast("Failed to send notification")
//                }
            }
        } catch(e: Exception) {
            // Handle the exception, for example, network issues
            Log.e("Response:", "Exception: ${e.toString()}")

            // Perform actions accordingly, e.g., show an error message to the user
//            runOnUiThread {
//                // Show an error message to the user
//                binding.progressBar.visibility = View.GONE
//                showErrorToast("Failed to send notification")
//            }
        }
    }
}