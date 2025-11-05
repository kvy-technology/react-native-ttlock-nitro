package com.margelo.nitro.ttlocknitro

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.margelo.nitro.ttlocknitro.model.IpSettingConverter
import com.margelo.nitro.ttlocknitro.model.RNControlAction
import com.margelo.nitro.ttlocknitro.model.TTBaseFieldConstant
import com.margelo.nitro.ttlocknitro.model.TTDoorSensorErrorConverter
import com.margelo.nitro.ttlocknitro.model.TTDoorSensorEvent
import com.margelo.nitro.ttlocknitro.model.TTDoorSensorFieldConstant
import com.margelo.nitro.ttlocknitro.model.TTGatewayErrorConverter
import com.margelo.nitro.ttlocknitro.model.TTGatewayEvent
import com.margelo.nitro.ttlocknitro.model.TTGatewayFieldConstant
import com.margelo.nitro.ttlocknitro.model.TTKeypadConstant
import com.margelo.nitro.ttlocknitro.model.TTKeypadErrorConverter
import com.margelo.nitro.ttlocknitro.model.TTKeypadEvent
import com.margelo.nitro.ttlocknitro.model.TTLockConfigConverter
import com.margelo.nitro.ttlocknitro.model.TTLockErrorConverter
import com.margelo.nitro.ttlocknitro.model.TTLockEvent
import com.margelo.nitro.ttlocknitro.model.TTLockFieldConstant
import com.margelo.nitro.ttlocknitro.model.TTRemoteEvent
import com.margelo.nitro.ttlocknitro.model.TTRemoteFieldConstant
import com.margelo.nitro.ttlocknitro.model.TTRemoteKeyErrorConverter
import com.margelo.nitro.ttlocknitro.util.PermissionUtils
import com.margelo.nitro.ttlocknitro.util.Utils
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice
import com.ttlock.bl.sdk.api.TTLockClient
import com.ttlock.bl.sdk.callback.*
import com.ttlock.bl.sdk.constant.LogType
import com.ttlock.bl.sdk.constant.RecoveryData
import com.ttlock.bl.sdk.device.Remote
import com.ttlock.bl.sdk.device.WirelessDoorSensor
import com.ttlock.bl.sdk.device.WirelessKeypad
import com.ttlock.bl.sdk.entity.AccessoryInfo
import com.ttlock.bl.sdk.entity.AccessoryType
import com.ttlock.bl.sdk.entity.ActivateLiftFloorsResult
import com.ttlock.bl.sdk.entity.AutoUnlockDirection
import com.ttlock.bl.sdk.entity.ControlLockResult
import com.ttlock.bl.sdk.entity.FaceCollectionStatus
import com.ttlock.bl.sdk.entity.IpSetting
import com.ttlock.bl.sdk.entity.LockError
import com.ttlock.bl.sdk.entity.PassageModeConfig
import com.ttlock.bl.sdk.entity.PassageModeType
import com.ttlock.bl.sdk.entity.RecoveryDataType
import com.ttlock.bl.sdk.entity.SoundVolume
import com.ttlock.bl.sdk.entity.TTLiftWorkMode
import com.ttlock.bl.sdk.entity.TTLockConfigType
import com.ttlock.bl.sdk.entity.UnlockDirection
import com.ttlock.bl.sdk.entity.ValidityInfo
import com.ttlock.bl.sdk.entity.WifiLockInfo
import com.ttlock.bl.sdk.gateway.api.GatewayClient
import com.ttlock.bl.sdk.gateway.callback.ConfigIpCallback
import com.ttlock.bl.sdk.gateway.callback.ConnectCallback
import com.ttlock.bl.sdk.gateway.callback.InitGatewayCallback
import com.ttlock.bl.sdk.gateway.callback.ScanGatewayCallback
import com.ttlock.bl.sdk.gateway.callback.ScanWiFiByGatewayCallback
import com.ttlock.bl.sdk.gateway.model.ConfigureGatewayInfo
import com.ttlock.bl.sdk.gateway.model.DeviceInfo
import com.ttlock.bl.sdk.gateway.model.GatewayError
import com.ttlock.bl.sdk.gateway.model.GatewayType
import com.ttlock.bl.sdk.gateway.model.WiFi
import com.ttlock.bl.sdk.keypad.InitKeypadCallback
import com.ttlock.bl.sdk.keypad.ScanKeypadCallback
import com.ttlock.bl.sdk.keypad.WirelessKeypadClient
import com.ttlock.bl.sdk.keypad.model.InitKeypadResult
import com.ttlock.bl.sdk.keypad.model.KeypadError
import com.ttlock.bl.sdk.remote.api.RemoteClient
import com.ttlock.bl.sdk.remote.callback.GetRemoteSystemInfoCallback
import com.ttlock.bl.sdk.remote.callback.InitRemoteCallback
import com.ttlock.bl.sdk.remote.callback.ScanRemoteCallback
import com.ttlock.bl.sdk.remote.model.InitRemoteResult
import com.ttlock.bl.sdk.remote.model.RemoteError
import com.ttlock.bl.sdk.remote.model.SystemInfo
import com.ttlock.bl.sdk.util.FeatureValueUtil
import com.ttlock.bl.sdk.util.GsonUtil
import com.ttlock.bl.sdk.util.LogUtil
import com.ttlock.bl.sdk.wirelessdoorsensor.WirelessDoorSensorClient
import com.ttlock.bl.sdk.wirelessdoorsensor.callback.InitDoorSensorCallback
import com.ttlock.bl.sdk.wirelessdoorsensor.callback.ScanWirelessDoorSensorCallback
import com.ttlock.bl.sdk.wirelessdoorsensor.model.DoorSensorError
import com.ttlock.bl.sdk.wirelessdoorsensor.model.InitDoorSensorResult
import java.util.ArrayList
import java.util.HashMap

