package com.aristo.admin.data.model

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.aristo.admin.data.vos.AdminVO
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.data.vos.UserVO

interface FirebaseRealtimeModel {

    fun uploadDataToFirebase(
        context: Context,
        categoryVO: CategoryVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun updateDataToFirebase(
        activity: Activity,
        categoryVO : CategoryVO,
        isWithImage : Boolean,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun uploadImageToFirebase(
        imageUri: Uri,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun getMainCategoryData(
        completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit
    )

    fun getCategoriesDatas(
        activity: Activity,
        completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit
    )

    fun addAdmin(
        adminVO: AdminVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun getAdmin(
        completionHandler: (Boolean, Any?) -> Unit
    )

    fun updateCategory(
        categoryVO: CategoryVO,
        pathList: ArrayList<CategoryVO>,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun deleteCategory(
        categoryVO: CategoryVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun deleteMainCategory(
        mainCatID : String,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun addNewProduct(
        newProductVO: NewProductVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun removeNewProduct(
        newProductVO: NewProductVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun getNewProducts(
        completionHandler: (Boolean, ArrayList<NewProductVO>?) -> Unit
    )

    fun getCustomerIdList(
        completionHandler: (Boolean, ArrayList<UserVO>?) -> Unit
    )

    fun addPoints(
        point : Int,
        phoneNo : String,
        completionHandler: (Boolean, String?) -> Unit
    )
}
