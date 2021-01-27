package com.james.datamock.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.james.datamock.DataMock
import com.james.datamock.R
import com.james.datamock.helper.SpHelper
import kotlinx.android.synthetic.main.settings_activity.*

class DataMockSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        initView()
    }

    private fun initView() {
        sw_coordinate.isChecked = DataMock.isEnableMockCoordinate()
        sw_coordinate.setOnCheckedChangeListener { buttonView, isChecked ->
            DataMock.enableMockCoordinate(isChecked)
        }
        sw_wifi.isChecked = DataMock.isEnableMockWifi()
        sw_wifi.setOnCheckedChangeListener { buttonView, isChecked ->
            DataMock.enableMockWifi(isChecked)
        }
        sw_cell_info.isChecked = DataMock.isEnableMockCellInfo()
        sw_cell_info.setOnCheckedChangeListener { buttonView, isChecked ->
            DataMock.enableMockCellInfo(isChecked)
        }
        val mockLal = SpHelper.getString("mocked_coordinate", "")
        if (!TextUtils.isEmpty(mockLal)) {
            et_coordinate.setText(mockLal)
        }
        if (DataMock.isEnableMockCoordinate()) {
            DataMock.mockCoordinate(mockLal)
        }
        et_coordinate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                DataMock.mockCoordinate(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}