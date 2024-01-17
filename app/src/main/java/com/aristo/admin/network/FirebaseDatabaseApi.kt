package com.aristo.admin.network

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseApi {

    // For send notification
    fun fetchUserDeviceTokens(completionHandler: (Boolean, ArrayList<String>?) -> Unit){

        val databaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val tokenList: ArrayList<String> = ArrayList()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // whenever data at this location is updated.
                for (childSnapshot in dataSnapshot.children) {
                    val token = childSnapshot.child("token").getValue(String::class.java)
                    // Handle the token data as needed
                    if (token != null) {
                        tokenList.add(token)
                    }

                }

                Log.d("Token", "Token List $tokenList")
                completionHandler(true,tokenList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", databaseError.toException())
                completionHandler(false,null)
            }
        })

    }

    // For Point Add
    fun fetchSpecificUserDeviceTokens(id : String ,completionHandler: (Boolean, String) -> Unit){

        val databaseReference = FirebaseDatabase.getInstance().getReference("Tokens")

        databaseReference.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val token = dataSnapshot.child("token").getValue(String::class.java)
                    // Use the retrieved token as needed
                    Log.d("FirebaseToken", "Token: $token")
                    if (token != null) {
                        completionHandler(true,token)
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", databaseError.toException())
                completionHandler(false,"")
            }
        })

    }
}