package com.james.datamock.wifi

import android.os.IBinder
import com.james.datamock.DataMock
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper
import java.lang.reflect.Method

/**
 * @author james
 * @date :2021/1/10 13:54
 */
class WifiManagerInvocationHandler(originIBinder: IBinder) :
    BaseDataManagerInvocationHandler(originIBinder) {

    companion object {
        /**
         * Stub路径
         */
        private const val STUB_PATH: String = "android.net.wifi.IWifiManager\$Stub"
    }

    override fun getStubClass(): Class<*>? {
       return ReflectionHelper.getClassByName(STUB_PATH)
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        if (!DataMock.isEnableMockWifi()){
            return super.invoke(proxy, method, args)
        }
        if (method?.name == "getScanResults"){
            return DataMock.mockScanResultList?:super.invoke(proxy, method, args)
        }
        return super.invoke(proxy, method, args)
    }
}