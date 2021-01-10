package com.james.datamock.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.james.datamock.DataMock
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * @author james
 * @date :2021/1/10 12:25
 */
class LocationListenerInvocationHandler(val originLocationListener: LocationListener?) :
    InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if (!DataMock.isEnableMockCoordinate()){
            return method?.invoke(originLocationListener, *args ?: arrayOf()) ?: Unit
        }
        if (method?.name == "onLocationChanged") {
            val mockData = Location(LocationManager.NETWORK_PROVIDER)
            mockData.time = System.currentTimeMillis()
            mockData.longitude = DataMock.mockLon
            mockData.latitude = DataMock.mockLat
            originLocationListener?.onLocationChanged(mockData)
            return Unit
        }
        return method?.invoke(originLocationListener, *args ?: arrayOf()) ?: Unit
    }
}