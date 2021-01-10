package com.james.datamock.telephony

import android.os.IBinder
import com.james.datamock.base.BaseBinderInvocationHandler
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper

/**
 * @author james
 * @date :2021/1/10 13:54
 */
class TelephonyBinderInvocationHandler(originIBinder: IBinder) :
    BaseBinderInvocationHandler(originIBinder) {

    companion object {
        /**
         * IInterface路径
         */
        private const val IINTERFACE_PATH = "com.android.internal.telephony.ITelephony"
    }

    override fun getIInterface(): Class<*>? {
        return ReflectionHelper.getClassByName(IINTERFACE_PATH)
    }

    override fun getActuallyInvocationHandler(originIBinder: IBinder): BaseDataManagerInvocationHandler {
        return TelephonyInvocationHandler(originIBinder)
    }
}