@ReactModule(name = "Ttlock")
class TtlockNitro(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val reactContext: ReactApplicationContext = reactContext
    private var scanType: Int = 0
    private val mCachedDevice: HashMap<String, ExtendedBluetoothDevice> = HashMap()
    private val mCachedRemote: HashMap<String, Remote> = HashMap()
    private val mCachedDoorSensor: HashMap<String, WirelessDoorSensor> = HashMap()
    private val mCachedKeypad: HashMap<String, WirelessKeypad> = HashMap()
    private var mac: String? = null
    private var totalCnt: Int = 0
    private var flag: Boolean = false

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
        const val TYPE_LOCK = 1
        const val TYPE_PLUG = 2
        const val TYPE_REMOTE = 3
        const val TYPE_DOOR_SENSOR = 4
        const val TYPE_WIRELESS_KEYPAD = 5
    }

    init {
        LogUtil.setDBG(true)
    }

    override fun getName(): String {
        return "Ttlock"
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        if (Manifest.permission.ACCESS_FINE_LOCATION == permissions[i] && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            when (scanType) {
                                TYPE_LOCK -> startScan()
                                TYPE_PLUG -> startScanGateway()
                                TYPE_REMOTE -> startScanRemoteKey()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cacheAndFilterScanDevice(btDevice: ExtendedBluetoothDevice): ExtendedBluetoothDevice? {
        var newAddDevice: ExtendedBluetoothDevice? = btDevice
        val lockMac = btDevice.address
        if (mCachedDevice.isEmpty()) {
            mCachedDevice[lockMac] = btDevice
        } else {
            val child = mCachedDevice[lockMac]
            if (child == null) {
                mCachedDevice[lockMac] = btDevice
            } else {
                if (newAddDevice?.isSettingMode != child.isSettingMode) {
                    mCachedDevice.remove(lockMac)
                    mCachedDevice[lockMac] = btDevice
                } else {
                    newAddDevice = null
                }
            }
        }
        return newAddDevice
    }

    //-------------wireless keypad---------------------------
    fun startScanWirelessKeypad() {
        scanType = TYPE_WIRELESS_KEYPAD
        PermissionUtils.doWithScanPermission(currentActivity) { success ->
            if (success) {
                mCachedKeypad.clear()
                WirelessKeypadClient.getDefault().startScanKeyboard(object : ScanKeypadCallback {
                    override fun onScanKeyboardSuccess(wirelessKeypad: WirelessKeypad) {
                        mCachedKeypad[wirelessKeypad.address] = wirelessKeypad
                        val map = Arguments.createMap()
                        map.putString(TTKeypadConstant.KEYPAD_NAME, wirelessKeypad.name)
                        map.putString(TTKeypadConstant.KEYPAD_MAC, wirelessKeypad.address)
                        map.putInt(TTBaseFieldConstant.RSSI, wirelessKeypad.rssi)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTKeypadEvent.ScanKeypad, map)
                    }

                    override fun onScanFailed(error: Int) {
                        // Handle error
                    }
                })
            } else {
                LogUtil.d("no scan permission")
            }
        }
    }

    fun stopScanWirelessKeypad() {
        WirelessKeypadClient.getDefault().stopScanKeyboard()
    }

    fun initWirelessKeypad(keypadMac: String, lockMac: String, success: Callback, fail: Callback) {
        WirelessKeypadClient.getDefault().initializeKeypad(
            mCachedKeypad[keypadMac],
            lockMac,
            object : InitKeypadCallback {
                override fun onInitKeypadSuccess(initKeypadResult: InitKeypadResult) {
                    val writableArray = Arguments.createArray()
                    writableArray.pushInt(initKeypadResult.batteryLevel)
                    writableArray.pushString(initKeypadResult.featureValue)
                    success.invoke(writableArray)
                }

                override fun onFail(error: KeypadError) {
                    fail.invoke(TTKeypadErrorConverter.native2RN(error), error.description)
                }
            }
        )
    }

    //-------------door sensor---------------------------
    override fun startScanDoorSensor() {
        scanType = TYPE_DOOR_SENSOR
        PermissionUtils.doWithScanPermission(currentActivity) { success ->
            if (success) {
                mCachedDoorSensor.clear()
                WirelessDoorSensorClient.getDefault().startScan(object : ScanWirelessDoorSensorCallback {
                    override fun onScan(doorSensor: WirelessDoorSensor) {
                        mCachedDoorSensor[doorSensor.address] = doorSensor
                        val map = Arguments.createMap()
                        map.putString(TTDoorSensorFieldConstant.DOOR_SENSOR_NAME, doorSensor.name)
                        map.putString(TTDoorSensorFieldConstant.DOOR_SENSOR_MAC, doorSensor.address)
                        map.putInt(TTBaseFieldConstant.RSSI, doorSensor.rssi)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTDoorSensorEvent.ScanDoorSensor, map)
                    }
                })
            } else {
                LogUtil.d("no scan permission")
            }
        }
    }

    override fun stopScanDoorSensor() {
        WirelessDoorSensorClient.getDefault().stopScan()
    }

    override fun initDoorSensor(doorSensorMac: String, lockData: String, success: Callback, fail: Callback) {
        LogUtil.d("init doorsensor start")
        LogUtil.d("mCachedDoorSensor.get(doorSensorMac):" + mCachedDoorSensor[doorSensorMac])
        WirelessDoorSensorClient.getDefault().initialize(
            mCachedDoorSensor[doorSensorMac],
            lockData,
            object : InitDoorSensorCallback {
                override fun onInitSuccess(initDoorSensorResult: InitDoorSensorResult) {
                    val writableArray = Arguments.createArray()
                    writableArray.pushInt(initDoorSensorResult.batteryLevel)
                    writableArray.pushString(GsonUtil.toJson(initDoorSensorResult.firmwareInfo))
                    success.invoke(writableArray)
                }

                override fun onFail(doorSensorError: DoorSensorError) {
                    val errorCode = TTDoorSensorErrorConverter.native2RN(doorSensorError)
                    fail.invoke(errorCode, doorSensorError.description)
                }
            }
        )
    }

    //---------------remote------------------------------
    override fun startScanRemoteKey() {
        scanType = TYPE_REMOTE
        PermissionUtils.doWithScanPermission(currentActivity) { success ->
            if (success) {
                mCachedRemote.clear()
                RemoteClient.getDefault().startScan(object : ScanRemoteCallback {
                    override fun onScanRemote(remote: Remote) {
                        mCachedRemote[remote.address] = remote
                        val map = Arguments.createMap()
                        map.putString(TTRemoteFieldConstant.REMOTE_KEY_NAME, remote.name)
                        map.putString(TTRemoteFieldConstant.REMOTE_KEY_MAC, remote.address)
                        map.putInt(TTRemoteFieldConstant.RSSI, remote.rssi)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTRemoteEvent.ScanRemoteKey, map)
                    }
                })
            } else {
                LogUtil.d("no scan permission")
            }
        }
    }

    override fun stopScanRemoteKey() {
        RemoteClient.getDefault().stopScan()
    }

    override fun initRemoteKey(remoteMac: String, lockData: String, success: Callback, fail: Callback) {
        RemoteClient.getDefault().initialize(
            mCachedRemote[remoteMac],
            lockData,
            object : InitRemoteCallback {
                override fun onInitSuccess(initRemoteResult: InitRemoteResult) {
                    val writableArray = Arguments.createArray()
                    writableArray.pushInt(initRemoteResult.batteryLevel)
                    writableArray.pushString(GsonUtil.toJson(initRemoteResult.systemInfo))
                    success.invoke(writableArray)
                }

                override fun onFail(remoteError: RemoteError) {
                    fail.invoke(TTRemoteKeyErrorConverter.native2RN(remoteError), remoteError.description)
                }
            }
        )
    }

    override fun getRemoteKeySystemInfo(remoteMac: String, success: Callback, fail: Callback) {
        RemoteClient.getDefault().getRemoteSystemInfo(remoteMac, object : GetRemoteSystemInfoCallback {
            override fun onGetRemoteSystemInfoSuccess(systemInfo: SystemInfo) {
                val map = Arguments.createMap()
                map.putString(TTRemoteFieldConstant.MODEL_NUM, systemInfo.modelNum)
                map.putString(TTRemoteFieldConstant.HARDWARE_REVISION, systemInfo.hardwareRevision)
                map.putString(TTRemoteFieldConstant.FIRMWARE_REVISION, systemInfo.firmwareRevision)
                success.invoke(map)
            }

            override fun onFail(remoteError: RemoteError) {
                fail.invoke(TTRemoteKeyErrorConverter.native2RN(remoteError), remoteError.description)
            }
        })
    }

    //-------------gateway---------------
    override fun startScanGateway() {
        scanType = TYPE_PLUG
        PermissionUtils.doWithScanPermission(currentActivity) { success ->
            if (success) {
                GatewayClient.getDefault().prepareBTService(currentActivity)
                GatewayClient.getDefault().startScanGateway(object : ScanGatewayCallback {
                    override fun onScanGatewaySuccess(device: ExtendedBluetoothDevice) {
                        val map = Arguments.createMap()
                        map.putString(TTGatewayFieldConstant.GATEWAY_NAME, device.name)
                        map.putString(TTGatewayFieldConstant.GATEWAY_MAC, device.address)
                        map.putBoolean(TTGatewayFieldConstant.IS_DFU_MODE, device.isDfuMode)
                        map.putInt(TTGatewayFieldConstant.RSSI, device.rssi)
                        map.putInt(TTGatewayFieldConstant.TYPE, device.gatewayType)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTGatewayEvent.scanGateway, map)
                    }

                    override fun onScanFailed(errorCode: Int) {
                        LogUtil.d("errorCode:$errorCode")
                    }
                })
            } else {
                LogUtil.d("no scan permission")
            }
        }
    }

    override fun stopScanGateway() {
        GatewayClient.getDefault().stopScanGateway()
    }

    override fun connect(mac: String, callback: Callback) {
        if (mac.isEmpty()) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, callback)
            LogUtil.d("mac is null")
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                this@TtlockNitro.mac = mac
                flag = false
                GatewayClient.getDefault().connectGateway(mac, object : ConnectCallback {
                    override fun onConnectSuccess(device: ExtendedBluetoothDevice) {
                        LogUtil.d("connected:$device")
                        flag = true
                        callback.invoke(1)
                    }

                    override fun onDisconnected() {
                        try {
                            if (!flag) {
                                flag = true
                                callback.invoke(0)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            } else {
                if (!flag) {
                    flag = true
                    callback.invoke(0)
                }
            }
        }
    }

    override fun getNearbyWifi(callback: Callback) {
        if (mac.isNullOrEmpty()) {
            LogUtil.d("mac is null")
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                GatewayClient.getDefault().scanWiFiByGateway(mac!!, object : ScanWiFiByGatewayCallback {
                    override fun onScanWiFiByGateway(wiFis: List<WiFi>) {
                        if (wiFis != null) {
                            val writableArray = Arguments.createArray()
                            for (wiFi in wiFis) {
                                val map = Arguments.createMap()
                                map.putString(TTGatewayFieldConstant.WIFI, wiFi.ssid)
                                map.putInt(TTGatewayFieldConstant.RSSI, wiFi.rssi)
                                writableArray.pushMap(map)
                            }
                            reactApplicationContext
                                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit(TTGatewayEvent.scanWifi, writableArray)
                        }
                    }

                    override fun onScanWiFiByGatewaySuccess() {
                        callback.invoke(0)
                    }

                    override fun onFail(error: GatewayError) {
                        callback.invoke(1)
                    }
                })
            } else {
                callback.invoke(1)
            }
        }
    }

    private fun configIp(gatewayInfo: ConfigureGatewayInfo, ipSetting: IpSetting, success: Callback, fail: Callback) {
        GatewayClient.getDefault().configIp(mac!!, ipSetting, object : ConfigIpCallback {
            override fun onConfigIpSuccess() {
                doInitGateway(gatewayInfo, success, fail)
            }

            override fun onFail(gatewayError: GatewayError) {
                fail.invoke(TTGatewayErrorConverter.native2RN(gatewayError))
            }
        })
    }

    private fun doInitGateway(gatewayInfo: ConfigureGatewayInfo, success: Callback, fail: Callback) {
        GatewayClient.getDefault().initGateway(gatewayInfo, object : InitGatewayCallback {
            override fun onInitGatewaySuccess(deviceInfo: DeviceInfo) {
                val map = Arguments.createMap()
                map.putString(TTGatewayFieldConstant.MODEL_NUM, deviceInfo.modelNum)
                map.putString(TTGatewayFieldConstant.HARDWARE_REVISION, deviceInfo.hardwareRevision)
                map.putString(TTGatewayFieldConstant.FIRMWARE_REVISION, deviceInfo.firmwareRevision)
                success.invoke(map)
            }

            override fun onFail(error: GatewayError) {
                LogUtil.d("error:" + error.description)
                fail.invoke(TTGatewayErrorConverter.native2RN(error))
            }
        })
    }

    override fun initGateway(readableMap: ReadableMap, success: Callback, fail: Callback) {
        if (readableMap != null) {
            val gatewayInfo = ConfigureGatewayInfo()
            gatewayInfo.plugName = readableMap.getString(TTGatewayFieldConstant.GATEWAY_NAME)
            gatewayInfo.plugVersion = readableMap.getInt(TTGatewayFieldConstant.TYPE)
            if (gatewayInfo.plugVersion == GatewayType.G2 || gatewayInfo.plugVersion == GatewayType.G5 || gatewayInfo.plugVersion == GatewayType.G6) {
                gatewayInfo.ssid = readableMap.getString(TTGatewayFieldConstant.WIFI)
                gatewayInfo.wifiPwd = readableMap.getString(TTGatewayFieldConstant.WIFI_PASSWORD)
            }
            gatewayInfo.uid = readableMap.getInt(TTGatewayFieldConstant.TTLOCK_UID)
            gatewayInfo.userPwd = readableMap.getString(TTGatewayFieldConstant.TTLOCK_LOGIN_PASSWORD)
            if (readableMap.hasKey(TTGatewayFieldConstant.IP_SETTING_TYPE)) {
                val ipSettingType = readableMap.getInt(TTGatewayFieldConstant.IP_SETTING_TYPE)
                if (ipSettingType == IpSetting.STATIC_IP) {
                    val ipSetting = IpSetting()
                    ipSetting.type = IpSetting.STATIC_IP

                    if (readableMap.hasKey(TTGatewayFieldConstant.IP_ADDRESS)) {
                        ipSetting.ipAddress = readableMap.getString(TTGatewayFieldConstant.IP_ADDRESS)
                    }
                    if (readableMap.hasKey(TTGatewayFieldConstant.SUBNET_MASK)) {
                        ipSetting.subnetMask = readableMap.getString(TTGatewayFieldConstant.SUBNET_MASK)
                    }
                    if (readableMap.hasKey(TTGatewayFieldConstant.ROUTER)) {
                        ipSetting.router = readableMap.getString(TTGatewayFieldConstant.ROUTER)
                    }
                    if (readableMap.hasKey(TTGatewayFieldConstant.PREFERRED_DNS)) {
                        ipSetting.preferredDns = readableMap.getString(TTGatewayFieldConstant.PREFERRED_DNS)
                    }
                    if (readableMap.hasKey(TTGatewayFieldConstant.ALTERNATE_DNS)) {
                        ipSetting.alternateDns = readableMap.getString(TTGatewayFieldConstant.ALTERNATE_DNS)
                    }

                    configIp(gatewayInfo, ipSetting, success, fail)
                } else {
                    doInitGateway(gatewayInfo, success, fail)
                }
            } else {
                doInitGateway(gatewayInfo, success, fail)
            }
        }
    }

    //-----------lock--------------
    override fun startScan() {
        LogUtil.d("start scan")
        scanType = TYPE_LOCK
        PermissionUtils.doWithScanPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().startScanLock(object : ScanLockCallback {
                    override fun onScanLockSuccess(extendedBluetoothDevice: ExtendedBluetoothDevice) {
                        if (extendedBluetoothDevice != null) {
                            cacheAndFilterScanDevice(extendedBluetoothDevice)
                            val map = Arguments.createMap()
                            map.putString(TTLockFieldConstant.LOCK_NAME, extendedBluetoothDevice.name)
                            map.putString(TTLockFieldConstant.LOCK_MAC, extendedBluetoothDevice.address)
                            map.putBoolean(TTLockFieldConstant.IS_INITED, !extendedBluetoothDevice.isSettingMode)
                            map.putBoolean(TTLockFieldConstant.IS_KEYBOARD_ACTIVATED, extendedBluetoothDevice.isTouch)
                            map.putInt(TTLockFieldConstant.ELECTRIC_QUANTITY, extendedBluetoothDevice.batteryCapacity)
                            map.putString(TTLockFieldConstant.LOCK_VERSION, extendedBluetoothDevice.lockVersionJson)
                            map.putInt(TTLockFieldConstant.RSSI, extendedBluetoothDevice.rssi)
                            map.putInt(TTLockFieldConstant.LOCK_SWITCH_STATE, extendedBluetoothDevice.parkStatus)
                            reactApplicationContext
                                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit(TTLockEvent.scanLock, map)
                        }
                    }

                    override fun onFail(error: LockError) {
                        LogUtil.d("error:$error")
                    }
                })
            }
        }
    }

    override fun stopScan() {
        TTLockClient.getDefault().stopScanLock()
    }

    override fun initLock(readableMap: ReadableMap, success: Callback, fail: Callback) {
        val lockmac = readableMap.getString(TTLockFieldConstant.LOCK_MAC)
        if (TextUtils.isEmpty(lockmac)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            LogUtil.d("lockmac is null")
            return
        }
        val device = mCachedDevice[lockmac]
        if (device == null) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            LogUtil.d("device is null")
            return
        }
        if (readableMap.hasKey(TTLockFieldConstant.CLIENT_PARA)) {
            val clientPara = readableMap.getString(TTLockFieldConstant.CLIENT_PARA)
            if (!TextUtils.isEmpty(clientPara)) {
                device.manufacturerId = clientPara
            }
        }
        TTLockClient.getDefault().initLock(device, object : InitLockCallback {
            override fun onInitLockSuccess(lockData: String) {
                success.invoke(lockData)
            }

            override fun onFail(error: LockError) {
                lockErrorCallback(error, fail)
            }
        })
    }

    override fun resetLock(lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().resetLock(lockData, null, object : ResetLockCallback {
                    override fun onResetLockSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun resetEkey(lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().resetEkey(lockData, null, object : ResetKeyCallback {
                    override fun onResetKeySuccess(lockData: String) {
                        successCallback.invoke(lockData)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun controlLock(controlAction: Int, lockData: String, successCallback: Callback, fail: Callback) {
        if (!RNControlAction.isValidAction(controlAction) || TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().controlLock(
                    RNControlAction.RN2Native(controlAction),
                    lockData,
                    null,
                    object : ControlLockCallback {
                        override fun onControlLockSuccess(controlLockResult: ControlLockResult) {
                            val writableArray = Arguments.createArray()
                            writableArray.pushDouble(controlLockResult.lockTime)
                            writableArray.pushInt(controlLockResult.battery)
                            writableArray.pushInt(controlLockResult.uniqueid)
                            successCallback.invoke(writableArray)
                        }

                        override fun onFail(error: LockError) {
                            lockErrorCallback(error, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun createCustomPasscode(
        passcode: String,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        LogUtil.d("passcode:$passcode")
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        LogUtil.d("startDate:$startDate")
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().createCustomPasscode(
                    passcode,
                    startDate.toLong(),
                    endDate.toLong(),
                    lockData,
                    null,
                    object : CreateCustomPasscodeCallback {
                        override fun onCreateCustomPasscodeSuccess(passcode: String) {
                            successCallback.invoke()
                        }

                        override fun onFail(error: LockError) {
                            lockErrorCallback(error, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun recoverPasscode(
        passcode: String,
        passcodeType: Int,
        cycleType: Int,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        val recoveryData = RecoveryData()
        recoveryData.keyboardPwd = passcode
        recoveryData.startDate = startDate.toLong()
        recoveryData.endDate = endDate.toLong()
        recoveryData.cycleType = cycleType
        recoveryData.keyboardPwdType = passcodeType
        val recoveryDataList = ArrayList<RecoveryData>()
        recoveryDataList.add(recoveryData)
        recoverLockData(GsonUtil.toJson(recoveryDataList), RecoveryDataType.PASSCODE, lockData, successCallback, fail)
    }

    override fun modifyPasscode(
        passcodeOrigin: String,
        passcodeNew: String,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        if (TextUtils.isEmpty(passcodeOrigin) || TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().modifyPasscode(
                    passcodeOrigin,
                    passcodeNew,
                    startDate.toLong(),
                    endDate.toLong(),
                    lockData,
                    null,
                    object : ModifyPasscodeCallback {
                        override fun onModifyPasscodeSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(error: LockError) {
                            lockErrorCallback(error, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun deletePasscode(passcode: String, lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deletePasscode(passcode, lockData, null, object : DeletePasscodeCallback {
                    override fun onDeletePasscodeSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun resetPasscode(lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().resetPasscode(lockData, null, object : ResetPasscodeCallback {
                    override fun onResetPasscodeSuccess(lockData: String) {
                        successCallback.invoke(lockData)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockSwitchState(lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(lockData)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockStatus(lockData, null, object : GetLockStatusCallback {
                    override fun onGetLockStatusSuccess(status: Int) {
                        successCallback.invoke(status)
                    }

                    override fun onGetDoorSensorStatusSuccess(status: Int) {
                        //todo:
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addCard(
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        LogUtil.d("cycleList:$cycleList")
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().addICCard(validityInfo, lockData, object : AddICCardCallback {
                    override fun onEnterAddMode() {
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTLockEvent.addCardProgrress, null)
                    }

                    override fun onAddICCardSuccess(cardNum: Long) {
                        successCallback.invoke(cardNum.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun recoverCard(
        cardNumber: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        val recoveryData = RecoveryData()
        LogUtil.d("cycleList:$cycleList")
        recoveryData.cardType = if (cycleList == null || cycleList.size() == 0) 1 else 4
        if (cycleList != null && cycleList.size() > 0) {
            recoveryData.cyclicConfig = Utils.readableArray2CyclicList(cycleList)
        }
        recoveryData.cardNumber = cardNumber
        recoveryData.startDate = startDate.toLong()
        recoveryData.endDate = endDate.toLong()
        val recoveryDataList = ArrayList<RecoveryData>()
        recoveryDataList.add(recoveryData)
        recoverLockData(GsonUtil.toJson(recoveryDataList), RecoveryDataType.IC, lockData, successCallback, fail)
    }

    private fun recoverLockData(
        recoveryDataJson: String,
        recoveryType: Int,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().recoverLockData(
                    recoveryDataJson,
                    recoveryType,
                    lockData,
                    null,
                    object : RecoverLockDataCallback {
                        override fun onRecoveryDataSuccess(type: Int) {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun modifyCardValidityPeriod(
        cardNumber: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        if (TextUtils.isEmpty(cardNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }

        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyICCardValidityPeriod(
                    validityInfo,
                    cardNumber,
                    lockData,
                    object : ModifyICCardPeriodCallback {
                        override fun onModifyICCardPeriodSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun deleteCard(cardNumber: String, lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(cardNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deleteICCard(cardNumber, lockData, null, object : DeleteICCardCallback {
                    override fun onDeleteICCardSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearAllCards(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearAllICCard(lockData, null, object : ClearAllICCardCallback {
                    override fun onClearAllICCardSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addFingerprint(
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFingerprint(validityInfo, lockData, object : AddFingerprintCallback {
                    override fun onEnterAddMode(totalCount: Int) {
                        totalCnt = totalCount
                        val writableArray = Arguments.createArray()
                        writableArray.pushInt(0)
                        writableArray.pushInt(totalCnt)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTLockEvent.addFingerprintProgress, writableArray)
                    }

                    override fun onCollectFingerprint(currentCount: Int) {
                        val writableArray = Arguments.createArray()
                        writableArray.pushInt(currentCount)
                        writableArray.pushInt(totalCnt)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTLockEvent.addFingerprintProgress, writableArray)
                    }

                    override fun onAddFingerpintFinished(fingerprintNum: Long) {
                        successCallback.invoke(fingerprintNum.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun modifyFingerprintValidityPeriod(
        fingerprintNumber: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        if (TextUtils.isEmpty(fingerprintNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }

        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyFingerprintValidityPeriod(
                    validityInfo,
                    fingerprintNumber,
                    lockData,
                    object : ModifyFingerprintPeriodCallback {
                        override fun onModifyPeriodSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun deleteFingerprint(fingerprintNumber: String, lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(fingerprintNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deleteFingerprint(fingerprintNumber, lockData, null, object : DeleteFingerprintCallback {
                    override fun onDeleteFingerprintSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearAllFingerprints(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearAllFingerprints(lockData, null, object : ClearAllFingerprintCallback {
                    override fun onClearAllFingerprintSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun modifyAdminPasscode(adminPasscode: String, lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(adminPasscode)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().modifyAdminPasscode(adminPasscode, lockData, null, object : ModifyAdminPasscodeCallback {
                    override fun onModifyAdminPasscodeSuccess(passcode: String) {
                        successCallback.invoke(passcode)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLockTime(timestamp: Double, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setLockTime(timestamp.toLong(), lockData, null, object : SetLockTimeCallback {
                    override fun onSetTimeSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockTime(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockTime(lockData, null, object : GetLockTimeCallback {
                    override fun onGetLockTimeSuccess(lockTimestamp: Long) {
                        successCallback.invoke(lockTimestamp.toString())
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockOperationRecord(type: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getOperationLog(
                    if (type == 0) LogType.NEW else LogType.ALL,
                    lockData,
                    null,
                    object : GetOperationLogCallback {
                        override fun onGetLogSuccess(log: String) {
                            successCallback.invoke(log)
                        }

                        override fun onFail(error: LockError) {
                            lockErrorCallback(error, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockAutomaticLockingPeriodicTime(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getAutomaticLockingPeriod(lockData, object : GetAutoLockingPeriodCallback {
                    override fun onGetAutoLockingPeriodSuccess(currtentTime: Int, minTime: Int, maxTime: Int) {
                        val writableArray = Arguments.createArray()
                        writableArray.pushInt(currtentTime)
                        writableArray.pushInt(maxTime)
                        writableArray.pushInt(minTime)
                        successCallback.invoke(writableArray)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLockAutomaticLockingPeriodicTime(seconds: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setAutomaticLockingPeriod(seconds, lockData, null, object : SetAutoLockingPeriodCallback {
                    override fun onSetAutoLockingPeriodSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockRemoteUnlockSwitchState(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getRemoteUnlockSwitchState(lockData, null, object : GetRemoteUnlockStateCallback {
                    override fun onGetRemoteUnlockSwitchStateSuccess(enabled: Boolean) {
                        successCallback.invoke(enabled)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLockRemoteUnlockSwitchState(isOn: Boolean, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setRemoteUnlockSwitchState(isOn, lockData, null, object : SetRemoteUnlockSwitchCallback {
                    override fun onSetRemoteUnlockSwitchSuccess(lockData: String) {
                        successCallback.invoke(lockData)
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockConfig(config: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockConfig(
                    TTLockConfigConverter.RN2Native(config),
                    lockData,
                    object : GetLockConfigCallback {
                        override fun onGetLockConfigSuccess(ttLockConfigType: TTLockConfigType, switchOn: Boolean) {
                            LogUtil.d("ttLockConfigType:$switchOn")
                            val writableArray = Arguments.createArray()
                            writableArray.pushInt(TTLockConfigConverter.native2RN(ttLockConfigType))
                            writableArray.pushBoolean(switchOn)
                            successCallback.invoke(writableArray)
                        }

                        override fun onFail(error: LockError) {
                            lockErrorCallback(error, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLockConfig(config: Int, isOn: Boolean, lockData: String, successCallback: Callback, fail: Callback) {
        val ttLockConfigType = TTLockConfigConverter.RN2Native(config)
        if (ttLockConfigType == null) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        LogUtil.d("ttLockConfigType:$isOn")
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setLockConfig(ttLockConfigType, isOn, lockData, object : SetLockConfigCallback {
                    override fun onSetLockConfigSuccess(ttLockConfigType: TTLockConfigType) {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addPassageMode(
        type: Int,
        weekly: ReadableArray?,
        monthly: ReadableArray?,
        startDate: Int,
        endDate: Int,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val passageModeConfig = PassageModeConfig()
                passageModeConfig.modeType = if (type == 0) PassageModeType.Weekly else PassageModeType.Monthly
                if (passageModeConfig.modeType == PassageModeType.Weekly) {
                    passageModeConfig.repeatWeekOrDays = Utils.readableArray2IntJson(weekly)
                } else {
                    passageModeConfig.repeatWeekOrDays = Utils.readableArray2IntJson(monthly)
                }
                passageModeConfig.startDate = startDate
                passageModeConfig.endDate = endDate

                LogUtil.d("weekdays:" + passageModeConfig.repeatWeekOrDays)

                TTLockClient.getDefault().setPassageMode(passageModeConfig, lockData, null, object : SetPassageModeCallback {
                    override fun onSetPassageModeSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearAllPassageModes(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearPassageMode(lockData, null, object : ClearPassageModeCallback {
                    override fun onClearPassageModeSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(error: LockError) {
                        lockErrorCallback(error, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockVersionWithLockMac(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockVersion(lockData, object : GetLockVersionCallback {
                    override fun onGetLockVersionSuccess(lockVersion: String) {
                        successCallback.invoke(lockVersion)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLockSoundVolume(soundVolumeValue: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                var soundVolume: SoundVolume = SoundVolume.OFF
                when (soundVolumeValue) {
                    -1 -> soundVolume = SoundVolume.ON
                    0 -> soundVolume = SoundVolume.OFF
                    1, 2, 3, 4, 5 -> soundVolume = SoundVolume.getInstance(soundVolumeValue)
                }
                TTLockClient.getDefault().setLockSoundWithSoundVolume(soundVolume, lockData, object : SetLockSoundWithSoundVolumeCallback {
                    override fun onSetLockSoundSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockSoundVolume(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockSoundWithSoundVolume(lockData, object : GetLockSoundWithSoundVolumeCallback {
                    override fun onGetLockSoundSuccess(enable: Boolean, soundVolume: SoundVolume) {
                        var soundVolumeValue = 0
                        if (enable) {
                            soundVolumeValue = when (soundVolume) {
                                SoundVolume.ON -> -1
                                SoundVolume.OFF -> 0
                                else -> soundVolume.value
                            }
                        }
                        successCallback.invoke(soundVolumeValue)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setUnlockDirection(direction: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setUnlockDirection(
                    if (direction == 1) UnlockDirection.LEFT else UnlockDirection.RIGHT,
                    lockData,
                    object : SetUnlockDirectionCallback {
                        override fun onSetUnlockDirectionSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setUnlockDirectionAutomatic(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().autoSetUnlockDirection(lockData, object : AutoSetUnlockDirectionCallback {
                    override fun onSetSuccess(autoUnlockDirection: AutoUnlockDirection) {
                        val direction = when (autoUnlockDirection) {
                            AutoUnlockDirection.LEFT -> 1
                            AutoUnlockDirection.RIGHT -> 2
                        }
                        successCallback.invoke(direction)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getUnlockDirection(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getUnlockDirection(lockData, object : GetUnlockDirectionCallback {
                    override fun onGetUnlockDirectionSuccess(unlockDirection: UnlockDirection) {
                        successCallback.invoke(if (unlockDirection.value == 1) 1 else 2)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addRemoteKey(
        remoteMac: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().addRemote(remoteMac, validityInfo, lockData, object : AddRemoteCallback {
                    override fun onAddSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun modifyRemoteKey(
        remoteMac: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().modifyRemoteValidityPeriod(
                    remoteMac,
                    validityInfo,
                    lockData,
                    object : ModifyRemoteValidityPeriodCallback {
                        override fun onModifySuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun deleteRemoteKey(remoteMac: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deleteRemote(remoteMac, lockData, object : DeleteRemoteCallback {
                    override fun onDeleteSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockElectricQuantity(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getBatteryLevel(lockData, null, object : GetBatteryLevelCallback {
                    override fun onGetBatteryLevelSuccess(battery: Int) {
                        successCallback.invoke(battery)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearAllRemoteKey(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearRemote(lockData, object : ClearRemoteCallback {
                    override fun onClearSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addDoorSensor(doorSensorMac: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().addDoorSensor(doorSensorMac, lockData, object : AddDoorSensorCallback {
                    override fun onAddSuccess() {
                        LogUtil.d("add door sensor to lock success")
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("add door sensor to lock failed:" + lockError.description)
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearAllDoorSensor(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deleteDoorSensor(lockData, object : DeleteDoorSensorCallback {
                    override fun onDeleteSuccess() {
                        LogUtil.d("clear door sensor success")
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("clear door sensor failed:" + lockError.description)
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setDoorSensorAlertTime(time: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setDoorSensorAlertTime(time, lockData, object : SetDoorSensorAlertTimeCallback {
                    override fun onSetDoorSensorAlertTimeSuccess() {
                        LogUtil.d("set door sensor alert time success")
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("set door sensor failed:" + lockError.description)
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getAccessoryElectricQuantity(
        type: Int,
        mac: String,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val accessoryInfo = AccessoryInfo()
                accessoryInfo.accessoryMac = mac
                val accessoryType = when (type) {
                    1 -> AccessoryType.WIRELESS_KEYPAD
                    2 -> AccessoryType.REMOTE
                    3 -> AccessoryType.DOOR_SENSOR
                    else -> null
                }
                accessoryInfo.accessoryType = accessoryType
                TTLockClient.getDefault().getAccessoryBatteryLevel(
                    accessoryInfo,
                    lockData,
                    object : GetAccessoryBatteryLevelCallback {
                        override fun onGetAccessoryBatteryLevelSuccess(accessoryInfo: AccessoryInfo) {
                            val writableArray = Arguments.createArray()
                            writableArray.pushInt(accessoryInfo.accessoryBattery)
                            writableArray.pushDouble(accessoryInfo.batteryDate)
                            successCallback.invoke(writableArray)
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun scanWifi(lockData: String, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().scanWifi(lockData, object : ScanWifiCallback {
                    override fun onScanWifi(wiFis: List<WiFi>, status: Int) {
                        if (wiFis != null) {
                            val writableArray = Arguments.createArray()
                            writableArray.pushBoolean(status == 1)

                            val readableArray = Arguments.createArray()
                            for (wiFi in wiFis) {
                                val map = Arguments.createMap()
                                map.putString(TTGatewayFieldConstant.WIFI, wiFi.ssid)
                                map.putInt(TTGatewayFieldConstant.RSSI, wiFi.rssi)
                                readableArray.pushMap(map)
                            }
                            writableArray.pushArray(readableArray)
                            reactApplicationContext
                                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit(TTLockEvent.scanWifi, writableArray)
                        }
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun configWifi(wifiName: String, wifiPassword: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().configWifi(wifiName, wifiPassword, lockData, object : ConfigWifiCallback {
                    override fun onConfigWifiSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun configServer(ip: String, port: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                try {
                    val portNumber = port.toInt()
                TTLockClient.getDefault().configServer(ip, portNumber, lockData, object : ConfigServerCallback {
                    override fun onConfigServerSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            }
        } else {
            noPermissionCallback(fail)
        }
    }

    override fun getWifiInfo(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getWifiInfo(lockData, object : GetWifiInfoCallback {
                    override fun onGetWiFiInfoSuccess(wifiLockInfo: WifiLockInfo) {
                        val writableArray = Arguments.createArray()
                        writableArray.pushString(wifiLockInfo.wifiMac)
                        writableArray.pushInt(wifiLockInfo.wifiRssi)
                        successCallback.invoke(writableArray)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun configIp(readableMap: ReadableMap, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().configIp(
                    IpSettingConverter.toObject(readableMap),
                    lockData,
                    object : com.ttlock.bl.sdk.callback.ConfigIpCallback {
                        override fun onConfigIpSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addFace(
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFace(lockData, validityInfo, object : AddFaceCallback {
                    override fun onEnterAddMode() {
                        val writableArray = Arguments.createArray()
                        writableArray.pushInt(0)
                        writableArray.pushInt(0)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTLockEvent.addFaceProgrress, writableArray)
                    }

                    override fun onCollectionStatus(faceCollectionStatus: FaceCollectionStatus) {
                        val writableArray = Arguments.createArray()
                        writableArray.pushInt(0)
                        writableArray.pushInt(faceCollectionStatus.value)
                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit(TTLockEvent.addFaceProgrress, writableArray)
                    }

                    override fun onAddFinished(faceNumber: Long) {
                        successCallback.invoke(faceNumber.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun addFaceFeatureData(
        faceFeatureData: String,
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFaceFeatureData(lockData, faceFeatureData, validityInfo, object : AddFaceCallback {
                    override fun onEnterAddMode() {
                        // Empty
                    }

                    override fun onCollectionStatus(faceCollectionStatus: FaceCollectionStatus) {
                        // Empty
                    }

                    override fun onAddFinished(faceNumber: Long) {
                        successCallback.invoke(faceNumber.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun modifyFaceValidityPeriod(
        cycleList: ReadableArray?,
        startDate: Double,
        endDate: Double,
        faceNumber: String,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        if (TextUtils.isEmpty(faceNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }

        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.size() == 0) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.readableArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyFaceValidityPeriod(
                    lockData,
                    faceNumber.toLong(),
                    validityInfo,
                    object : ModifyFacePeriodCallback {
                        override fun onModifySuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun deleteFace(faceNumber: String, lockData: String, successCallback: Callback, fail: Callback) {
        if (TextUtils.isEmpty(faceNumber)) {
            lockErrorCallback(LockError.DATA_FORMAT_ERROR, fail)
            return
        }
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().deleteFace(lockData, faceNumber.toLong(), object : DeleteFaceCallback {
                    override fun onDeleteSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearFace(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearFace(lockData, object : ClearFaceCallback {
                    override fun onClearSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getLockSystem(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getLockSystemInfo(lockData, null, object : GetLockSystemInfoCallback {
                    override fun onGetLockSystemInfoSuccess(deviceInfo: com.ttlock.bl.sdk.entity.DeviceInfo) {
                        val map = Arguments.createMap()
                        map.putString(TTBaseFieldConstant.MODEL_NUM, deviceInfo.modelNum)
                        map.putString(TTBaseFieldConstant.HARDWARE_REVISION, deviceInfo.hardwareRevision)
                        map.putString(TTBaseFieldConstant.FIRMWARE_REVISION, deviceInfo.firmwareRevision)
                        map.putString(TTLockFieldConstant.LOCK_DATA, deviceInfo.lockData)
                        successCallback.invoke(map)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getWifiPowerSavingTime(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().getWifiPowerSavingTimes(lockData, object : GetWifiPowerSavingTimesCallback {
                    override fun onGetSuccess(powerSavingData: String) {
                        successCallback.invoke(powerSavingData)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun configWifiPowerSavingTime(
        days: ReadableArray?,
        startDate: Int,
        endDate: Int,
        lockData: String,
        successCallback: Callback,
        fail: Callback
    ) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().configWifiPowerSavingTimes(
                    Utils.readableArray2IntList(days),
                    startDate,
                    endDate,
                    lockData,
                    object : ConfigWifiPowerSavingTimesCallback {
                        override fun onConfigSuccess() {
                            successCallback.invoke()
                        }

                        override fun onFail(lockError: LockError) {
                            lockErrorCallback(lockError, fail)
                        }
                    }
                )
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun clearWifiPowerSavingTime(lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().clearWifiPowerSavingTimes(lockData, object : ClearWifiPowerSavingTimesCallback {
                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }

                    override fun onClearSuccess() {
                        successCallback.invoke()
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun activateLiftFloors(floors: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                val list = ArrayList<Int>()
                if (!TextUtils.isEmpty(floors)) {
                    val split = floors.split(",")
                    for (s in split) {
                        list.add(s.toInt())
                    }
                }
                TTLockClient.getDefault().activateLiftFloors(list, 0, lockData, object : ActivateLiftFloorsCallback {
                    override fun onActivateLiftFloorsSuccess(activateLiftFloorsResult: ActivateLiftFloorsResult) {
                        val writableArray = Arguments.createArray()
                        writableArray.pushDouble(activateLiftFloorsResult.deviceTime)
                        writableArray.pushInt(activateLiftFloorsResult.battery)
                        writableArray.pushInt(activateLiftFloorsResult.uniqueid)
                        successCallback.invoke(writableArray)
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLiftControlEnableFloors(floors: String, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                TTLockClient.getDefault().setLiftControlableFloors(floors, lockData, object : SetLiftControlableFloorsCallback {
                    override fun onSetLiftControlableFloorsSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun setLiftWorkMode(workMode: Int, lockData: String, successCallback: Callback, fail: Callback) {
        PermissionUtils.doWithConnectPermission(currentActivity) { success ->
            if (success) {
                // enum LiftWorkMode {
                //   ACTIVATE_ALL_FLOORS = 0,
                //   ACTIVATE_SPECIFIC_FLOORS = 1
                // }
                var liftWorkMode: TTLiftWorkMode = TTLiftWorkMode.ActivateAllFloors
                if (workMode == 1) {
                    liftWorkMode = TTLiftWorkMode.ActivateSpecificFloors
                }
                TTLockClient.getDefault().setLiftWorkMode(liftWorkMode, lockData, object : SetLiftWorkModeCallback {
                    override fun onSetLiftWorkModeSuccess() {
                        successCallback.invoke()
                    }

                    override fun onFail(lockError: LockError) {
                        lockErrorCallback(lockError, fail)
                    }
                })
            } else {
                noPermissionCallback(fail)
            }
        }
    }

    override fun getBluetoothState(callback: Callback) {
        val enable = TTLockClient.getDefault().isBLEEnabled(currentActivity)
        //4-on, 5-off
        callback.invoke(if (enable) 4 else 5)
    }

    override fun supportFunction(function: Int, lockData: String, callback: Callback) {
        val support = FeatureValueUtil.isSupportFeature(lockData, function)
        callback.invoke(support)
    }

    private fun lockErrorCallback(lockError: LockError, fail: Callback?) {
        if (fail != null) {
            fail.invoke(TTLockErrorConverter.native2RN(lockError), lockError.errorMsg)
        }
    }

    private fun noPermissionCallback(fail: Callback) {
        lockErrorCallback(LockError.LOCK_NO_PERMISSION, fail)
    }
}
