package com.james.datamock.helper

import android.util.Log
import com.james.datamock.DataMock
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author james
 * @date :2021/1/10 10:15
 */
object ReflectionHelper {

    fun getClassByName(name: String): Class<*>? {
        var result: Class<*>? = null
        try {
            result = Class.forName(name)
        } catch (e: Exception) {
            Log.e(DataMock.TAG, e.message, e)
        }
        return result
    }

    fun getDeclaredMethod(clazz: Class<*>, methodName: String): Method {
        val javaClass = Class.forName("java.lang.Class")
        val getDeclaredMethod = javaClass.getDeclaredMethod("getDeclaredMethod", String::class.java)
        return getDeclaredMethod.invoke(clazz, methodName) as Method
    }

    fun getDeclaredField(clazz: Class<*>, fieldName: String): Field {
        val javaClass = Class.forName("java.lang.Class")
        val getDeclaredField = javaClass.getDeclaredMethod("getDeclaredField", String::class.java)
        return getDeclaredField.invoke(clazz, fieldName) as Field
    }
}