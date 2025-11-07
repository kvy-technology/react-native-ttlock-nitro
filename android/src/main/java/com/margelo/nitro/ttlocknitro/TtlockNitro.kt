package com.margelo.nitro.ttlocknitro

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
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
import com.margelo.nitro.ttlocknitro.util.OnSuccessListener
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
import com.margelo.nitro.core.AnyMap

@DoNotStrip
class TtlockNitro : HybridTtlockNitroSpec() {

    private val context = NitroModules.applicationContext
        ?: throw RuntimeException("Application context is missing")
    private var scanType: Int = 0
    private val mCachedDevice: HashMap<String, ExtendedBluetoothDevice> = HashMap()
    private val mCachedRemote: HashMap<String, Remote> = HashMap()
    private val mCachedDoorSensor: HashMap<String, WirelessDoorSensor> = HashMap()
    private val mCachedKeypad: HashMap<String, WirelessKeypad> = HashMap()
    private var mac: String? = null
    private var totalCnt: Int = 0
    private var flag: Boolean = false
    private val listeners: MutableMap<String, MutableList<(String, AnyMap?) -> Unit>> = HashMap()

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
        const val TYPE_LOCK = 1
        const val TYPE_PLUG = 2
        const val TYPE_REMOTE = 3
        const val TYPE_DOOR_SENSOR = 4
        const val TYPE_WIRELESS_KEYPAD = 5
        const val TAG = "TtlockNitro"
    }

    init {
        LogUtil.setDBG(true)
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
    override fun startScanWirelessKeypad() {
        scanType = TYPE_WIRELESS_KEYPAD
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithScanPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    mCachedKeypad.clear()
                    WirelessKeypadClient.getDefault().startScanKeyboard(object : ScanKeypadCallback {
                        override fun onScanKeyboardSuccess(wirelessKeypad: WirelessKeypad) {
                            mCachedKeypad[wirelessKeypad.address] = wirelessKeypad
                            val map = AnyMap()
                            map.setString(TTKeypadConstant.KEYPAD_NAME, wirelessKeypad.name)
                            map.setString(TTKeypadConstant.KEYPAD_MAC, wirelessKeypad.address)
                            map.setDouble(TTBaseFieldConstant.RSSI, wirelessKeypad.rssi.toDouble())
                            sendEvent(TTKeypadEvent.ScanKeypad, map)
                        }

                        override fun onScanFailed(error: Int) {
                            // Handle error
                        }
                    })
                } else {
                    LogUtil.d("no scan permission")
                }

            }
        })
    }

    override fun stopScanWirelessKeypad() {
        WirelessKeypadClient.getDefault().stopScanKeyboard()
    }

    override fun initWirelessKeypad(keypadMac: String, lockMac: String, resolve: (data: NumberStringPair) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        WirelessKeypadClient.getDefault().initializeKeypad(
            mCachedKeypad[keypadMac],
            lockMac,
            object : InitKeypadCallback {
                override fun onInitKeypadSuccess(initKeypadResult: InitKeypadResult) {
                    resolve(NumberStringPair(
                        initKeypadResult.batteryLevel.toDouble(),
                        initKeypadResult.featureValue
                    ))
                }

                override fun onFail(error: KeypadError) {
                    reject(TTKeypadErrorConverter.native2RN(error).toDouble(), error.description)
                }
            }
        )
    }

    //-------------door sensor---------------------------
    override fun startScanDoorSensor() {
        scanType = TYPE_DOOR_SENSOR
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithScanPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    mCachedDoorSensor.clear()
                    WirelessDoorSensorClient.getDefault().startScan(object : ScanWirelessDoorSensorCallback {
                        override fun onScan(doorSensor: WirelessDoorSensor) {
                            mCachedDoorSensor[doorSensor.address] = doorSensor
                            val map = AnyMap()
                            map.setString(TTDoorSensorFieldConstant.DOOR_SENSOR_NAME, doorSensor.name)
                            map.setString(TTDoorSensorFieldConstant.DOOR_SENSOR_MAC, doorSensor.address)
                            map.setDouble(TTBaseFieldConstant.RSSI, doorSensor.rssi.toDouble())
                            sendEvent(TTDoorSensorEvent.ScanDoorSensor, map)
                        }
                    })
                } else {
                    LogUtil.d("no scan permission")
                }

            }
        })
    }

    override fun stopScanDoorSensor() {
        WirelessDoorSensorClient.getDefault().stopScan()
    }

    override fun initDoorSensor(doorSensorMac: String, lockData: String, resolve: (data: NumberStringPair) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        LogUtil.d("init doorsensor start")
        LogUtil.d("mCachedDoorSensor.get(doorSensorMac):" + mCachedDoorSensor[doorSensorMac])
        WirelessDoorSensorClient.getDefault().initialize(
            mCachedDoorSensor[doorSensorMac],
            lockData,
            object : InitDoorSensorCallback {
                override fun onInitSuccess(initDoorSensorResult: InitDoorSensorResult) {
                    resolve(NumberStringPair(
                        initDoorSensorResult.batteryLevel.toDouble(),
                        GsonUtil.toJson(initDoorSensorResult.firmwareInfo)
                    ))
                }

                override fun onFail(doorSensorError: DoorSensorError) {
                    reject(TTDoorSensorErrorConverter.native2RN(doorSensorError).toDouble(), doorSensorError.description)
                }
            }
        )
    }

    //---------------remote------------------------------
    override fun startScanRemoteKey() {
        scanType = TYPE_REMOTE
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithScanPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    mCachedRemote.clear()
                    RemoteClient.getDefault().startScan(object : ScanRemoteCallback {
                        override fun onScanRemote(remote: Remote) {
                            mCachedRemote[remote.address] = remote
                            val map = AnyMap()
                            map.setString(TTRemoteFieldConstant.REMOTE_KEY_NAME, remote.name)
                            map.setString(TTRemoteFieldConstant.REMOTE_KEY_MAC, remote.address)
                            map.setDouble(TTRemoteFieldConstant.RSSI, remote.rssi.toDouble())
                            sendEvent(TTRemoteEvent.ScanRemoteKey, map)
                        }
                    })
                } else {
                    LogUtil.d("no scan permission")
                }

            }
        })
    }

    override fun stopScanRemoteKey() {
        RemoteClient.getDefault().stopScan()
    }

    override fun initRemoteKey(remoteMac: String, lockData: String, resolve: (data: NumberStringPair) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        RemoteClient.getDefault().initialize(
            mCachedRemote[remoteMac],
            lockData,
            object : InitRemoteCallback {
                override fun onInitSuccess(initRemoteResult: InitRemoteResult) {
                    resolve(NumberStringPair(
                        initRemoteResult.batteryLevel.toDouble(),
                        GsonUtil.toJson(initRemoteResult.systemInfo)
                    ))
                }

                override fun onFail(remoteError: RemoteError) {
                    reject(TTRemoteKeyErrorConverter.native2RN(remoteError).toDouble(), remoteError.description)
                }
            }
        )
    }

    override fun getRemoteKeySystemInfo(remoteMac: String, resolve: (data: DeviceSystemModal) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        RemoteClient.getDefault().getRemoteSystemInfo(remoteMac, object : GetRemoteSystemInfoCallback {
            override fun onGetRemoteSystemInfoSuccess(systemInfo: SystemInfo) {
                resolve(DeviceSystemModal(
                    modelNum = systemInfo.modelNum ?: "",
                    hardwareRevision = systemInfo.hardwareRevision ?: "",
                    firmwareRevision = systemInfo.firmwareRevision ?: "",
                    nbOperator = "",
                    nbNodeId = "",
                    nbCardNumber = "",
                    nbRssi = "",
                    passcodeKeyNumber = "",
                    lockData = ""
                ))
            }

            override fun onFail(remoteError: RemoteError) {
                reject(TTRemoteKeyErrorConverter.native2RN(remoteError).toDouble(), remoteError.description)
            }
        })
    }

    //-------------gateway---------------
    override fun startScanGateway() {
        scanType = TYPE_PLUG
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithScanPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    GatewayClient.getDefault().prepareBTService(activity)
                    GatewayClient.getDefault().startScanGateway(object : ScanGatewayCallback {
                        override fun onScanGatewaySuccess(device: ExtendedBluetoothDevice) {
                            val map = AnyMap()
                            map.setString(TTGatewayFieldConstant.GATEWAY_NAME, device.name)
                            map.setString(TTGatewayFieldConstant.GATEWAY_MAC, device.address)
                            map.setBoolean(TTGatewayFieldConstant.IS_DFU_MODE, device.isDfuMode)
                            map.setDouble(TTGatewayFieldConstant.RSSI, device.rssi.toDouble())
                            map.setDouble(TTGatewayFieldConstant.TYPE, device.gatewayType.toDouble())
                            sendEvent(TTGatewayEvent.scanGateway, map)
                        }

                        override fun onScanFailed(errorCode: Int) {
                            LogUtil.d("errorCode:$errorCode")
                        }
                    })
                } else {
                    LogUtil.d("no scan permission")
                }

            }
        })
    }

    override fun stopScanGateway() {
        GatewayClient.getDefault().stopScanGateway()
    }

    override fun connect(mac: String, resolve: (state: Double) -> Unit) {
        if (mac.isEmpty()) {
            LogUtil.d("mac is null")
            resolve(0.0)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            resolve(0.0)
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    this@TtlockNitro.mac = mac
                    flag = false
                    GatewayClient.getDefault().connectGateway(mac, object : ConnectCallback {
                        override fun onConnectSuccess(device: ExtendedBluetoothDevice) {
                            LogUtil.d("connected:$device")
                            flag = true
                            resolve(1.0)
                        }

                        override fun onDisconnected() {
                            try {
                                if (!flag) {
                                    flag = true
                                    resolve(0.0)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                } else {
                    if (!flag) {
                        flag = true
                        resolve(0.0)
                    }
                }
            }
        })
    }

    override fun getNearbyWifi(resolve: (state: Double) -> Unit) {
        if (mac.isNullOrEmpty()) {
            LogUtil.d("mac is null")
            resolve(1.0)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            resolve(1.0)
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    GatewayClient.getDefault().scanWiFiByGateway(mac!!, object : ScanWiFiByGatewayCallback {
                        override fun onScanWiFiByGateway(wiFis: List<WiFi>) {
                            if (wiFis != null) {
                                val wifiList = wiFis.map { wiFi ->
                                    val map = AnyMap()
                                    map.setString(TTGatewayFieldConstant.WIFI, wiFi.ssid)
                                    map.setDouble(TTGatewayFieldConstant.RSSI, wiFi.rssi.toDouble())
                                    map
                                }
                                val data = AnyMap()
                                data.setAny("wifis", wifiList.toTypedArray())
                                sendEvent(TTGatewayEvent.scanWifi, data)
                            }
                        }

                        override fun onScanWiFiByGatewaySuccess() {
                            resolve(0.0)
                        }

                        override fun onFail(error: GatewayError) {
                            resolve(1.0)
                        }
                    })
                } else {
                    resolve(1.0)
                }
            }
        })
    }

    private fun configIpHelper(gatewayInfo: ConfigureGatewayInfo, ipSetting: IpSetting, resolve: (data: InitGatewayModal) -> Unit, reject: (errorCode: Double) -> Unit) {
        GatewayClient.getDefault().configIp(mac!!, ipSetting, object : ConfigIpCallback {
            override fun onConfigIpSuccess() {
                doInitGatewayHelper(gatewayInfo, resolve, reject)
            }

            override fun onFail(gatewayError: GatewayError) {
                reject(TTGatewayErrorConverter.native2RN(gatewayError).toDouble())
            }
        })
    }

    private fun doInitGatewayHelper(gatewayInfo: ConfigureGatewayInfo, resolve: (data: InitGatewayModal) -> Unit, reject: (errorCode: Double) -> Unit) {
        GatewayClient.getDefault().initGateway(gatewayInfo, object : InitGatewayCallback {
            override fun onInitGatewaySuccess(deviceInfo: DeviceInfo) {
                resolve(InitGatewayModal(
                    modelNum = deviceInfo.modelNum ?: "",
                    hardwareRevision = deviceInfo.hardwareRevision ?: "",
                    firmwareRevision = deviceInfo.firmwareRevision ?: ""
                ))
            }

            override fun onFail(error: GatewayError) {
                LogUtil.d("error:" + error.description)
                reject(TTGatewayErrorConverter.native2RN(error).toDouble())
            }
        })
    }

    override fun initGateway(params: InitGatewayParam, resolve: (data: InitGatewayModal) -> Unit, reject: (errorCode: Double) -> Unit) {
        val gatewayInfo = ConfigureGatewayInfo()
        gatewayInfo.plugName = params.gatewayName
        gatewayInfo.plugVersion = params.type.toInt()
        if (gatewayInfo.plugVersion == GatewayType.G2 || gatewayInfo.plugVersion == GatewayType.G5 || gatewayInfo.plugVersion == GatewayType.G6) {
            gatewayInfo.ssid = params.wifi
            gatewayInfo.wifiPwd = params.wifiPassword
        }
        gatewayInfo.uid = params.ttLockUid.toInt()
        gatewayInfo.userPwd = params.ttLockLoginPassword

        if (params.ipSettingType != null && params.ipSettingType.toInt() == IpSetting.STATIC_IP) {
            val ipSetting = IpSetting()
            ipSetting.type = IpSetting.STATIC_IP
            ipSetting.ipAddress = params.ipAddress
            ipSetting.subnetMask = params.subnetMask
            ipSetting.router = params.router
            ipSetting.preferredDns = params.preferredDns
            ipSetting.alternateDns = params.alternateDns
            configIpHelper(gatewayInfo, ipSetting, resolve, reject)
        } else {
            doInitGatewayHelper(gatewayInfo, resolve, reject)
        }
    }

    //-----------lock--------------
    override fun startScan() {
        LogUtil.d("start scan")
        scanType = TYPE_LOCK
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithScanPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().startScanLock(object : ScanLockCallback {
                        override fun onScanLockSuccess(extendedBluetoothDevice: ExtendedBluetoothDevice) {
                        if (extendedBluetoothDevice != null) {
                            cacheAndFilterScanDevice(extendedBluetoothDevice)
                            val map = AnyMap()
                            map.setString(TTLockFieldConstant.LOCK_NAME, extendedBluetoothDevice.name)
                            map.setString(TTLockFieldConstant.LOCK_MAC, extendedBluetoothDevice.address)
                            map.setBoolean(TTLockFieldConstant.IS_INITED, !extendedBluetoothDevice.isSettingMode)
                            map.setBoolean(TTLockFieldConstant.IS_KEYBOARD_ACTIVATED, extendedBluetoothDevice.isTouch)
                            map.setDouble(TTLockFieldConstant.ELECTRIC_QUANTITY, extendedBluetoothDevice.batteryCapacity.toDouble())
                            map.setString(TTLockFieldConstant.LOCK_VERSION, extendedBluetoothDevice.lockVersionJson)
                            map.setDouble(TTLockFieldConstant.RSSI, extendedBluetoothDevice.rssi.toDouble())
                            map.setDouble(TTLockFieldConstant.LOCK_SWITCH_STATE, extendedBluetoothDevice.parkStatus.toDouble())
                            sendEvent(TTLockEvent.scanLock, map)
                        }
                    }

                    override fun onFail(error: LockError) {
                        LogUtil.d("error:$error")
                    }
                })
            }
            }
        })
    }

    override fun stopScan() {
        TTLockClient.getDefault().stopScanLock()
    }

    override fun initLock(params: InitLockParam, resolve: (lockData: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val lockmac = params.lockMac
        if (TextUtils.isEmpty(lockmac)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            LogUtil.d("lockmac is null")
            return
        }
        val device = mCachedDevice[lockmac]
        if (device == null) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            LogUtil.d("device is null")
            return
        }
        if (params.clientPara != null && !TextUtils.isEmpty(params.clientPara)) {
            device.manufacturerId = params.clientPara
        }
        TTLockClient.getDefault().initLock(device, object : InitLockCallback {
            override fun onInitLockSuccess(lockData: String) {
                resolve(lockData)
            }

            override fun onFail(error: LockError) {
                reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
            }
        })
    }

    override fun resetLock(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().resetLock(lockData, null, object : ResetLockCallback {
                        override fun onResetLockSuccess() {
                            resolve()
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    })
                } else {
                    reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
                }

            }
        })
    }

    override fun resetEkey(lockData: String, resolve: (lockData: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().resetEkey(lockData, null, object : ResetKeyCallback {
                        override fun onResetKeySuccess(lockData: String) {
                            resolve(lockData)
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    })
                } else {
                    reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
                }

            }
        })
    }

    override fun controlLock(controlAction: Double, lockData: String, resolve: (data: NumberNumberNumberTriple) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (!RNControlAction.isValidAction(controlAction.toInt()) || TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().controlLock(
                    RNControlAction.RN2Native(controlAction.toInt()),
                    lockData,
                    null,
                    object : ControlLockCallback {
                        override fun onControlLockSuccess(controlLockResult: ControlLockResult) {
                            resolve(NumberNumberNumberTriple(
                                controlLockResult.lockTime.toDouble(),
                                controlLockResult.battery.toDouble(),
                                controlLockResult.uniqueid.toDouble()
                            ))
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun createCustomPasscode(
        passcode: String,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        LogUtil.d("passcode:$passcode")
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        LogUtil.d("startDate:$startDate")
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().createCustomPasscode(
                    passcode,
                    startDate.toLong(),
                    endDate.toLong(),
                    lockData,
                    null,
                    object : CreateCustomPasscodeCallback {
                        override fun onCreateCustomPasscodeSuccess(passcode: String) {
                            resolve()
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun recoverPasscode(
        passcode: String,
        passcodeType: Double,
        cycleType: Double,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val recoveryData = RecoveryData()
        recoveryData.keyboardPwd = passcode
        recoveryData.startDate = startDate.toLong()
        recoveryData.endDate = endDate.toLong()
        recoveryData.cycleType = cycleType.toInt()
        recoveryData.keyboardPwdType = passcodeType.toInt()
        val recoveryDataList = ArrayList<RecoveryData>()
        recoveryDataList.add(recoveryData)
        recoverLockDataHelper(GsonUtil.toJson(recoveryDataList), RecoveryDataType.PASSCODE, lockData, resolve, reject)
    }

    override fun modifyPasscode(
        passcodeOrigin: String,
        passcodeNew: String,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        if (TextUtils.isEmpty(passcodeOrigin) || TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
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
                            resolve()
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun deletePasscode(passcode: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(passcode) || TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deletePasscode(passcode, lockData, null, object : DeletePasscodeCallback {
                    override fun onDeletePasscodeSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun resetPasscode(lockData: String, resolve: (lockData: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().resetPasscode(lockData, null, object : ResetPasscodeCallback {
                    override fun onResetPasscodeSuccess(lockData: String) {
                        resolve(lockData)
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockSwitchState(lockData: String, resolve: (state: Double) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(lockData)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getLockStatus(lockData, null, object : GetLockStatusCallback {
                    override fun onGetLockStatusSuccess(status: Int) {
                        resolve(status.toDouble())
                    }

                    override fun onGetDoorSensorStatusSuccess(status: Int) {
                        //todo:
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addCard(
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: (cardNumber: String) -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        LogUtil.d("cycleList:$cycleList")
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().addICCard(validityInfo, lockData, object : AddICCardCallback {
                    override fun onEnterAddMode() {
                        sendEvent(TTLockEvent.addCardProgrress, null)
                    }

                    override fun onAddICCardSuccess(cardNum: Long) {
                        resolve(cardNum.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun recoverCard(
        cardNumber: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val recoveryData = RecoveryData()
        LogUtil.d("cycleList:$cycleList")
        recoveryData.cardType = if (cycleList == null || cycleList.isEmpty()) 1 else 4
        if (cycleList != null && cycleList.isNotEmpty()) {
            recoveryData.cyclicConfig = Utils.cycleDateArray2CyclicList(cycleList)
        }
        recoveryData.cardNumber = cardNumber
        recoveryData.startDate = startDate.toLong()
        recoveryData.endDate = endDate.toLong()
        val recoveryDataList = ArrayList<RecoveryData>()
        recoveryDataList.add(recoveryData)
        recoverLockDataHelper(GsonUtil.toJson(recoveryDataList), RecoveryDataType.IC, lockData, resolve, reject)
    }

    private fun recoverLockDataHelper(
        recoveryDataJson: String,
        recoveryType: Int,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().recoverLockData(
                    recoveryDataJson,
                    recoveryType,
                    lockData,
                    null,
                    object : RecoverLockDataCallback {
                        override fun onRecoveryDataSuccess(type: Int) {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun modifyCardValidityPeriod(
        cardNumber: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        if (TextUtils.isEmpty(cardNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }

        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyICCardValidityPeriod(
                    validityInfo,
                    cardNumber,
                    lockData,
                    object : ModifyICCardPeriodCallback {
                        override fun onModifyICCardPeriodSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun deleteCard(cardNumber: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(cardNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deleteICCard(cardNumber, lockData, null, object : DeleteICCardCallback {
                    override fun onDeleteICCardSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearAllCards(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearAllICCard(lockData, null, object : ClearAllICCardCallback {
                    override fun onClearAllICCardSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addFingerprint(
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: (fingerprintNumber: String) -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFingerprint(validityInfo, lockData, object : AddFingerprintCallback {
                    override fun onEnterAddMode(totalCount: Int) {
                        totalCnt = totalCount
                        val data = AnyMap()
                        data.setDouble("current", 0.0)
                        data.setDouble("total", totalCnt.toDouble())
                        sendEvent(TTLockEvent.addFingerprintProgress, data)
                    }

                    override fun onCollectFingerprint(currentCount: Int) {
                        val data = AnyMap()
                        data.setDouble("current", currentCount.toDouble())
                        data.setDouble("total", totalCnt.toDouble())
                        sendEvent(TTLockEvent.addFingerprintProgress, data)
                    }

                    override fun onAddFingerpintFinished(fingerprintNum: Long) {
                        resolve(fingerprintNum.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun modifyFingerprintValidityPeriod(
        fingerprintNumber: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        if (TextUtils.isEmpty(fingerprintNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }

        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyFingerprintValidityPeriod(
                    validityInfo,
                    fingerprintNumber,
                    lockData,
                    object : ModifyFingerprintPeriodCallback {
                        override fun onModifyPeriodSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun deleteFingerprint(fingerprintNumber: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(fingerprintNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deleteFingerprint(fingerprintNumber, lockData, null, object : DeleteFingerprintCallback {
                    override fun onDeleteFingerprintSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearAllFingerprints(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearAllFingerprints(lockData, null, object : ClearAllFingerprintCallback {
                    override fun onClearAllFingerprintSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun modifyAdminPasscode(adminPasscode: String, lockData: String, resolve: (passcode: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(adminPasscode)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().modifyAdminPasscode(adminPasscode, lockData, null, object : ModifyAdminPasscodeCallback {
                    override fun onModifyAdminPasscodeSuccess(passcode: String) {
                        resolve(passcode)
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLockTime(timestamp: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setLockTime(timestamp.toLong(), lockData, null, object : SetLockTimeCallback {
                    override fun onSetTimeSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockTime(lockData: String, resolve: (lockTimestamp: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getLockTime(lockData, null, object : GetLockTimeCallback {
                    override fun onGetLockTimeSuccess(lockTimestamp: Long) {
                        resolve(lockTimestamp.toString())
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockOperationRecord(type: Double, lockData: String, resolve: (records: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getOperationLog(
                    if (type.toInt() == 0) LogType.NEW else LogType.ALL,
                    lockData,
                    null,
                    object : GetOperationLogCallback {
                        override fun onGetLogSuccess(log: String) {
                            resolve(log)
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockAutomaticLockingPeriodicTime(lockData: String, resolve: (data: NumberNumberNumberTriple) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getAutomaticLockingPeriod(lockData, object : GetAutoLockingPeriodCallback {
                    override fun onGetAutoLockingPeriodSuccess(currtentTime: Int, minTime: Int, maxTime: Int) {
                        resolve(NumberNumberNumberTriple(
                            currtentTime.toDouble(),
                            maxTime.toDouble(),
                            minTime.toDouble()
                        ))
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLockAutomaticLockingPeriodicTime(seconds: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setAutomaticLockingPeriod(seconds.toInt(), lockData, null, object : SetAutoLockingPeriodCallback {
                    override fun onSetAutoLockingPeriodSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockRemoteUnlockSwitchState(lockData: String, resolve: (isOn: Boolean) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getRemoteUnlockSwitchState(lockData, null, object : GetRemoteUnlockStateCallback {
                    override fun onGetRemoteUnlockSwitchStateSuccess(enabled: Boolean) {
                        resolve(enabled)
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLockRemoteUnlockSwitchState(isOn: Boolean, lockData: String, resolve: (lockData: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setRemoteUnlockSwitchState(isOn, lockData, null, object : SetRemoteUnlockSwitchCallback {
                    override fun onSetRemoteUnlockSwitchSuccess(lockData: String) {
                        resolve(lockData)
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockConfig(config: Double, lockData: String, resolve: (data: NumberBooleanPair) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getLockConfig(
                    TTLockConfigConverter.RN2Native(config.toInt()),
                    lockData,
                    object : GetLockConfigCallback {
                        override fun onGetLockConfigSuccess(ttLockConfigType: TTLockConfigType, switchOn: Boolean) {
                            LogUtil.d("ttLockConfigType:$switchOn")
                            resolve(NumberBooleanPair(
                                TTLockConfigConverter.native2RN(ttLockConfigType).toDouble(),
                                switchOn
                            ))
                        }

                        override fun onFail(error: LockError) {
                            reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLockConfig(config: Double, isOn: Boolean, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val ttLockConfigType = TTLockConfigConverter.RN2Native(config.toInt())
        if (ttLockConfigType == null) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        LogUtil.d("ttLockConfigType:$isOn")
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setLockConfig(ttLockConfigType, isOn, lockData, object : SetLockConfigCallback {
                    override fun onSetLockConfigSuccess(ttLockConfigType: TTLockConfigType) {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addPassageMode(
        type: Double,
        weekly: DoubleArray?,
        monthly: DoubleArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val passageModeConfig = PassageModeConfig()
                    passageModeConfig.modeType = if (type.toInt() == 0) PassageModeType.Weekly else PassageModeType.Monthly
                    if (passageModeConfig.modeType == PassageModeType.Weekly) {
                        passageModeConfig.repeatWeekOrDays = Utils.doubleArray2IntJson(weekly)
                    } else {
                        passageModeConfig.repeatWeekOrDays = Utils.doubleArray2IntJson(monthly)
                    }
                    passageModeConfig.setStartDate(startDate.toInt())
                    passageModeConfig.setEndDate(endDate.toInt())

                    LogUtil.d("weekdays:" + passageModeConfig.repeatWeekOrDays)

                    TTLockClient.getDefault().setPassageMode(passageModeConfig, lockData, null, object : SetPassageModeCallback {
                    override fun onSetPassageModeSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearAllPassageModes(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearPassageMode(lockData, null, object : ClearPassageModeCallback {
                    override fun onClearPassageModeSuccess() {
                        resolve()
                    }

                    override fun onFail(error: LockError) {
                        reject(TTLockErrorConverter.native2RN(error).toDouble(), error.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockVersionWithLockMac(lockMac: String, resolve: (lockVersion: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getLockVersion(lockMac, object : GetLockVersionCallback {
                    override fun onGetLockVersionSuccess(lockVersion: String) {
                        resolve(lockVersion)
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLockSoundVolume(soundVolumeValue: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    var soundVolume: SoundVolume = SoundVolume.OFF
                when (soundVolumeValue.toInt()) {
                    -1 -> soundVolume = SoundVolume.ON
                    0 -> soundVolume = SoundVolume.OFF
                    1, 2, 3, 4, 5 -> soundVolume = SoundVolume.getInstance(soundVolumeValue.toInt())
                }
                TTLockClient.getDefault().setLockSoundWithSoundVolume(soundVolume, lockData, object : SetLockSoundWithSoundVolumeCallback {
                    override fun onSetLockSoundSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockSoundVolume(lockData: String, resolve: (soundVolumeValue: Double) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
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
                        resolve(soundVolumeValue.toDouble())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setUnlockDirection(direction: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setUnlockDirection(
                    if (direction.toInt() == 1) UnlockDirection.LEFT else UnlockDirection.RIGHT,
                    lockData,
                    object : SetUnlockDirectionCallback {
                        override fun onSetUnlockDirectionSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setUnlockDirectionAutomatic(lockData: String, resolve: (direction: Double) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().autoSetUnlockDirection(lockData, object : AutoSetUnlockDirectionCallback {
                    override fun onSetSuccess(autoUnlockDirection: AutoUnlockDirection) {
                        val direction = when (autoUnlockDirection) {
                            AutoUnlockDirection.LEFT -> 1
                            AutoUnlockDirection.RIGHT -> 2
                            else -> 0
                        }
                        resolve(direction.toDouble())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getUnlockDirection(lockData: String, resolve: (direction: Double) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getUnlockDirection(lockData, object : GetUnlockDirectionCallback {
                    override fun onGetUnlockDirectionSuccess(unlockDirection: UnlockDirection) {
                        resolve(if (unlockDirection.value == 1) 1.0 else 2.0)
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addRemoteKey(
        remoteMac: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().addRemote(remoteMac, validityInfo, lockData, object : AddRemoteCallback {
                    override fun onAddSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun modifyRemoteKey(
        remoteMac: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val validityInfo = ValidityInfo()
        validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
        validityInfo.startDate = startDate.toLong()
        validityInfo.endDate = endDate.toLong()
        validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().modifyRemoteValidityPeriod(
                    remoteMac,
                    validityInfo,
                    lockData,
                    object : ModifyRemoteValidityPeriodCallback {
                        override fun onModifySuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun deleteRemoteKey(remoteMac: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deleteRemote(remoteMac, lockData, object : DeleteRemoteCallback {
                    override fun onDeleteSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockElectricQuantity(lockData: String, resolve: (electricQuantity: Double) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getBatteryLevel(lockData, null, object : GetBatteryLevelCallback {
                    override fun onGetBatteryLevelSuccess(battery: Int) {
                        resolve(battery.toDouble())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearAllRemoteKey(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearRemote(lockData, object : ClearRemoteCallback {
                    override fun onClearSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addDoorSensor(doorSensorMac: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().addDoorSensor(doorSensorMac, lockData, object : AddDoorSensorCallback {
                    override fun onAddSuccess() {
                        LogUtil.d("add door sensor to lock success")
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("add door sensor to lock failed:" + lockError.description)
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearAllDoorSensor(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deleteDoorSensor(lockData, object : DeleteDoorSensorCallback {
                    override fun onDeleteSuccess() {
                        LogUtil.d("clear door sensor success")
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("clear door sensor failed:" + lockError.description)
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setDoorSensorAlertTime(time: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setDoorSensorAlertTime(time.toInt(), lockData, object : SetDoorSensorAlertTimeCallback {
                    override fun onSetDoorSensorAlertTimeSuccess() {
                        LogUtil.d("set door sensor alert time success")
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        LogUtil.d("set door sensor failed:" + lockError.description)
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getAccessoryElectricQuantity(
        accessoryType: Double,
        accessoryMac: String,
        lockData: String,
        resolve: (data: NumberNumberPair) -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val accessoryInfo = AccessoryInfo()
                accessoryInfo.accessoryMac = accessoryMac
                val type = when (accessoryType.toInt()) {
                    1 -> AccessoryType.WIRELESS_KEYPAD
                    2 -> AccessoryType.REMOTE
                    3 -> AccessoryType.DOOR_SENSOR
                    else -> null
                }
                accessoryInfo.accessoryType = type
                TTLockClient.getDefault().getAccessoryBatteryLevel(
                    accessoryInfo,
                    lockData,
                    object : GetAccessoryBatteryLevelCallback {
                        override fun onGetAccessoryBatteryLevelSuccess(accessoryInfo: AccessoryInfo) {
                            resolve(NumberNumberPair(
                                accessoryInfo.accessoryBattery.toDouble(),
                                accessoryInfo.batteryDate.toDouble()
                            ))
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun scanWifi(lockData: String, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().scanWifi(lockData, object : ScanWifiCallback {
                    override fun onScanWifi(wiFis: List<WiFi>, status: Int) {
                        if (wiFis != null) {
                            val wifiList = wiFis.map { wiFi ->
                                val map = AnyMap()
                                map.setString(TTGatewayFieldConstant.WIFI, wiFi.ssid)
                                map.setDouble(TTGatewayFieldConstant.RSSI, wiFi.rssi.toDouble())
                                map
                            }
                            val data = AnyMap()
                            data.setBoolean("isFinished", (status == 1))
                            data.setAny("wifis", wifiList.toTypedArray())
                            sendEvent(TTLockEvent.scanWifi, data)
                        }
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun configWifi(wifiName: String, wifiPassword: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().configWifi(wifiName, wifiPassword, lockData, object : ConfigWifiCallback {
                    override fun onConfigWifiSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun configServer(ip: String, port: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    try {
                    val portNumber = port.toInt()
                    TTLockClient.getDefault().configServer(ip, portNumber, lockData, object : ConfigServerCallback {
                        override fun onConfigServerSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
                }
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getWifiInfo(lockData: String, resolve: (data: StringNumberPair) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getWifiInfo(lockData, object : GetWifiInfoCallback {
                    override fun onGetWiFiInfoSuccess(wifiLockInfo: WifiLockInfo) {
                        resolve(StringNumberPair(
                            wifiLockInfo.wifiMac ?: "",
                            wifiLockInfo.wifiRssi.toDouble()
                        ))
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun configIp(info: WifiLockServerInfo, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val ipSetting = IpSetting()
                    ipSetting.type = info.type.toInt()
                    ipSetting.ipAddress = info.ipAddress
                    ipSetting.subnetMask = info.subnetMask
                    ipSetting.router = info.router
                    ipSetting.preferredDns = info.preferredDns
                    ipSetting.alternateDns = info.alternateDns
                    TTLockClient.getDefault().configIp(
                    ipSetting,
                    lockData,
                    object : com.ttlock.bl.sdk.callback.ConfigIpCallback {
                        override fun onConfigIpSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addFace(
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: (faceNumber: String) -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFace(lockData, validityInfo, object : AddFaceCallback {
                    override fun onEnterAddMode() {
                        val data = AnyMap()
                        data.setDouble("step", 0.0)
                        data.setDouble("status", 0.0)
                        sendEvent(TTLockEvent.addFaceProgrress, data)
                    }

                    override fun onCollectionStatus(faceCollectionStatus: FaceCollectionStatus) {
                        val data = AnyMap()
                        data.setDouble("step", 0.0)
                        data.setDouble("status", faceCollectionStatus.value.toDouble())
                        sendEvent(TTLockEvent.addFaceProgrress, data)
                    }

                    override fun onAddFinished(faceNumber: Long) {
                        resolve(faceNumber.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun addFaceFeatureData(
        faceFeatureData: String,
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: (faceNumber: String) -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().addFaceFeatureData(lockData, faceFeatureData, validityInfo, object : AddFaceCallback {
                    override fun onEnterAddMode() {
                        // Empty
                    }

                    override fun onCollectionStatus(faceCollectionStatus: FaceCollectionStatus) {
                        // Empty
                    }

                    override fun onAddFinished(faceNumber: Long) {
                        resolve(faceNumber.toString())
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun modifyFaceValidityPeriod(
        cycleList: Array<CycleDateParam>?,
        startDate: Double,
        endDate: Double,
        faceNumber: String,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        if (TextUtils.isEmpty(faceNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }

        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    val validityInfo = ValidityInfo()
                validityInfo.modeType = if (cycleList == null || cycleList.isEmpty()) ValidityInfo.TIMED else ValidityInfo.CYCLIC
                validityInfo.startDate = startDate.toLong()
                validityInfo.endDate = endDate.toLong()
                validityInfo.cyclicConfigs = Utils.cycleDateArray2CyclicList(cycleList)

                TTLockClient.getDefault().modifyFaceValidityPeriod(
                    lockData,
                    faceNumber.toLong(),
                    validityInfo,
                    object : ModifyFacePeriodCallback {
                        override fun onModifySuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun deleteFace(faceNumber: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        if (TextUtils.isEmpty(faceNumber)) {
            reject(TTLockErrorConverter.native2RN(LockError.DATA_FORMAT_ERROR).toDouble(), LockError.DATA_FORMAT_ERROR.errorMsg)
            return
        }
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().deleteFace(lockData, faceNumber.toLong(), object : DeleteFaceCallback {
                    override fun onDeleteSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearFace(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearFace(lockData, object : ClearFaceCallback {
                    override fun onClearSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getLockSystem(lockData: String, resolve: (systemInfo: DeviceSystemModal) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getLockSystemInfo(lockData, null, object : GetLockSystemInfoCallback {
                    override fun onGetLockSystemInfoSuccess(deviceInfo: com.ttlock.bl.sdk.entity.DeviceInfo) {
                        resolve(DeviceSystemModal(
                            modelNum = deviceInfo.modelNum ?: "",
                            hardwareRevision = deviceInfo.hardwareRevision ?: "",
                            firmwareRevision = deviceInfo.firmwareRevision ?: "",
                            nbOperator = "",
                            nbNodeId = "",
                            nbCardNumber = "",
                            nbRssi = "",
                            passcodeKeyNumber = "",
                            lockData = deviceInfo.lockData ?: ""
                        ))
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getWifiPowerSavingTime(lockData: String, resolve: (powerSavingData: String) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().getWifiPowerSavingTimes(lockData, object : GetWifiPowerSavingTimesCallback {
                    override fun onGetSuccess(powerSavingData: String) {
                        resolve(powerSavingData)
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun configWifiPowerSavingTime(
        days: DoubleArray?,
        startDate: Double,
        endDate: Double,
        lockData: String,
        resolve: () -> Unit,
        reject: (errorCode: Double, description: String) -> Unit
    ) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().configWifiPowerSavingTimes(
                    Utils.doubleArray2IntList(days),
                    startDate.toInt(),
                    endDate.toInt(),
                    lockData,
                    object : ConfigWifiPowerSavingTimesCallback {
                        override fun onConfigSuccess() {
                            resolve()
                        }

                        override fun onFail(lockError: LockError) {
                            reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                        }
                    }
                )
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun clearWifiPowerSavingTime(lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().clearWifiPowerSavingTimes(lockData, object : ClearWifiPowerSavingTimesCallback {
                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }

                    override fun onClearSuccess() {
                        resolve()
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun activateLiftFloors(floors: String, lockData: String, resolve: (data: NumberNumberNumberTriple) -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
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
                        resolve(NumberNumberNumberTriple(
                            activateLiftFloorsResult.deviceTime.toDouble(),
                            activateLiftFloorsResult.battery.toDouble(),
                            activateLiftFloorsResult.uniqueid.toDouble()
                        ))
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLiftControlEnableFloors(floors: String, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    TTLockClient.getDefault().setLiftControlableFloors(floors, lockData, object : SetLiftControlableFloorsCallback {
                    override fun onSetLiftControlableFloorsSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                      reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun setLiftWorkMode(workMode: Double, lockData: String, resolve: () -> Unit, reject: (errorCode: Double, description: String) -> Unit) {
        val activity = context.getCurrentActivity()
        if (activity == null) {
            LogUtil.d("currentActivity is null")
            return
        }
        PermissionUtils.doWithConnectPermission(activity, object : OnSuccessListener {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    // enum LiftWorkMode {
                //   ACTIVATE_ALL_FLOORS = 0,
                //   ACTIVATE_SPECIFIC_FLOORS = 1
                // }
                var liftWorkMode: TTLiftWorkMode = TTLiftWorkMode.ActivateAllFloors
                if (workMode.toInt() == 1) {
                    liftWorkMode = TTLiftWorkMode.ActivateSpecificFloors
                }
                TTLockClient.getDefault().setLiftWorkMode(liftWorkMode, lockData, object : SetLiftWorkModeCallback {
                    override fun onSetLiftWorkModeSuccess() {
                        resolve()
                    }

                    override fun onFail(lockError: LockError) {
                        reject(TTLockErrorConverter.native2RN(lockError).toDouble(), lockError.errorMsg)
                    }
                })
            } else {
                reject(TTLockErrorConverter.native2RN(LockError.LOCK_NO_PERMISSION).toDouble(), LockError.LOCK_NO_PERMISSION.errorMsg)
            }

            }
        })
    }

    override fun getBluetoothState(resolve: (state: Double) -> Unit) {
        val enable = TTLockClient.getDefault().isBLEEnabled(context.getCurrentActivity())
        //4-on, 5-off
        resolve(if (enable) 4.0 else 5.0)
    }

    override fun supportFunction(lockFunction: Double, lockData: String, resolve: (isSupport: Boolean) -> Unit) {
        val support = FeatureValueUtil.isSupportFeature(lockData, lockFunction.toInt())
        resolve(support)
    }

    // Event Listeners
    override fun addListener(eventName: String, listener: (String, AnyMap?) -> Unit) {
        if (!listeners.containsKey(eventName)) {
            listeners[eventName] = mutableListOf()
        }
        listeners[eventName]?.add(listener)
    }

    override fun removeListener(eventName: String, listener: (String, AnyMap?) -> Unit) {
        listeners[eventName]?.remove(listener)
    }

    fun sendEvent(eventName: String, data: AnyMap?) {
        listeners[eventName]?.forEach { listener ->
            listener(eventName, data)
        }
    }
}
