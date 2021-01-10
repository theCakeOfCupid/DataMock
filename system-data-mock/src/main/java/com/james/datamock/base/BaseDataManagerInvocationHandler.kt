package com.james.datamock.base

import android.os.IBinder
import android.os.IInterface
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * @author james
 * @date :2021/1/9 20:28
 */
abstract class BaseDataManagerInvocationHandler(private var origin: IBinder) : InvocationHandler {
    /**
     * 这里是最原始的IInterface（ILocationManager...）,有可能是包装的Proxy，也有可能是本地Binder对象
     */
    private lateinit var iInterface: IInterface

    init {
        initInterface()
    }

    private fun initInterface() {
        iInterface = getStubClass()?.getDeclaredMethod("asInterface", IBinder::class.java)
            ?.invoke(null, origin) as IInterface
    }

    abstract fun getStubClass(): Class<*>?

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        return method?.invoke(iInterface, *args ?: arrayOf()) ?: Unit
    }
}