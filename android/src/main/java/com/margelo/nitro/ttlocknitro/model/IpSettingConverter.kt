package com.margelo.nitro.ttlocknitro.model

import com.facebook.react.bridge.ReadableMap
import com.ttlock.bl.sdk.entity.IpSetting
import java.lang.reflect.Field

object IpSettingConverter {
    fun toObject(readableMap: ReadableMap): IpSetting {
        val params = readableMap.toHashMap()
        val ipSetting = IpSetting()
        val fields: Array<Field> = ipSetting.javaClass.declaredFields
        try {
            for (field in fields) {
                // 设置是否允许访问，不是修改原来的访问权限修饰词。
                field.isAccessible = true
                val value = params[field.name]
                if (value != null) {
                    field.set(ipSetting, value)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ipSetting
    }
}

