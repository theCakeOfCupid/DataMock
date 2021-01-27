package com.james.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.james.datamock.DataMock
import com.james.datamock.helper.ReflectionHelper
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        bt_test.setOnClickListener(this)
        bt_test_wifi.setOnClickListener(this)
        bt_test_cell_info.setOnClickListener(this)
    }

    private val strings = mutableListOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private fun initPermission() {
        PermissionX.init(this).permissions(strings)
            .request { allGranted, grantedList, deniedList -> }
    }

    private fun testLocation() {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val lastKnownLocation =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        Log.d(DataMock.TAG, "lon:${lastKnownLocation?.longitude}")
        Log.d(DataMock.TAG, "lat:${lastKnownLocation?.latitude}")
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 0L, 0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    Log.d(DataMock.TAG, "onLocationChanged lon:${location?.longitude}")
                    Log.d(DataMock.TAG, "onLocationChanged lat:${location?.latitude}")
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }

            })
    }

    private fun getWifiScanResult(): ScanResult {
        val scanResult = ReflectionHelper.getClassByName("android.net.wifi.ScanResult")
            ?.newInstance() as ScanResult
        scanResult.SSID = "test_ssid"
        scanResult.level = -62
        scanResult.BSSID = "sdfjlsadfkjlsdf"
        return scanResult
    }

    private fun getCellInfo(): CellInfo {
        val cellInfo = ReflectionHelper.getClassByName("android.telephony.CellInfoLte")
            ?.newInstance() as CellInfoLte
        return cellInfo
    }

    private fun testWifi() {
        val temp = mutableListOf<ScanResult>()
        for (i in 1..10) {
            temp.add(getWifiScanResult())
        }
        DataMock.mockScanResultList = temp
        val wifiService =
            getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val result = wifiService.scanResults
        result.forEach {
            Log.d(DataMock.TAG, it.BSSID)
            Log.d(DataMock.TAG, it.SSID)
            Log.d(DataMock.TAG, it.level.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private fun testCellInfo() {
        val temp = mutableListOf<CellInfo>()
        for (i in 1..10) {
            temp.add(getCellInfo())
        }
        DataMock.mockCellInfoList = temp
        val telephonyManager =
            getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val result = telephonyManager.allCellInfo
        result.forEach {
            Log.d(DataMock.TAG, "cellinfo---")
            if (it is CellInfoLte) {
                Log.d(DataMock.TAG, it.cellSignalStrength.dbm.toString())
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.bt_test) {
            testLocation()
        }
        if (v?.id == R.id.bt_test_wifi) {
            testWifi()
        }
        if (v?.id == R.id.bt_test_cell_info) {
            testCellInfo()
        }
    }
}