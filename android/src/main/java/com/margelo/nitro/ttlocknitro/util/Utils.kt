package com.margelo.nitro.ttlocknitro.util

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.margelo.nitro.ttlocknitro.CycleDateParam
import com.margelo.nitro.ttlocknitro.model.TTLockFieldConstant
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
    
    fun doubleArray2IntList(doubleArray: DoubleArray?): List<Int> {
        val list = ArrayList<Int>()
        if (doubleArray != null) {
            for (d in doubleArray) {
                list.add(d.toInt())
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
    
    fun doubleArray2IntJson(doubleArray: DoubleArray?): String {
        var json = "[]"
        if (doubleArray != null) {
            val integers = ArrayList<Int>()
            for (d in doubleArray) {
                integers.add(d.toInt())
            }
            json = GsonUtil.toJson(integers)
        }
        return json
    }

    fun readableArray2CyclicList(readableArray: ReadableArray?): List<CyclicConfig> {
        val cyclicConfigs = ArrayList<CyclicConfig>()
        if (readableArray != null) {
            for (i in 0 until readableArray.size()) {
                val map: ReadableMap? = readableArray.getMap(i)
                if (map != null) {
                    val cyclicConfig = CyclicConfig()
                    cyclicConfig.weekDay = map.getInt(TTLockFieldConstant.WEEK_DAY)
                    cyclicConfig.startTime = map.getInt(TTLockFieldConstant.START_TIME)
                    cyclicConfig.endTime = map.getInt(TTLockFieldConstant.END_TIME)
                    cyclicConfigs.add(cyclicConfig)
                }
            }
        }
        return cyclicConfigs
    }
    
    fun cycleDateArray2CyclicList(cycleDateArray: Array<CycleDateParam>?): List<CyclicConfig> {
        val cyclicConfigs = ArrayList<CyclicConfig>()
        if (cycleDateArray != null) {
            for (param in cycleDateArray) {
                val cyclicConfig = CyclicConfig()
                cyclicConfig.weekDay = param.weekDay.toInt()
                cyclicConfig.startTime = param.startTime.toInt()
                cyclicConfig.endTime = param.endTime.toInt()
                cyclicConfigs.add(cyclicConfig)
            }
        }
        return cyclicConfigs
    }
}

