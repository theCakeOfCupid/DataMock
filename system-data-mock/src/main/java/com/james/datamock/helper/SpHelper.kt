package com.james.datamock.helper

import android.content.Context
import android.content.SharedPreferences

/**
 * @author james
 * @date :2021/1/10 10:30
 */
object SpHelper {

    private var mSp:SharedPreferences? = null

    fun init(context:Context){
        mSp = context.getSharedPreferences("DataMock",Context.MODE_PRIVATE)
    }

    fun setBoolean(key: String?, value: Boolean) {
        mSp?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getBoolean(key: String?, value: Boolean): Boolean {
        return mSp?.getBoolean(key, value)?:false
    }

    fun setString(key: String?, value: String) {
        mSp?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String?, value: String): String {
        return mSp?.getString(key, value)?:""
    }

}