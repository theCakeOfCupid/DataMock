package com.james.datamock.location

import android.os.IBinder
import android.os.IInterface
import android.util.Log
import com.james.datamock.DataMock
import com.james.datamock.base.BaseBinderInvocationHandler
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author james
 * @date :2021/1/9 20:05
 */
class LocationBinderInvocationHandler(iBinder: IBinder) : BaseBinderInvocationHandler(iBinder) {

    companion object{
        /**
         * IInterface路径
         */
        private const val IINTERFACE_PATH = "android.location.ILocationManager"
    }


    override fun getIInterface(): Class<*>? {
        return ReflectionHelper.getClassByName(IINTERFACE_PATH)
    }

    override fun getActuallyInvocationHandler(originIBinder: IBinder): BaseDataManagerInvocationHandler {
        return LocationManagerInvocationHandler(originIBinder)
    }

}