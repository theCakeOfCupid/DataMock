package com.james.datamock.wifi

import android.os.IBinder
import com.james.datamock.base.BaseBinderInvocationHandler
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper

/**
 * @author james
 * @date :2021/1/10 13:54
 */
class WifiBinderInvocationHandler(originIBinder: IBinder) :
    BaseBinderInvocationHandler(originIBinder) {

    companion object {
        /**
         * IInterface路径
         */
        private const val IINTERFACE_PATH = "android.net.wifi.IWifiManager"
    }

    override fun getIInterface(): Class<*>? {
        return ReflectionHelper.getClassByName(IINTERFACE_PATH)
    }

    override fun getActuallyInvocationHandler(originIBinder: IBinder): BaseDataManagerInvocationHandler {
        return WifiManagerInvocationHandler(originIBinder)
    }
}