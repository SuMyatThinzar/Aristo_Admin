package com.aristo.admin.data.dataholders

import android.util.Log

class DataListHolder private constructor() {

    private val subIndexList: ArrayList<Int> = ArrayList()
    private val subIDList: ArrayList<String> = ArrayList()
    private val childList: ArrayList<String> = ArrayList()
    private val isType: ArrayList<Boolean> = ArrayList()

    fun setSubIndexList(data: Int) {

        Log.d("setSubIndexList", "setSubIndexList: $data")
        //subIndexList.clear()
        subIndexList.add(data)
    }

    fun getSubIndexList(): ArrayList<Int> {
        return subIndexList
    }

    fun setSubIDList(data: String) {

        subIDList.add(data)
    }

    fun getSubIDList(): ArrayList<String> {
        return subIDList
    }

    fun setChildPath(data: String) {

        childList.add(data)
    }

    fun getChildPath(): ArrayList<String> {
        return childList
    }

    fun setIsType(data: Boolean) {

        isType.add(data)
    }

    fun getIsType(): ArrayList<Boolean> {
        return isType
    }

    fun deleteAll(){
        subIDList.clear()
        subIndexList.clear()
        isType.clear()
    }


    companion object {
        private var instance: DataListHolder? = null

        fun getInstance(): DataListHolder {
            if (instance == null) {
                instance = DataListHolder()
            }
            return instance!!
        }
    }
}
