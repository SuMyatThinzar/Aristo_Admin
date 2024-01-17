package com.aristo.admin.data.model

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.aristo.admin.data.vos.AdminVO
import com.aristo.admin.data.vos.CategoryVO
import com.aristo.admin.data.vos.NewProductVO
import com.aristo.admin.data.vos.UserVO

object FirebaseRealtimeModelImpl : AbstractBaseModel(), FirebaseRealtimeModel {

    override fun uploadDataToFirebase(
        context: Context,
        categoryVO: CategoryVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.uploadDataToFirebase(context, categoryVO, completionHandler)
    }

    override fun updateDataToFirebase(
        activity: Activity,
        categoryVO: CategoryVO,
        isWithImage: Boolean,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.updateDataToFirebase(activity, categoryVO, isWithImage, completionHandler)
    }

    override fun uploadImageToFirebase(
        imageUri: Uri,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.uploadImageToFirebase(imageUri, completionHandler)
    }

    override fun getMainCategoryData(
        completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit
    ) {
        mFirebaseRealtimeApi.getMainCategoryData(completionHandler)
    }

    override fun getCategoriesDatas(
        activity: Activity,
        completionHandler: (Boolean, ArrayList<CategoryVO>?) -> Unit
    ) {
        mFirebaseRealtimeApi.getCategoriesDatas(activity, completionHandler)
    }

    override fun addAdmin(
        adminVO: AdminVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.addAdmin(adminVO, completionHandler)
    }

    override fun getAdmin(
        completionHandler: (Boolean, Any?) -> Unit
    ) {
        mFirebaseRealtimeApi.getAdmin(completionHandler)
    }

    override fun updateCategory(
        categoryVO: CategoryVO,
        pathList: ArrayList<CategoryVO>,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.updateCategory(categoryVO, pathList, completionHandler)
    }

    override fun deleteCategory(
        categoryVO: CategoryVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.deleteCategory(categoryVO, completionHandler)
    }

    override fun deleteMainCategory(
        mainCatID: String,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.deleteMainCategory(mainCatID, completionHandler)
    }

    override fun addNewProduct(
        newProductVO: NewProductVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.addNewProduct(newProductVO, completionHandler)
    }

    override fun removeNewProduct(
        newProductVO: NewProductVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.removeNewProduct(newProductVO, completionHandler)
    }

    override fun getNewProducts(completionHandler: (Boolean, ArrayList<NewProductVO>?) -> Unit) {
        mFirebaseRealtimeApi.getNewProducts(completionHandler)
    }

    override fun getCustomerIdList(completionHandler: (Boolean, ArrayList<UserVO>?) -> Unit) {
        mFirebaseRealtimeApi.getCustomerIdList(completionHandler)
    }

    override fun addPoints(
        point: Int,
        phoneNo: String,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.addPoints(point, phoneNo, completionHandler)
    }

}