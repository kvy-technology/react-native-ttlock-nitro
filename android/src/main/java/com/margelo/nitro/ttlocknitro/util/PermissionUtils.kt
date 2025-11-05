package com.margelo.nitro.ttlocknitro.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ttlock.bl.sdk.util.LogUtil

object PermissionUtils {
    const val PERMISSIONS_REQUEST_CODE = 1

    private val scanConnectPermission = arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    private val locationPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    fun isAndroid12OrOver(): Boolean {
        return getAndroidSDKVersion() >= 31
    }

    fun getAndroidSDKVersion(): Int {
        return try {
            Build.VERSION.SDK_INT
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }
    }

    fun hasScanPermission(activity: Activity): Boolean {
        return if (isAndroid12OrOver()) {
            // android 12及以上 扫描权限  获取名称需要连接权限
            hasPermission(activity, Manifest.permission.BLUETOOTH_SCAN) && hasPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            // 以下 位置权限
            hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun hasConnectPermission(activity: Activity): Boolean {
        return if (isAndroid12OrOver()) {
            // android 12及以上 需要连接权限
            hasPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            // 12以下的不需要
            true
        }
    }

    fun doWithScanPermission(activity: Activity, onSuccessListener: OnSuccessListener) {
        if (hasScanPermission(activity)) {
            onSuccessListener.onSuccess(true)
        } else {
            onSuccessListener.onSuccess(false)
            LogUtil.d("no scan permission")
            ActivityCompat.requestPermissions(activity, if (isAndroid12OrOver()) scanConnectPermission else locationPermission, PERMISSIONS_REQUEST_CODE)
        }
    }

    fun doWithConnectPermission(activity: Activity, onSuccessListener: OnSuccessListener) {
        if (hasConnectPermission(activity)) {
            onSuccessListener.onSuccess(true)
        } else {
            onSuccessListener.onSuccess(false)
            LogUtil.d("no connect permission")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSIONS_REQUEST_CODE)
        }
    }

    fun hasPermission(activity: Activity, permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, permission)
    }
}

