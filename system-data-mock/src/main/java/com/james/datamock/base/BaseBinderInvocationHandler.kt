package com.james.datamock.base

import android.os.IBinder
import android.os.IInterface
import android.util.Log
import com.james.datamock.DataMock
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author james
 * @date :2021/1/9 23:01
 */
abstract class BaseBinderInvocationHandler(
    /**
     * 真实的IBinder
     */
    protected val originIBinder: IBinder
) : InvocationHandler {

    abstract fun getIInterface(): Class<*>?

    abstract fun getActuallyInvocationHandler(originIBinder: IBinder): BaseDataManagerInvocationHandler

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if (null == getIInterface()) {
            Log.d(DataMock.TAG, "get IIterface failed")
            return method?.invoke(originIBinder, args)!!
        }
        Log.d(DataMock.TAG, "hook binder success")
        //这里只能hook queryLocalInterface,不能hook asInterface,因为在SystemServiceRegistry里面是通过ILocationManager.Stub.asInterface(b)获取IInterface的
        if (method?.name == "queryLocalInterface") {
            //这里要么是BinderProxy,要么是本地Binder对象，即Binder
            return Proxy.newProxyInstance(
                originIBinder.javaClass.classLoader,
                arrayOf(getIInterface()!!, IBinder::class.java, IInterface::class.java),
                getActuallyInvocationHandler(originIBinder)
            )
        }
        return method?.invoke(originIBinder, *args ?: arrayOf()) ?: Unit
    }
}