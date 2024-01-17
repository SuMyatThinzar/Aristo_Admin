package com.aristo.admin.network

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.aristo.admin.data.dataholders.AdminDataHolder
import com.aristo.admin.data.dataholders.CategoryDataHolder
import com.aristo.admin.data.dataholders.DataListHolder
import com.aristo.admin.utils.components.SharedPreferencesManager
import com.aristo.admin.data.vos.AdminVO
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.UserVO
import com.aristo.admin.data.vos.NewProductVO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseRealtimeApi {
        private val database = FirebaseDatabase.getInstance()
        private val categoriesRef: DatabaseReference = database.getReference("Products")
        private var referencePathUpDateData = "Products/Categories/"
        
        fun uploadDataToFirebase(categoryVO: CategoryVO, completionHandler: (Boolean, String?) -> Unit) {

            val categoryId = categoriesRef.push().key
            SharedPreferencesManager.saveCategoryId(categoryId!!)
            categoryVO.id = categoryId

            // Upload image to Firebase Storage
            uploadImageToFirebase(categoryVO.imageURL.toUri()) { isSuccess, imageUrl ->
                if (isSuccess) {
                    // Image uploaded successfully, set the imageUrl in the category object
                    if (imageUrl != null) {
                        categoryVO.imageURL = imageUrl
                    }

                    // Store category data in Firebase Realtime Database
                    categoriesRef.child("Categories").child(categoryId).setValue(categoryVO)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                completionHandler(true, null) // Success, pass true and no error message
                            } else {
                                val errorMessage = task.exception?.message ?: "Unknown error occurred"
                                completionHandler(false, errorMessage) // Failure, pass false and the error message
                            }
                        }
                } else {
                    completionHandler(false, imageUrl) // Failure, pass false and the error message
                }
            }
        }

        fun updateDataToFirebase(categoryVO : CategoryVO, isWithImage : Boolean, completionHandler: (Boolean, String?) -> Unit){

            var subCategoryId : String?
            var categoryId = SharedPreferencesManager.getCategoryId()

            if (DataListHolder.getInstance().getSubIDList().isEmpty()){
                subCategoryId = categoriesRef.push().key
                //DataListHolder.getInstance().setSubIDList(subCategoryId!!)
                if (subCategoryId != null) {
                    categoryVO.id = subCategoryId
                }
            }
            else{
                subCategoryId = DataListHolder.getInstance().getSubIDList()[0]
            }

            val subIndexList = DataListHolder.getInstance().getSubIndexList()
            var baseURL = categoriesRef.child("Categories").child(categoryId!!)
            var referencePath = "subCategories/$subCategoryId/"

            //User selected from sub category index
            if (subIndexList.isNotEmpty()){

                subCategoryId = categoriesRef.push().key
                DataListHolder.getInstance().setSubIDList(subCategoryId!!)
                categoryVO.id = subCategoryId

                for(index in 0..<subIndexList.size){
                    referencePath += "subCategories/${DataListHolder.getInstance().getSubIDList()[index + 1]}/"
                }
            }

            var referenceString = baseURL.child(referencePath).toString()
            var restoredReference = Firebase.database.getReferenceFromUrl(referenceString)

            // Upload data with image
            if (isWithImage){
                // Upload image to Firebase Storage
                uploadImageToFirebase(categoryVO.imageURL.toUri()) { isSuccess, imageUrl ->
                    if (isSuccess) {
                        // Image uploaded successfully, set the imageUrl in the category object
                        if (imageUrl != null) {
                            categoryVO.imageURL = imageUrl
                        }

                        postSubCategoryData(categoryVO, restoredReference){ isSuccess, message ->
                            if (isSuccess) {
                                completionHandler(true,null)
                            } else {
                                completionHandler(false, message)
                            }
                        }

                    } else {
                        completionHandler(false, imageUrl) // Failure, pass false and the error message
                    }
                }
            }

            // Upload data with color code
            else{
                postSubCategoryData(categoryVO, restoredReference){ isSuccess, message ->
                    if (isSuccess) {
                        completionHandler(true,null)
                    } else {
                        completionHandler(false, message)
                    }
                }
            }
        }

        private fun postSubCategoryData(categoryVO: CategoryVO, restoredReference : DatabaseReference, completionHandler: (Boolean, String?) -> Unit){
            // Store category data in Firebase Realtime Database
            restoredReference.setValue(categoryVO)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (DataListHolder.getInstance().getSubIDList().size > 1){
                            DataListHolder.getInstance().getSubIDList().removeLast()
                        }
                        completionHandler(true, null)
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error occurred"
                        completionHandler(false, errorMessage)
                    }
                }
        }

        fun uploadImageToFirebase(imageUri: Uri, completionHandler: (Boolean, String?) -> Unit) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
            val uploadTask = imageRef.putFile(imageUri)

            uploadTask.addOnSuccessListener { _ ->
                // Image uploaded successfully, get the download URL
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    completionHandler(true, downloadUri.toString()) // Succes, pass true and download URL
                }
            }.addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Failed to upload image"
                completionHandler(false, errorMessage) // Failure, pass false and the error message
            }
        }

        fun getMainCategoryData(completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit) {

            categoriesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val subCategoriesSnapshot = snapshot.child("Categories")
                    val subCategoriesList: ArrayList<CategoryVO> = ArrayList()

                    for (categorySnapshot in subCategoriesSnapshot.children) {
                        val subCategoryVO = categorySnapshot.getValue(CategoryVO::class.java)
                        subCategoryVO?.let {
                            subCategoriesList.add(it)
                        }
                    }
                    completionHandler(true,subCategoriesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false,null)
                }
            })
        }

        fun getCategoriesData(completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit){

            var mainCatID = SharedPreferencesManager.getCategoryId()
            var baseURL = mainCatID?.let {
                categoriesRef.child("Categories").child(it)
            }
            val subIndexList = DataListHolder.getInstance().getSubIndexList()
            var referencePath = ""

            for(index in 0..<subIndexList.size){
                referencePath += "subCategories/${DataListHolder.getInstance().getSubIDList()[index]}/"
            }

            var referenceString = baseURL?.child(referencePath).toString()
            var categoryRef = Firebase.database.getReferenceFromUrl(referenceString)

            categoryRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val subCategoriesSnapshot = snapshot.child("subCategories")
                    val subCategoriesList: ArrayList<CategoryVO> = ArrayList()

                    for (categorySnapshot in subCategoriesSnapshot.children) {

                        val subCategoryVO = categorySnapshot.getValue(CategoryVO::class.java)
                        subCategoryVO?.let {
                            subCategoriesList.add(it)
                        }
                    }
                    completionHandler(true,subCategoriesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false,null)
                }
            })
        }

        fun addAdmin(adminVO: AdminVO, completionHandler: (Boolean, String?) -> Unit) {

            AdminDataHolder.instance.adminVO = adminVO

            database.reference.child("companyInfo").setValue(adminVO).addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "Successful")
                } else {
                    completionHandler(false, it.exception?.message)
                }
            }
        }

        fun getAdmin(completionHandler: (Boolean, Any?) -> Unit) {

            database.reference.child("companyInfo").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val adminVO = snapshot.getValue(AdminVO::class.java)
                        AdminDataHolder.instance.adminVO = adminVO as AdminVO
                        completionHandler(true, adminVO)
                    } else {
                        completionHandler(false, null)
                    } }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false, error.message)
                }

            })
        }

        fun updateCategory(categoryVO: CategoryVO, pathList: ArrayList<CategoryVO>, completionHandler: (Boolean, String?) -> Unit) {
            referencePathUpDateData = "Products/Categories/"

            pathList.forEach {
                referencePathUpDateData += "${it.id}/subCategories/"
            }
            referencePathUpDateData += categoryVO.id

            val referenceString = database.reference.child(referencePathUpDateData).toString()

            val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)
            restoredReference.setValue(categoryVO).addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "updated")
                } else {
                    completionHandler(false, it.exception?.message)
                }
            }
        }

        fun deleteCategory(categoryVO: CategoryVO, completionHandler: (Boolean, String?) -> Unit) {
            referencePathUpDateData = "Products/Categories/"

            CategoryDataHolder.getInstance().updatedCategoryListVO.forEach {
                referencePathUpDateData += "${it.id}/subCategories/"
            }
            referencePathUpDateData += categoryVO.id

            val referenceString = database.reference.child(referencePathUpDateData).toString()

            val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)
            restoredReference.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "deleted")
                } else {
                    completionHandler(false, it.exception?.message)
                }
            }
        }

        fun deleteMainCategory(mainCatID : String, completionHandler: (Boolean, String?) -> Unit) {
            referencePathUpDateData = "Products/Categories/$mainCatID"

            val referenceString = database.reference.child(referencePathUpDateData).toString()

            val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)
            restoredReference.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "Deleted")
                } else {
                    completionHandler(false, "Failed")
                }
            }
        }

        fun addNewProduct(newProductVO: NewProductVO, completionHandler: (Boolean, String?) -> Unit) {

                referencePathUpDateData = "Products/NewProducts/${newProductVO.id}"

                val referenceString = database.reference.child(referencePathUpDateData).toString()

                val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)

                getNewProducts { success, newList ->
                    if(success) {
                        if (newList?.count()!! > 8) {
                            var sort = newList.sortedByDescending { it.timeStamp }
                            removeNewProduct(sort.last()) { _, _ ->}
                        }
                    }
                }

                restoredReference.setValue(newProductVO).addOnCompleteListener {
                    if (it.isSuccessful) {
                        completionHandler(true, null)
                    } else {
                        completionHandler(false, it.exception?.message)
                    }
                }
            }

        fun removeNewProduct(newProductVO: NewProductVO, completionHandler: (Boolean, String?) -> Unit) {

            referencePathUpDateData = "Products/NewProducts/${newProductVO.id}"

            val referenceString = database.reference.child(referencePathUpDateData).toString()

            val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)
            restoredReference.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "removed")
                } else {
                    completionHandler(false, it.exception?.message)
                }
            }
        }

        fun getNewProducts(completionHandler: (Boolean, ArrayList<NewProductVO>?) -> Unit) {

            categoriesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val categoriesSnapshot = snapshot.child("NewProducts")
                    val newProductVOList: ArrayList<NewProductVO> = ArrayList()

                    for (categorySnapshot in categoriesSnapshot.children) {

                        val newProductsVO = categorySnapshot.getValue(NewProductVO::class.java)
                        newProductsVO?.let {
                            newProductVOList.add(it)
                        }
                    }
                    completionHandler(true, newProductVOList)
                }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false, null)
                }

            })
        }

        fun getCustomerIdList(completionHandler: (Boolean, ArrayList<UserVO>?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val categoriesRef: DatabaseReference = database.getReference("Users")

            categoriesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val customerList: ArrayList<UserVO> = ArrayList()

                    for (customerSnapshot in snapshot.children) {
                        val customer = customerSnapshot.getValue(UserVO::class.java)

                        if (customer != null) {
                            customerList.add(customer)
                        }
                    }

                    completionHandler(true, customerList)
                }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false, null)
                }
            })
        }

        fun addPoints(point : Int, phoneNo : String, completionHandler: (Boolean, String?) -> Unit) {


            val userReferencePath = "Users/$phoneNo/point"

            val referenceString = database.reference.child(userReferencePath).toString()

            val restoredReference = Firebase.database.getReferenceFromUrl(referenceString)


            Log.d("addPoints", "addPoints: $phoneNo $restoredReference")

            restoredReference.setValue(point).addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true, "updated")
                } else {
                    completionHandler(false, it.exception?.message)
                }
            }
        }

}