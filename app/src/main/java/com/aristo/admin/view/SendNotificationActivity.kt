package com.aristo.admin.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.aristo.admin.R
import com.aristo.admin.data.model.FirebaseDatabaseModelImpl
import com.aristo.admin.databinding.ActivitySendNotificationBinding
import com.aristo.admin.view.Notification.NotificationData
import com.aristo.admin.view.Notification.PushNotification
import com.aristo.admin.view.Notification.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendNotificationActivity : AbstractBaseActivity() {

    private val binding by lazy { ActivitySendNotificationBinding.inflate(layoutInflater) }
    private val mFirebaseDatabaseModel by lazy { FirebaseDatabaseModelImpl }

    private var userTokenLists : ArrayList<String> = arrayListOf()
    private var notificationsSentCount = 0
    private var selectedImageUri: Uri? = null

    companion object {
        const val TOPIC = "/topics/myTopic2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpModel()
        setUpListeners()
        requestData()
    }

    private fun setUpListeners() {

        binding.btnSend.setOnClickListener {
            val title = "New Arrival"
            val message = binding.tvNotiMessage.text.toString()
            notificationsSentCount = 0

            binding.progressBar.visibility = View.VISIBLE

            selectedImageUri?.let { it1 ->
                mFirebaseRealtimeModel.uploadImageToFirebase(it1) { isSuccess, imageUrl ->
                    // Image uploaded successfully, set the imageUrl in the category object
                    if (isSuccess && imageUrl != null) {
                        selectedImageUri = imageUrl.toUri()

                        for (token in userTokenLists){
                            if(message.isNotEmpty()) {

                                PushNotification(
                                    NotificationData(title,selectedImageUri.toString(), message),
                                    token
                                ).also {
                                    sendNotification(it)
                                }
                            }
                        }
                    }
                }
            }

            if (selectedImageUri == null){
                for (token in userTokenLists){
                    if(message.isNotEmpty()) {

                        PushNotification(
                            NotificationData(title,selectedImageUri.toString(), message),
                            token
                        ).also {
                            sendNotification(it)
                        }
                    }
                }
            }

        }

        binding.tvNotiMessage.addTextChangedListener {

            if (binding.tvNotiMessage.text.isEmpty()){
                binding.btnSend.isEnabled = false
                binding.btnSend.alpha = 0.5f
            }
            else{
                binding.btnSend.isEnabled = true
                binding.btnSend.alpha = 1f
            }
        }

        binding.ibBack.setOnClickListener {
            finish()
        }

        // Set selected image url
        val galleryImage = registerForActivityResult(
            ActivityResultContracts. GetContent()
        ) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.ivProduct.setImageURI(uri)
            }
        }

        // When select image from gallery
        binding.ivProduct.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("Response:", "Notification Sent Successfully")
            } else {
                // Handle the case when notification sending fails
                Log.e("Response:", "Failed to send notification: ${response.errorBody().toString()}")

                // Perform actions accordingly, e.g., show an error message to the user
                runOnUiThread {
                    // Show an error message to the user
                    binding.progressBar.visibility = View.GONE
                    showErrorToast("Failed to send notification")
                }
            }
        } catch(e: Exception) {
            // Handle the exception, for example, network issues
            Log.e("Response:", "Exception: ${e.toString()}")

            // Perform actions accordingly, e.g., show an error message to the user
            runOnUiThread {
                // Show an error message to the user
                binding.progressBar.visibility = View.GONE
                showErrorToast("Failed to send notification")
            }
        } finally {
            notificationsSentCount++

            // Check if all notifications are sent
            if (notificationsSentCount == userTokenLists.size) {
                // All notifications are sent, perform your action here
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    showErrorToast("Notification sent successfully.")
                    binding.tvNotiMessage.setText("")
                    binding.btnSend.isEnabled = false
                    selectedImageUri = null
                    binding.ivProduct.setImageURI(null)
                    binding.ivProduct.setImageResource(R.drawable.ic_placeholder)
                    binding.btnSend.alpha = 0.5f
                }
            }
        }
    }

    private fun requestData() {

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        mFirebaseDatabaseModel.fetchUserDeviceTokens { isSuccess, tokens ->

            if (tokens != null) {
                userTokenLists = tokens
            }
            Log.d("\"Response:", "Response Token List $userTokenLists")
        }

    }

    // Function to show an error message to the user (you can customize this based on your UI)
    private fun showErrorToast(message: String) {
        Toast.makeText(this@SendNotificationActivity, message, Toast.LENGTH_SHORT).show()
    }

}