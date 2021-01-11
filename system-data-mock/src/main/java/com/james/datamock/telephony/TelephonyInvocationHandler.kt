package com.james.datamock.telephony

import android.os.IBinder
import com.james.datamock.DataMock
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper
import java.lang.reflect.Method

/**
 * @author james
 * @date :2021/1/10 13:54
 */
class TelephonyInvocationHandler(originIBinder: IBinder) :
    BaseDataManagerInvocationHandler(originIBinder) {

    companion object {
        /**
         * Stub路径
         */
        private const val STUB_PATH: String = "com.android.internal.telephony.ITelephony\$Stub"
    }

    override fun getStubClass(): Class<*>? {
       return ReflectionHelper.getClassByName(STUB_PATH)
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        if (!DataMock.isEnableMockCellInfo()){
            return super.invoke(proxy, method, args)
        }
        if (method?.name == "getAllCellInfo"){
            return DataMock.mockCellInfoList?:super.invoke(proxy, method, args)
        }
        return super.invoke(proxy, method, args)
    }

}