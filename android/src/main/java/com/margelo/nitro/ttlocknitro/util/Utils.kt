package com.margelo.nitro.ttlocknitro.util

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.reactnativettlock.model.TTLockFieldConstant
import com.ttlock.bl.sdk.entity.CyclicConfig
import com.ttlock.bl.sdk.util.GsonUtil
import java.util.ArrayList

/**
 * Created by TTLock on 2021/1/7.
 */
object Utils {
    fun readableArray2IntList(readableArray: ReadableArray?): List<Int> {
        val list = ArrayList<Int>()
        if (readableArray != null) {
            for (i in 0 until readableArray.size()) {
                list.add(readableArray.getInt(i))
            }
        }
        return list
    }

    fun readableArray2IntJson(readableArray: ReadableArray?): String {
        var json = "[]"
        if (readableArray != null) {
            val integers = ArrayList<Int>()
            for (i in 0 until readableArray.size()) {
                integers.add(readableArray.getInt(i))
            }
            json = GsonUtil.toJson(integers)
        }
        return json
    }

    fun readableArray2CyclicList(readableArray: ReadableArray?): List<CyclicConfig> {
        val cyclicConfigs = ArrayList<CyclicConfig>()
        if (readableArray != null) {
            for (i in 0 until readableArray.size()) {
                val map: ReadableMap = readableArray.getMap(i)
                val cyclicConfig = CyclicConfig()
                cyclicConfig.weekDay = map.getInt(TTLockFieldConstant.WEEK_DAY)
                cyclicConfig.startTime = map.getInt(TTLockFieldConstant.START_TIME)
                cyclicConfig.endTime = map.getInt(TTLockFieldConstant.END_TIME)
                cyclicConfigs.add(cyclicConfig)
            }
        }
        return cyclicConfigs
    }
}

