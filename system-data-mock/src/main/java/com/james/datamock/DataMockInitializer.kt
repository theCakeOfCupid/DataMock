package com.james.datamock

import android.content.Context
import androidx.startup.Initializer

/**
 * @author james
 * @date :2021/1/10 15:54
 */
class DataMockInitializer:Initializer<Unit> {
    override fun create(context: Context) {
        DataMock.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>>  = mutableListOf()
}