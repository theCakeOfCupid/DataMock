package com.james.datamock

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.ScanResult
import android.os.IBinder
import android.telephony.CellInfo
import android.util.Log
import androidx.core.graphics.scaleMatrix
import com.james.datamock.helper.Reflection
import com.james.datamock.helper.ReflectionHelper
import com.james.datamock.location.LocationBinderInvocationHandler
import com.james.datamock.helper.SpHelper
import com.james.datamock.telephony.TelephonyBinderInvocationHandler
import com.james.datamock.wifi.WifiBinderInvocationHandler
import java.lang.reflect.Proxy

/**
 * @author james
 * @date :2021/1/9 19:55
 */
object DataMock {
    private const val ENABLE_MOCK_COORDINATE = "enable_mock_coordinate"

    private const val ENABLE_MOCK_WIFI = "enable_mock_wifi"

    private const val ENABLE_MOCK_CELL_INFO = "enable_mock_cell_info"

    private const val MOCKED_COORDINATE = "mocked_coordinate"

    var mockLon = 0.0

    var mockLat = 0.0

    var mockScanResultList :MutableList<ScanResult>? = null

    var mockCellInfoList :MutableList<CellInfo>? = null

    const val TAG = "DataMock"

   private const val SYSTEM_MANAGER_PATH = "android.os.ServiceManager"

    private  var serviceManagerClass:Class<*>? = null

    fun init(context: Context) {
        try {
            Reflection.unseal(context)
            serviceManagerClass = ReflectionHelper.getClassByName(SYSTEM_MANAGER_PATH)
            SpHelper.init(context)
            initHook()
        } catch (e: Exception) {
            Log.e(TAG, "init failed", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun initHook() {
        val sCacheField = serviceManagerClass?.getDeclaredField("sCache")
        sCacheField?.isAccessible = true
        val sCache: MutableMap<String, IBinder> = sCacheField?.get(null) as MutableMap<String, IBinder>
        sCacheField.isAccessible = false
        hookCoordinate(sCache)
        hookWifi(sCache)
        hookCellInfo(sCache)
    }

    /**
     * hook经纬度
     */
    private fun hookCoordinate(sCache: MutableMap<String, IBinder>){
        var originLocationService = sCache[Context.LOCATION_SERVICE]
        if (null == originLocationService) {
            Log.d(TAG, "locationManager not cached")
            val getService = serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
            originLocationService = getService?.invoke(null, Context.LOCATION_SERVICE) as IBinder
        }
        sCache[Context.LOCATION_SERVICE] = Proxy.newProxyInstance(
            serviceManagerClass?.classLoader,
            arrayOf(IBinder::class.java),
            LocationBinderInvocationHandler(originLocationService)
        ) as IBinder
    }

    /**
     * hook Wifi
     */
    private fun hookWifi(sCache: MutableMap<String, IBinder>){
        var originWifiService = sCache[Context.WIFI_SERVICE]
        if (null == originWifiService) {
            Log.d(TAG, "wifiManager not cached")
            val getService = serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
            originWifiService = getService?.invoke(null, Context.WIFI_SERVICE) as IBinder
        }
        sCache[Context.WIFI_SERVICE] = Proxy.newProxyInstance(
            serviceManagerClass?.classLoader,
            arrayOf(IBinder::class.java),
            WifiBinderInvocationHandler(originWifiService)
        ) as IBinder
    }

    /**
     * hook 基站
     */
    private fun hookCellInfo(sCache: MutableMap<String, IBinder>){
        var originTelephonyService = sCache[Context.TELEPHONY_SERVICE]
        if (null == originTelephonyService) {
            Log.d(TAG, "telephonyManager not cached")
            val getService = serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
            originTelephonyService = getService?.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
        }
        sCache[Context.TELEPHONY_SERVICE] = Proxy.newProxyInstance(
            serviceManagerClass?.classLoader,
            arrayOf(IBinder::class.java),
            TelephonyBinderInvocationHandler(originTelephonyService)
        ) as IBinder
    }

    /**
     * 控制模拟经纬度开关
     */
    fun enableMockCoordinate(enable:Boolean){
        SpHelper.setBoolean(ENABLE_MOCK_COORDINATE, enable)
    }

    /**
     * 是否允许模拟经纬度
     */
    fun isEnableMockCoordinate() = SpHelper.getBoolean(ENABLE_MOCK_COORDINATE, false)

    /**
     * 控制模拟wifi开关
     */
    fun enableMockWifi(enable:Boolean){
        SpHelper.setBoolean(ENABLE_MOCK_WIFI, enable)
    }

    /**
     * 是否允许模拟wifi
     */
    fun isEnableMockWifi() = SpHelper.getBoolean(ENABLE_MOCK_WIFI, false)

    /**
     * 控制模拟基站开关
     */
    fun enableMockCellInfo(enable:Boolean){
        SpHelper.setBoolean(ENABLE_MOCK_CELL_INFO, enable)
    }

    /**
     * 是否允许模拟基站
     */
    fun isEnableMockCellInfo() = SpHelper.getBoolean(ENABLE_MOCK_CELL_INFO, false)

    /**
     * 模拟经纬度数据，以,分割; 例：113.123123,22.32323
     */
    fun mockCoordinate(coordinate:String?){
        coordinate?.let {
            try {
                val split = it.split(",")
                mockLon = split[0].toDouble()
                mockLat = split[1].toDouble()
                SpHelper.setString(MOCKED_COORDINATE,it)
            }catch (e:Exception){
                mockLon = 0.0
                mockLat = 0.0
                Log.e(TAG,e.message,e)
            }
        }
    }

}