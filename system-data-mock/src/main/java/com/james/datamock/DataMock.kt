package com.james.datamock

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.net.wifi.ScanResult
import android.os.IBinder
import android.telephony.CellInfo
import android.util.Log
import com.amap.api.location.AMapLocation
import com.baidu.location.Address
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.james.datamock.helper.Reflection
import com.james.datamock.helper.ReflectionHelper
import com.james.datamock.helper.SpHelper
import com.james.datamock.location.LocationBinderInvocationHandler
import com.james.datamock.telephony.TelephonyBinderInvocationHandler
import com.james.datamock.utils.CoordinateConvertUtil
import com.james.datamock.wifi.WifiBinderInvocationHandler
import java.lang.ref.WeakReference
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

    private const val IGNORE_MOCK_TIME = "ignore_mock_time"

    internal var mockLon = 0.0

    internal var mockLat = 0.0
    
    var mockScanResultList: MutableList<ScanResult>? = null

    var mockCellInfoList: MutableList<CellInfo>? = null

    const val TAG = "DataMock"

    private const val SYSTEM_MANAGER_PATH = "android.os.ServiceManager"

    private var serviceManagerClass: Class<*>? = null

    private lateinit var mContext: WeakReference<Context>

    private val mGeoCoder by lazy {
        Geocoder(mContext.get())
    }

    internal fun init(context: Context) {
        mContext = WeakReference(context.applicationContext)
        try {
            Reflection.unseal(context)
            serviceManagerClass = ReflectionHelper.getClassByName(SYSTEM_MANAGER_PATH)
            SpHelper.init(context)
            initHook()
        } catch (e: Exception) {
            Log.e(TAG, "init failed", e)
        }
    }

    public fun getMockTime(): Long {
        if (isIgnoreMockTime()) {
            return 0
        }
        return System.currentTimeMillis()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun initHook() {
        val sCacheField = serviceManagerClass?.getDeclaredField("sCache")
        sCacheField?.isAccessible = true
        val sCache: MutableMap<String, IBinder> =
            sCacheField?.get(null) as MutableMap<String, IBinder>
        sCacheField.isAccessible = false
        hookCoordinate(sCache)
        hookWifi(sCache)
        hookCellInfo(sCache)
    }

    /**
     * hook经纬度
     */
    private fun hookCoordinate(sCache: MutableMap<String, IBinder>) {
        var originLocationService = sCache[Context.LOCATION_SERVICE]
        if (null == originLocationService) {
            Log.d(TAG, "locationManager not cached")
            val getService =
                serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
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
    private fun hookWifi(sCache: MutableMap<String, IBinder>) {
        var originWifiService = sCache[Context.WIFI_SERVICE]
        if (null == originWifiService) {
            Log.d(TAG, "wifiManager not cached")
            val getService =
                serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
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
    private fun hookCellInfo(sCache: MutableMap<String, IBinder>) {
        var originTelephonyService = sCache[Context.TELEPHONY_SERVICE]
        if (null == originTelephonyService) {
            Log.d(TAG, "telephonyManager not cached")
            val getService =
                serviceManagerClass?.getDeclaredMethod("getService", String::class.java)
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
    fun enableMockCoordinate(enable: Boolean) {
        SpHelper.setBoolean(ENABLE_MOCK_COORDINATE, enable)
    }

    /**
     * 是否允许模拟经纬度
     */
    fun isEnableMockCoordinate() = SpHelper.getBoolean(ENABLE_MOCK_COORDINATE, false)

    /**
     * 控制模拟wifi开关
     */
    fun enableMockWifi(enable: Boolean) {
        SpHelper.setBoolean(ENABLE_MOCK_WIFI, enable)
    }

    /**
     * 是否允许模拟wifi
     */
    fun isEnableMockWifi() = SpHelper.getBoolean(ENABLE_MOCK_WIFI, false)

    /**
     * 控制模拟基站开关
     */
    fun enableMockCellInfo(enable: Boolean) {
        SpHelper.setBoolean(ENABLE_MOCK_CELL_INFO, enable)
    }

    /**
     * 是否允许模拟基站
     */
    fun isEnableMockCellInfo() = SpHelper.getBoolean(ENABLE_MOCK_CELL_INFO, false)

    /**
     * 忽略模拟时间
     */
    fun ignoreMockTime(ignore: Boolean) = SpHelper.setBoolean(IGNORE_MOCK_TIME, ignore)

    /**
     * 是否忽略模拟时间
     */
    fun isIgnoreMockTime(): Boolean {
        return SpHelper.getBoolean(IGNORE_MOCK_TIME, false)
    }

    /**
     * 模拟经纬度数据，以,分割; 例：113.123123,22.32323
     */
    fun mockCoordinate(coordinate: String?) {
        coordinate?.let {
            try {
                val split = it.split(",")
                mockLon = split[0].toDouble()
                mockLat = split[1].toDouble()
                SpHelper.setString(MOCKED_COORDINATE, it)
            } catch (e: Exception) {
                mockLon = 0.0
                mockLat = 0.0
                Log.e(TAG, e.message, e)
            }
        }
    }

    /**
     * 是否需要处理高德定位
     */
    fun mockLocationIfNeeded(location: Any?) {
        if (!isEnableMockCoordinate()) {
            return
        }
        if (location is Location) {
            location.longitude = mockLon
            location.latitude = mockLat
        }

        if (location is AMapLocation) {
            if (location.errorCode == AMapLocation.LOCATION_SUCCESS) {
                location.longitude = mockLon
                location.latitude = mockLat
            }
        }
        if (location is BDLocation) {
            location.longitude = mockLon
            location.latitude = mockLat
        }
        geoCoderFromLocation(location)
    }

    /**
     * 反地理编码
     */
    private fun geoCoderFromLocation(location: Any?) {
        if (location is AMapLocation) {
            val result = CoordinateConvertUtil.gcj02ToWgs84(location.longitude, location.latitude)
            val fromLocation = mGeoCoder.getFromLocation(result[1], result[0], 1)
            if (fromLocation.isNullOrEmpty()) {
                return
            }
            location.province = fromLocation[0].adminArea
            location.city = fromLocation[0].locality
            location.address = fromLocation[0].featureName
            location.district = fromLocation[0].subLocality
        }
        if (location is BDLocation) {
            val result = CoordinateConvertUtil.bd09ToWgs84(location.longitude, location.latitude)
            val fromLocation = mGeoCoder.getFromLocation(result[1], result[0], 1)
            if (fromLocation.isNullOrEmpty()) {
                return
            }
            val address = Address.Builder()
                .province(fromLocation[0].adminArea)
                .city(fromLocation[0].locality)
                .country(fromLocation[0].countryName)
                .street(fromLocation[0].subAdminArea)
                .build()
            location.setAddr(address)
        }
    }


    class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(p0: BDLocation?) {
            println("nothing")
        }
    }

}