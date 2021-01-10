package com.james.datamock.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import com.james.datamock.DataMock
import com.james.datamock.base.BaseDataManagerInvocationHandler
import com.james.datamock.helper.ReflectionHelper
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author james
 * @date :2021/1/9 20:26
 */
class LocationManagerInvocationHandler(origin: IBinder) : BaseDataManagerInvocationHandler(origin) {
    companion object {
        /**
         * Stub路径
         */
        private const val STUB_PATH: String = "android.location.ILocationManager\$Stub"
    }

    override fun getStubClass(): Class<*>? {
        return ReflectionHelper.getClassByName(STUB_PATH)
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        Log.d(DataMock.TAG, "hook locationManager success")
        //不做处理
        if (!DataMock.isEnableMockCoordinate()) {
            return super.invoke(proxy, method, args)
        }
        val mockData = Location(LocationManager.NETWORK_PROVIDER)
        mockData.time = System.currentTimeMillis()
        mockData.longitude = DataMock.mockLon
        mockData.latitude = DataMock.mockLat
        //mock就走这里
        if (method?.name == "getLastLocation") {
            return mockData
        }
        if (method?.name == "requestLocationUpdates") {
            try {
                getListenerTransportAndReplaceLocationListener(args)
            } catch (e: Exception) {
                Log.e(DataMock.TAG, "compat issue happened", e)
            }
        }
        return super.invoke(proxy, method, args)
    }

    /**
     * hook locationListener,这里可能会有版本兼容问题
     */
    private fun getListenerTransportAndReplaceLocationListener(args: Array<out Any>?) {
        args?.forEach {
            if (it?.javaClass?.simpleName == "ListenerTransport") {
                val locationListenerField =
                    ReflectionHelper.getDeclaredField(it.javaClass, "mListener")
                locationListenerField.isAccessible = true
                val originLocationListener = locationListenerField.get(it) as LocationListener
                val proxyLocationListener: LocationListener = Proxy.newProxyInstance(
                    getStubClass()?.classLoader, arrayOf(LocationListener::class.java),
                    LocationListenerInvocationHandler(originLocationListener)
                ) as LocationListener
                locationListenerField.set(it, proxyLocationListener)
                locationListenerField.isAccessible = false
            }
        }
    }
}