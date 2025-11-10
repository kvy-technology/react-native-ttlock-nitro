import TTLock
import NitroModules

// MARK: - Event Names
let EVENT_SCAN_LOCK = "EventScanLock"
let EVENT_ADD_CARD_PROGRESS = "EventAddCardProgrress"
let EVENT_ADD_FINGERPRINT_PROGRESS = "EventAddFingerprintProgrress"
let EVENT_ADD_FACE_PROGRESS = "EventAddFaceProgrress"
let EVENT_SCAN_GATEWAY = "EventScanGateway"
let EVENT_SCAN_WIFI = "EventScanWifi"
let EVENT_SCAN_REMOTE_KEY = "EventScanRemoteKey"
let EVENT_SCAN_DOOR_SENSOR = "EventScanDoorSensor"
let EVENT_SCAN_WIRELESS_KEYPAD = "EventWirelessKeypad"
let EVENT_SCAN_LOCK_WIFI = "EventScanLockWifi"

// MARK: - RSSI Settings
let RSSI_SETTING_MAX: Int = -65    // Corresponding unlocking distance: 0.5m
let RSSI_SETTING_MIN: Int = -140
let RSSI_SETTING_1m: Int = -85     // Corresponding unlocking distance: 1m
let RSSI_SETTING_2m: Int = -150    // Corresponding unlocking distance: 2m
let RSSI_SETTING_3m: Int = -180    // Corresponding unlocking distance: 3m
let RSSI_SETTING_4m: Int = -210    // Corresponding unlocking distance: 4m
let RSSI_SETTING_5m: Int = -240    // Corresponding unlocking distance: 5m

// MARK: - TTControlAction Constants (matching SDK enum names)
struct TTControlActionValue {
    static let TTControlActionRemoteStop: Int = 0x00
    static let TTControlActionUnlock: Int = 0x01
    static let TTControlActionLock: Int = 0x02
    static let TTControlActionlPause: Int = 0x04
    static let TTControlActionHold: Int = 0x08
}

// MARK: - TTBluetoothState Constants (matching SDK enum names)
struct TTBluetoothStateValue {
    static let TTBluetoothStateUnknown: Int = 0
    static let TTBluetoothStateResetting: Int = 1
    static let TTBluetoothStateUnsupported: Int = 2
    static let TTBluetoothStateUnauthorized: Int = 3
    static let TTBluetoothStatePoweredOff: Int = 4
    static let TTBluetoothStatePoweredOn: Int = 5
}

// MARK: - TTLockType Constants (matching SDK enum names)
struct TTLockTypeValue {
    static let TTLockTypeV2: Int = 1
    static let TTLockTypeV2Scene1: Int = 2
    static let TTLockTypeV2Scene2: Int = 3
    static let TTLockTypeV2ParkingLock: Int = 4
    static let TTLockTypeV3: Int = 5
    static let TTLockTypeGateLock: Int = 6
    static let TTLockTypeSafeLock: Int = 7
    static let TTLockTypeBicycleLock: Int = 8
    static let TTLockTypeParkingLock: Int = 9
    static let TTLockTypePadLock: Int = 10
    static let TTLockTypeCylinderLock: Int = 11
    static let TTLockTypeRemoteControl: Int = 12
    static let TTLockTypeHotelLock: Int = 13
    static let TTLockTypeLift: Int = 14
    static let TTLockTypePowerSaver: Int = 15
    static let TTLockTypeWifiPowerSaver: Int = 16
}

// MARK: - TTPasscodeType Constants (matching SDK enum names)
struct TTPasscodeTypeValue {
    static let TTPasscodeTypeOnce: Int = 1
    static let TTPasscodeTypePermanent: Int = 2
    static let TTPasscodeTypePeriod: Int = 3
    static let TTPasscodeTypeCycle: Int = 4
}

// MARK: - TTOprationType Constants (matching SDK enum names)
struct TTOprationTypeValue {
    static let TTOprationTypeClear: Int = 1
    static let TTOprationTypeAdd: Int = 2
    static let TTOprationTypeDelete: Int = 3
    static let TTOprationTypeModify: Int = 4
    static let TTOprationTypeQuery: Int = 5
    static let TTOprationTypeRecover: Int = 6
    static let TTOprationTypeAddFingerprintData: Int = 7
}

// MARK: - TTLockSwitchState Constants (matching SDK enum names)
struct TTLockSwitchStateValue {
    static let TTLockSwitchStateLock: Int = 0
    static let TTLockSwitchStateUnlock: Int = 1
    static let TTLockSwitchStateUnknown: Int = 2
    static let TTLockSwitchStateDeadLock: Int = 3
}

// MARK: - TTDoorSensorState Constants (matching SDK enum names)
struct TTDoorSensorStateValue {
    static let TTDoorSensorStateOpen: Int = 0
    static let TTDoorSensorStateClose: Int = 1
    static let TTDoorSensorStateUnknown: Int = 2
}

// MARK: - TTAddICState Constants (matching SDK enum names)
struct TTAddICStateValue {
    static let TTAddICStateHadAdd: Int = 1
    static let TTAddICStateCanAdd: Int = 2
}

// MARK: - TTAddFingerprintState Constants (matching SDK enum names)
struct TTAddFingerprintStateValue {
    static let TTAddFingerprintCollectSuccess: Int = 1
    static let TTAddFingerprintCanCollect: Int = 2
    static let TTAddFingerprintCanCollectAgain: Int = 3
}

// MARK: - TTOperateLogType Constants (matching SDK enum names)
struct TTOperateLogTypeValue {
    static let TTOperateLogTypeLatest: Int = 1
    static let TTOperateLogTypeAll: Int = 2
}

// MARK: - TTPassageModeType Constants (matching SDK enum names)
struct TTPassageModeTypeValue {
    static let TTPassageModeTypeWeekly: Int = 1
    static let TTPassageModeTypeMonthly: Int = 2
}

// MARK: - TTDeviceInfoType Constants (matching SDK enum names)
struct TTDeviceInfoTypeValue {
    static let TTDeviceInfoTypeOfProductionModel: Int = 1
    static let TTDeviceInfoTypeOfHardwareVersion: Int = 2
    static let TTDeviceInfoTypeOfFirmwareVersion: Int = 3
    static let TTDeviceInfoTypeOfProductionDate: Int = 4
    static let TTDeviceInfoTypeOfProductionMac: Int = 5
    static let TTDeviceInfoTypeOfProductionClock: Int = 6
    static let TTDeviceInfoTypeOfNBOperator: Int = 7
    static let TTDeviceInfoTypeOfNbNodeId: Int = 8
    static let TTDeviceInfoTypeOfNbCardNumber: Int = 9
    static let TTDeviceInfoTypeOfNbRssi: Int = 10
    static let TTDeviceInfoTypeOfPasscodeKeyNumber: Int = 12
    static let TTDeviceInfoTypeOfCameraSerialNumber: Int = 13
    static let TTDeviceInfoTypeOfUuid: Int = 14
    static let TTDeviceInfoTypeOfAuthCode: Int = 15
}

// MARK: - TTLockFeatureValue Constants (matching SDK enum names)
struct TTLockFeatureValueValue {
    static let TTLockFeatureValuePasscode: Int = 0
    static let TTLockFeatureValueICCard: Int = 1
    static let TTLockFeatureValueFingerprint: Int = 2
    static let TTLockFeatureValueWristband: Int = 3
    static let TTLockFeatureValueAutoLock: Int = 4
    static let TTLockFeatureValueDeletePasscode: Int = 5
    static let TTLockFeatureValueManagePasscode: Int = 7
    static let TTLockFeatureValueLocking: Int = 8
    static let TTLockFeatureValuePasscodeVisible: Int = 9
    static let TTLockFeatureValueGatewayUnlock: Int = 10
    static let TTLockFeatureValueLockFreeze: Int = 11
    static let TTLockFeatureValueCyclePassword: Int = 12
    static let TTLockFeatureValueRemoteUnlockSwicth: Int = 14
    static let TTLockFeatureValueAudioSwitch: Int = 15
    static let TTLockFeatureValueNBIoT: Int = 16
    static let TTLockFeatureValueGetAdminPasscode: Int = 18
    static let TTLockFeatureValueHotelCard: Int = 19
    static let TTLockFeatureValueNoClock: Int = 20
    static let TTLockFeatureValueNoBroadcastInNormal: Int = 21
    static let TTLockFeatureValuePassageMode: Int = 22
    static let TTLockFeatureValueTurnOffAutoLock: Int = 23
    static let TTLockFeatureValueWirelessKeypad: Int = 24
    static let TTLockFeatureValueLight: Int = 25
    static let TTLockFeatureValueHotelCardBlacklist: Int = 26
    static let TTLockFeatureValueIdentityCard: Int = 27
    static let TTLockFeatureValueTamperAlert: Int = 28
    static let TTLockFeatureValueResetButton: Int = 29
    static let TTLockFeatureValuePrivacyLock: Int = 30
    static let TTLockFeatureValueDeadLock: Int = 32
    static let TTLockFeatureValueCyclicCardOrFingerprint: Int = 34
    static let TTLockFeatureValueUnlockDirection: Int = 36
    static let TTLockFeatureValueFingerVein: Int = 37
    static let TTLockFeatureValueBle5G: Int = 38
    static let TTLockFeatureValueNBAwake: Int = 39
    static let TTLockFeatureValueRecoverCyclePasscode: Int = 40
    static let TTLockFeatureValueWirelessKeyFob: Int = 41
    static let TTLockFeatureValueGetAccessoryElectricQuantity: Int = 42
    static let TTLockFeatureValueSoundVolume: Int = 43
    static let TTLockFeatureValueQRCode: Int = 44
    static let TTLockFeatureValueSensorState: Int = 45
    static let TTLockFeatureValuePassageModeAutoUnlock: Int = 46
    static let TTLockFeatureValueDoorSensor: Int = 50
    static let TTLockFeatureValueDoorSensorAlert: Int = 51
    static let TTLockFeatureValueSensitivity: Int = 52
    static let TTLockFeatureValueFace: Int = 53
    static let TTLockFeatureValueCpuCard: Int = 55
    static let TTLockFeatureValueWifiLock: Int = 56
    static let TTLockFeatureValueWifiLockStaticIP: Int = 58
    static let TTLockFeatureValuePasscodeKeyNumber: Int = 60
    static let TTLockFeatureValueStandAloneActivation: Int = 62
    static let TTLockFeatureValueDoubleAuth: Int = 63
    static let TTLockFeatureValueAuthorizedUnlock: Int = 64
    static let TTLockFeatureValueGatewayAuthorizedUnlock: Int = 65
    static let TTLockFeatureValueNoEkeyUnlock: Int = 66
    static let TTLockFeatureValueZhiAnPhotoFace: Int = 69
    static let TTLockFeatureValuePalmVein: Int = 70
    static let TTLockFeatureValueWifiArea: Int = 71
    static let TTLockFeatureValueXiaoCaoCamera: Int = 75
    static let TTLockFeatureValueResetLockByCode: Int = 76
    static let TTLockFeatureValueAutoSetAngle: Int = 78
    static let TTLockFeatureValueManualSetAngle: Int = 79
    static let TTLockFeatureValueControlLatchBolt: Int = 80
    static let TTLockFeatureValueAutoSetUnlockDirection: Int = 81
    static let TTLockFeatureValueWifiPowerSavingTime: Int = 83
    static let TTLockFeatureValueMultifunctionalKeypad: Int = 84
    static let TTLockFeatureValuePublicMode: Int = 86
    static let TTLockFeatureValueLowBatteryAutoUnlock: Int = 87
    static let TTLockFeatureValueMotorDriveTime: Int = 88
    static let TTLockFeatureValueModifyFeatureValue: Int = 89
    static let TTLockFeatureValueModifyLockNamePrefix: Int = 90
    static let TTLockFeatureValueAuthCode: Int = 92
    static let TTLockFeatureValuePowerSaverSupportWifi: Int = 96
    static let TTLockFeatureValueWorkingMode: Int = 98
    static let TTLockFeatureValueWorkingTime: Int = 107
}

// MARK: - TTLockConfigType Constants (matching SDK enum names)
struct TTLockConfigTypeValue {
    static let TTLockSound: Int = 1
    static let TTPasscodeVisible: Int = 2
    static let TTLockFreeze: Int = 3
    static let TTTamperAlert: Int = 4
    static let TTResetButton: Int = 5
    static let TTPrivacyLock: Int = 6
    static let TTPassageModeAutoUnlock: Int = 7
    static let TTWifiPowerSavingMode: Int = 8
    static let TTDoubleAuth: Int = 9
    static let TTPublicMode: Int = 10
    static let TTLowBatteryAutoUnlock: Int = 11
}

// MARK: - TTLiftWorkMode Constants (matching SDK enum names)
struct TTLiftWorkModeValue {
    static let TTLiftWorkModeActivateAllFloors: Int = 0
    static let TTLiftWorkModeModeActivateSpecificFloors: Int = 1
}

// MARK: - TTPowerSaverWorkMode Constants (matching SDK enum names)
struct TTPowerSaverWorkModeValue {
    static let TTPowerSaverWorkModeAllCards: Int = 0
    static let TTPowerSaverWorkModeHotelCard: Int = 1
    static let TTPowerSaverWorkModeRoomCard: Int = 2
}

// MARK: - TTNBAwakeMode Constants (matching SDK enum names)
struct TTNBAwakeModeValue {
    static let TTNBAwakeModeKeypad: Int = 0
    static let TTNBAwakeModeCard: Int = 1
    static let TTNBAwakeModeFingerprint: Int = 2
}

// MARK: - TTNBAwakeTimeType Constants (matching SDK enum names)
struct TTNBAwakeTimeTypeValue {
    static let TTNBAwakeTimeTypePoint: Int = 1
    static let TTNBAwakeTimeTypeInterval: Int = 2
}

// MARK: - TTUnlockDirection Constants (matching SDK enum names)
struct TTUnlockDirectionValue {
    static let TTUnlockDirectionLeft: Int = 1
    static let TTUnlockDirectionRight: Int = 2
}

// MARK: - TTAccessoryType Constants (matching SDK enum names)
struct TTAccessoryTypeValue {
    static let TTAccessoryTypeWirelessKeypad: Int = 1
    static let TTAccessoryTypeWirelessKeyFob: Int = 2
    static let TTAccessoryTypeDoorSensor: Int = 3
}

// MARK: - TTSoundVolume Constants (matching SDK enum names)
struct TTSoundVolumeValue {
    static let TTSoundVolumeOn: Int = -1
    static let TTSoundVolumeOff: Int = 0
    static let TTSoundVolumeFirstLevel: Int = 1
    static let TTSoundVolumeSecondLevel: Int = 2
    static let TTSoundVolumeThirdLevel: Int = 3
    static let TTSoundVolumeFourthLevel: Int = 4
    static let TTSoundVolumeFifthLevel: Int = 5
}

// MARK: - TTAddFaceState Constants (matching SDK enum names)
struct TTAddFaceStateValue {
    static let TTAddFaceStateSuccess: Int = 1
    static let TTAddFaceStateCanStartAdd: Int = 2
    static let TTAddFaceStateError: Int = 3
}

// MARK: - TTAddPalmVeinState Constants (matching SDK enum names)
struct TTAddPalmVeinStateValue {
    static let TTAddPalmVeinStateSuccess: Int = 1
    static let TTAddPalmVeinStateCanStartAdd: Int = 2
    static let TTAddPalmVeinStateError: Int = 3
}

// MARK: - TTSensitivityValue Constants (matching SDK enum names)
struct TTSensitivityValueValue {
    static let TTSensitivityValueOff: Int = 0
    static let TTSensitivityValueLow: Int = 1
    static let TTSensitivityValueMedium: Int = 2
    static let TTSensitivityValueHigh: Int = 3
}

// MARK: - TTFaceErrorCode Constants (matching SDK enum names)
struct TTFaceErrorCodeValue {
    static let TTFaceErrorCodeNoError: Int = 0
    static let TTFaceErrorCodeNoFaceDetected: Int = 1
    static let TTFaceErrorCodeTooCloseToTheTop: Int = 2
    static let TTFaceErrorCodeTooCloseToTheBottom: Int = 3
    static let TTFaceErrorCodeTooCloseToTheLeft: Int = 4
    static let TTFaceErrorCodeTooCloseToTheRight: Int = 5
    static let TTFaceErrorCodeTooFarAway: Int = 6
    static let TTFaceErrorCodeTooClose: Int = 7
    static let TTFaceErrorCodeEyebrowsCovered: Int = 8
    static let TTFaceErrorCodeEyesCovered: Int = 9
    static let TTFaceErrorCodeFaceCovered: Int = 10
    static let TTFaceErrorCodeWrongFaceDirection: Int = 11
    static let TTFaceErrorCodeEyeOpeningDetected: Int = 12
    static let TTFaceErrorCodeEyesClosedStatus: Int = 13
    static let TTFaceErrorCodeFailedToDetectEye: Int = 14
    static let TTFaceErrorCodeNeedTurnHeadToLeft: Int = 15
    static let TTFaceErrorCodeNeedTurnHeadToRight: Int = 16
    static let TTFaceErrorCodeNeedRaiseHead: Int = 17
    static let TTFaceErrorCodeNeedLowerHead: Int = 18
    static let TTFaceErrorCodeNeedTiltHeadToLeft: Int = 19
    static let TTFaceErrorCodeNeedTiltHeadToRight: Int = 20
}

// MARK: - TTPalmVeinErrorCode Constants (matching SDK enum names)
struct TTPalmVeinErrorCodeValue {
    static let TTPalmVeinErrorNoPalm: Int = 1
    static let TTPalmVeinErrorRectConfidenceLow: Int = 2
    static let TTPalmVeinErrorLandmarkConfidenceLow: Int = 3
    static let TTPalmVeinErrorAngleRoll: Int = 4
    static let TTPalmVeinErrorAngleLean: Int = 5
    static let TTPalmVeinErrorPlamBlock: Int = 6
    static let TTPalmVeinErrorPlamBlur: Int = 7
    static let TTPalmVeinErrorPlamBack: Int = 8
}

// MARK: - TTAutoUnlockDirection Constants (matching SDK enum names)
struct TTAutoUnlockDirectionValue {
    static let TTAutoUnlockDirectionFail: Int = 0
    static let TTAutoUnlockDirectionLeft: Int = 1
    static let TTAutoUnlockDirectionRight: Int = 2
}

// MARK: - TTSupportFeature Constants (matching SDK enum names)
struct TTSupportFeatureValue {
    static let TTSupportFeatureFingerprint: Int = 0
    static let TTSupportFeatureCard: Int = 1
    static let TTSupportFeaturePasscode: Int = 2
}

// MARK: - TTGatewayType Constants (matching SDK enum names)
struct TTGatewayTypeValue {
    static let TTGateWayTypeG2: Int = 2
    static let TTGateWayTypeG3: Int = 3
    static let TTGateWayTypeG4: Int = 4
    static let TTGateWayTypeG5: Int = 5
    static let TTGateWayTypeG6: Int = 6
}

// MARK: - TTGatewayConnectStatus Constants (matching SDK enum names)
struct TTGatewayConnectStatusValue {
    static let TTGatewayConnectTimeout: Int = 0
    static let TTGatewayConnectSuccess: Int = 1
    static let TTGatewayConnectFail: Int = 2
}

// MARK: - TTGatewayStatus Constants (matching SDK enum names)
struct TTGatewayStatusValue {
    static let TTGatewaySuccess: Int = 0
    static let TTGatewayFail: Int = 1
    static let TTGatewayWrongSSID: Int = 3
    static let TTGatewayWrongWifiPassword: Int = 4
    static let TTGatewayInvalidCommand: Int = 6
    static let TTGatewayTimeout: Int = 7
    static let TTGatewayNoSIM: Int = 8
    static let TTGatewayNoPlugCable: Int = 9
    static let TTGatewayWrongCRC: Int = -1
    static let TTGatewayWrongAeskey: Int = -2
    static let TTGatewayNotConnect: Int = -3
    static let TTGatewayDisconnect: Int = -4
    static let TTGatewayFailConfigRouter: Int = -5
    static let TTGatewayFailConfigServer: Int = -6
    static let TTGatewayFailConfigAccount: Int = -7
    static let TTGatewayFailConfigIP: Int = -8
    static let TTGatewayFailInvaildIP: Int = -9
}

// MARK: - TTKeyFobStatus Constants (matching SDK enum names)
struct TTKeyFobStatusValue {
    static let TTKeyFobSuccess: Int = 0
    static let TTKeyFobFail: Int = 1
    static let TTKeyFobWrongCRC: Int = -1
    static let TTKeyFobConnectTimeout: Int = -2
}

// MARK: - TTKeyFobFeatureValue Constants (matching SDK enum names)
struct TTKeyFobFeatureValueValue {
    static let TTKeyFobFeatureValueOnMicro: Int = 0
    static let TTKeyFobFeatureValueAuthCode: Int = 1
}

// MARK: - TTKeypadStatus Constants (matching SDK enum names)
struct TTKeypadStatusValue {
    static let TTKeypadSuccess: Int = 0
    static let TTKeypadFail: Int = 1
    static let TTKeypadWrongCRC: Int = -1
    static let TTKeypadConnectTimeout: Int = -2
    static let TTKeypadWrongFactorydDate: Int = -3
    static let TTKeypadDuplicateFingerprint: Int = -4
    static let TTKeypadLackOfStorageSpace: Int = 0x16
}

// MARK: - TTDoorSensorError Constants (matching SDK enum names)
struct TTDoorSensorErrorValue {
    static let TTDoorSensorErrorBluetoothPowerOff: Int = 1
    static let TTDoorSensorErrorConnectTimeout: Int = 2
    static let TTDoorSensorErrorFail: Int = 3
    static let TTDoorSensorErrorWrongCRC: Int = 4
}

// MARK: - TTError Constants (matching SDK enum names)
struct TTErrorValue {
    static let TTErrorHadReseted: Int = 0x00
    static let TTErrorCRCError: Int = 0x01
    static let TTErrorNoPermisstion: Int = 0x02
    static let TTErrorWrongAdminCode: Int = 0x03
    static let TTErrorLackOfStorageSpace: Int = 0x04
    static let TTErrorInSettingMode: Int = 0x05
    static let TTErrorNoAdmin: Int = 0x06
    static let TTErrorNotInSettingMode: Int = 0x07
    static let TTErrorWrongDynamicCode: Int = 0x08
    static let TTErrorIsNoPower: Int = 0x0a
    static let TTErrorResetPasscode: Int = 0x0b
    static let TTErrorUpdatePasscodeIndex: Int = 0x0c
    static let TTErrorInvalidLockFlagPos: Int = 0x0d
    static let TTErrorEkeyExpired: Int = 0x0e
    static let TTErrorPasscodeLengthInvalid: Int = 0x0f
    static let TTErrorSamePasscodes: Int = 0x10
    static let TTErrorEkeyInactive: Int = 0x11
    static let TTErrorAesKey: Int = 0x12
    static let TTErrorFail: Int = 0x13
    static let TTErrorPasscodeExist: Int = 0x14
    static let TTErrorPasscodeNotExist: Int = 0x15
    static let TTErrorLackOfStorageSpaceWhenAddingPasscodes: Int = 0x16
    static let TTErrorInvalidParaLength: Int = 0x17
    static let TTErrorCardNotExist: Int = 0x18
    static let TTErrorFingerprintDuplication: Int = 0x19
    static let TTErrorInvalidParam: Int = 0x20
    static let TTErrorFingerprintNotExist: Int = 0x1A
    static let TTErrorInvalidCommand: Int = 0x1B
    static let TTErrorInFreezeMode: Int = 0x1C
    static let TTErrorInvalidClientPara: Int = 0x1D
    static let TTErrorLockIsLocked: Int = 0x1E
    static let TTErrorRecordNotExist: Int = 0x1F
    static let TTErrorWrongSSID: Int = 0x25
    static let TTErrorWrongWifiPassword: Int = 0x26
    static let TTErrorBluetoothPoweredOff: Int = 0x61
    static let TTErrorConnectionTimeout: Int = 0x62
    static let TTErrorDisconnection: Int = 0x63
    static let TTErrorLockIsBusy: Int = 0x64
    static let TTErrorWrongLockData: Int = 0x65
    static let TTErrorInvalidParameter: Int = 0x66
}

enum Device {
    case LOCK
    case GATEWAY
    case DOOR_SENSOR
    case REMOTE_KEY_PAD
    case REMOTE_KEY
}

class TtlockNitro: HybridTtlockNitroSpec {
    var listeners: [String: [(String, AnyMap?) -> Void]] = [:]

    override init() {
        super.init()
        TTLock.setupBluetooth { state in
            // Bluetooth state callback
        }
    }

    // Helper to send events
    private func sendEvent(_ eventName: String, body: Any?) {
        // Notify registered listeners
        if let eventListeners = listeners[eventName] {
            let data = body as? AnyMap
            for listener in eventListeners {
                listener(eventName, data)
            }
        }
    }

    // Helper to convert model to dictionary
    private func dictionaryFromModel(_ model: Any) -> [String: Any] {
        var result: [String: Any] = [:]
        let mirror = Mirror(reflecting: model)
        for child in mirror.children {
            if let label = child.label {
                result[label] = child.value
            }
        }
        return result
    }

    // Helper: response success
    private func responseSuccess(_ data: Any?, resolve: @escaping (Any?) -> Void) {
        resolve(data)
    }

    // Helper: response fail for TTError
    private func responseFail(_ device: Device, code: TTError, errorMessage: String?, reject: @escaping (Double, String) -> Void) {
        let errorCode = Double(getTTLockErrorCode(code))
        reject(errorCode, errorMessage ?? "")
    }

    // Helper: response fail for Gateway
    private func responseFail(_ device: Device, code: TTGatewayStatus, errorMessage: String?, reject: @escaping (Double, String) -> Void) {
        let errorCode = Double(getTTGatewayErrorCode(code))
        reject(errorCode, errorMessage ?? "")
    }

    // Helper: response fail for KeyFob
    private func responseFail(_ device: Device, code: TTKeyFobStatus, errorMessage: String?, reject: @escaping (Double, String) -> Void) {
        let errorCode = Double(getTTRemoteKeyErrorCode(code))
        reject(errorCode, errorMessage ?? "")
    }

    // Helper: response fail for Keypad
    private func responseFail(_ device: Device, code: TTKeypadStatus, errorMessage: String?, reject: @escaping (Double, String) -> Void) {
        let errorCode = Double(getTTRemoteKeypadErrorCode(code))
        reject(errorCode, errorMessage ?? "")
    }

    // Helper: response fail for DoorSensor
    private func responseFail(_ device: Device, code: TTDoorSensorError, errorMessage: String?, reject: @escaping (Double, String) -> Void) {
        let errorCode = Double(getTTDoorSensorErrorCode(code))
        reject(errorCode, errorMessage ?? "")
    }

    // MARK: - Error Code Converters

    private func getTTGatewayErrorCode(_ status: TTGatewayStatus) -> Int {
        let codeMapping: [Int: Int] = [
            TTGatewayStatusValue.TTGatewayFail: 0,
            TTGatewayStatusValue.TTGatewayWrongSSID: 1,
            TTGatewayStatusValue.TTGatewayWrongWifiPassword: 2,
            TTGatewayStatusValue.TTGatewayWrongCRC: 3,
            TTGatewayStatusValue.TTGatewayWrongAeskey: 4,
            TTGatewayStatusValue.TTGatewayNotConnect: 5,
            TTGatewayStatusValue.TTGatewayDisconnect: 6,
            TTGatewayStatusValue.TTGatewayFailConfigRouter: 7,
            TTGatewayStatusValue.TTGatewayFailConfigServer: 8,
            TTGatewayStatusValue.TTGatewayFailConfigAccount: 9,
            TTGatewayStatusValue.TTGatewayNoSIM: 10,
            TTGatewayStatusValue.TTGatewayInvalidCommand: 11,
            TTGatewayStatusValue.TTGatewayFailConfigIP: 12,
            TTGatewayStatusValue.TTGatewayFailInvaildIP: 13
        ]

        return codeMapping[Int(status.rawValue)] ?? 0
    }

    private func getTTRemoteKeyErrorCode(_ status: TTKeyFobStatus) -> Int {
        let codeMapping: [Int: Int] = [
            TTKeyFobStatusValue.TTKeyFobFail: 0,
            TTKeyFobStatusValue.TTKeyFobWrongCRC: 1,
            TTKeyFobStatusValue.TTKeyFobConnectTimeout: 2
        ]

        return codeMapping[Int(status.rawValue)] ?? 0
    }

    private func getTTRemoteKeypadErrorCode(_ status: TTKeypadStatus) -> Int {
        let codeMapping: [Int: Int] = [
            TTKeypadStatusValue.TTKeypadFail: 0,
            TTKeypadStatusValue.TTKeypadWrongCRC: 1,
            TTKeypadStatusValue.TTKeypadConnectTimeout: 2,
            TTKeypadStatusValue.TTKeypadWrongFactorydDate: 3
        ]

        return codeMapping[Int(status.rawValue)] ?? 0
    }

    private func getTTDoorSensorErrorCode(_ status: TTDoorSensorError) -> Int {
        let codeMapping: [Int: Int] = [
            TTDoorSensorErrorValue.TTDoorSensorErrorBluetoothPowerOff: 0,
            TTDoorSensorErrorValue.TTDoorSensorErrorConnectTimeout: 1,
            TTDoorSensorErrorValue.TTDoorSensorErrorFail: 2,
            TTDoorSensorErrorValue.TTDoorSensorErrorWrongCRC: 3
        ]

        return codeMapping[Int(status.rawValue)] ?? 2
    }

    private func getTTLockErrorCode(_ code: TTError) -> Int {
        let codeMapping: [Int: Int] = [
            TTErrorValue.TTErrorHadReseted: 0,
            TTErrorValue.TTErrorCRCError: 1,
            TTErrorValue.TTErrorNoPermisstion: 2,
            TTErrorValue.TTErrorWrongAdminCode: 3,
            TTErrorValue.TTErrorLackOfStorageSpace: 4,
            TTErrorValue.TTErrorInSettingMode: 5,
            TTErrorValue.TTErrorNoAdmin: 6,
            TTErrorValue.TTErrorNotInSettingMode: 7,
            TTErrorValue.TTErrorWrongDynamicCode: 8,
            TTErrorValue.TTErrorIsNoPower: 9,
            TTErrorValue.TTErrorResetPasscode: 10,
            TTErrorValue.TTErrorUpdatePasscodeIndex: 11,
            TTErrorValue.TTErrorInvalidLockFlagPos: 12,
            TTErrorValue.TTErrorEkeyExpired: 13,
            TTErrorValue.TTErrorPasscodeLengthInvalid: 14,
            TTErrorValue.TTErrorSamePasscodes: 15,
            TTErrorValue.TTErrorEkeyInactive: 16,
            TTErrorValue.TTErrorAesKey: 17,
            TTErrorValue.TTErrorFail: 18,
            TTErrorValue.TTErrorPasscodeExist: 19,
            TTErrorValue.TTErrorPasscodeNotExist: 20,
            TTErrorValue.TTErrorLackOfStorageSpaceWhenAddingPasscodes: 21,
            TTErrorValue.TTErrorInvalidParaLength: 22,
            TTErrorValue.TTErrorCardNotExist: 23,
            TTErrorValue.TTErrorFingerprintDuplication: 24,
            TTErrorValue.TTErrorFingerprintNotExist: 25,
            TTErrorValue.TTErrorInvalidCommand: 26,
            TTErrorValue.TTErrorInFreezeMode: 27,
            TTErrorValue.TTErrorInvalidClientPara: 28,
            TTErrorValue.TTErrorLockIsLocked: 29,
            TTErrorValue.TTErrorRecordNotExist: 30,
            TTErrorValue.TTErrorWrongSSID: 31,
            TTErrorValue.TTErrorWrongWifiPassword: 32,
            TTErrorValue.TTErrorBluetoothPoweredOff: 33,
            TTErrorValue.TTErrorConnectionTimeout: 34,
            TTErrorValue.TTErrorDisconnection: 35,
            TTErrorValue.TTErrorLockIsBusy: 36,
            TTErrorValue.TTErrorWrongLockData: 37,
            TTErrorValue.TTErrorInvalidParameter: 38
        ]

        return codeMapping[Int(code.rawValue)] ?? 18
    }

    // MARK: - Utility

    public func getBluetoothState(resolve: @escaping (Double) -> Void) {
        let bluetoothState = TTLock.bluetoothState
        resolve(Double(bluetoothState.rawValue))
    }

    public func supportFunction(lockFunction: Double, lockData: String, resolve: @escaping (Bool) -> Void) {
        let feature = TTLockFeatureValue(rawValue: Int(lockFunction)) ?? TTLockFeatureValue(rawValue: 0)!
        let isSupport = TTUtil.lockFeatureValue(lockData, suportFunction: feature)
        resolve(isSupport)
    }

    // MARK: - Lock Operations

    public func startScan() {
        TTLock.startScan { scanModel in
            let model = scanModel!
            var data: [String: Any] = [:]
            data["lockName"] = model.lockName ?? ""
            data["lockMac"] = model.lockMac ?? ""
            data["isInited"] = model.isInited
            data["isKeyboardActivated"] = model.isAllowUnlock
            data["electricQuantity"] = model.electricQuantity
            data["lockVersion"] = model.lockVersion ?? ""
            data["lockSwitchState"] = model.lockSwitchState.rawValue
            data["rssi"] = model.rssi
            data["oneMeterRssi"] = model.oneMeterRSSI
            self.sendEvent(EVENT_SCAN_LOCK, body: data)
        }
    }

    public func stopScan() {
        TTLock.stopScan()
    }

    public func initLock(params: InitLockParam, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        var dict: [String: Any] = [:]
        if let lockMac = params.lockMac {
            dict["lockMac"] = lockMac
        }
        if let clientPara = params.clientPara {
            dict["clientPara"] = clientPara
        }
        TTLock.initLock(withDict: dict, success: { lockData in
            resolve(lockData ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockVersionWithLockMac(lockMac: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getLockVersion(withLockMac: lockMac, success: { lockVersion in
            resolve(self.dictionaryToJson(lockVersion as? [String: Any] ?? [:]))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getAccessoryElectricQuantity(accessoryType: Double, accessoryMac: String, lockData: String, resolve: @escaping (NumberNumberPair) -> Void, reject: @escaping (Double, String) -> Void) {
        let type = TTAccessoryType(rawValue: Int32(accessoryType)) ?? .doorSensor
        TTLock.getAccessoryElectricQuantity(with: type, accessoryMac: accessoryMac, lockData: lockData, success: { electricQuantity, updateDate in
            resolve((Double(electricQuantity), Double(updateDate)))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetLock(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.resetLock(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetEkey(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.resetEkey(withLockData: lockData, success: { lockData in
            resolve(lockData ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func controlLock(controlAction: Double, lockData: String, resolve: @escaping (NumberNumberNumberTriple) -> Void, reject: @escaping (Double, String) -> Void) {
        let action = TTControlAction(rawValue: Int(controlAction) + 1)!
        TTLock.controlLock(with: action, lockData: lockData, success: { lockTime, electricQuantity, uniqueId in
            resolve((Double(lockTime), Double(electricQuantity), Double(uniqueId)))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Passcode Operations

    public func createCustomPasscode(passcode: String, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.createCustomPasscode(passcode, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func recoverPasscode(passcode: String, passcodeType: Double, cycleType: Double, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let type = TTPasscodeType(rawValue: Int(passcodeType)) ?? .permanent
        TTLock.recoverPasscode(passcode, newPasscode: passcode, passcodeType: type, startDate: Int64(startDate), endDate: Int64(endDate), cycleType: Int32(cycleType), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyPasscode(passcodeOrigin: String, passcodeNew: String, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.modifyPasscode(passcodeOrigin, newPasscode: passcodeNew, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deletePasscode(passcode: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.deletePasscode(passcode, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetPasscode(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.resetPasscodes(withLockData: lockData, success: { lockData in
            resolve(lockData ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Status

    public func getLockSwitchState(lockData: String, resolve: @escaping (Double) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getLockSwitchState(withLockData: lockData, success: { state, _ in
            resolve(Double(state.rawValue))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Card Operations

    public func addCard(cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.addICCard(withCyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, progress: { _ in
            self.sendEvent(EVENT_ADD_CARD_PROGRESS, body: nil)
        }, success: { cardNumber in
            resolve(cardNumber ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func recoverCard(cardNumber: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.recoverICCard(withCyclicConfig: cyclicConfig, cardNumber: cardNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: { cardNumber in
            resolve(cardNumber ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyCardValidityPeriod(cardNumber: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.modifyICCardValidityPeriod(withCyclicConfig: cyclicConfig, cardNumber: cardNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteCard(cardNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.deleteICCardNumber(cardNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllCards(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearAllICCards(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Fingerprint Operations

    public func addFingerprint(cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.addFingerprint(withCyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, progress: { currentCount, totalCount in
            let data: [String: Any] = [
                "current": currentCount,
                "total": totalCount
            ]
            self.sendEvent(EVENT_ADD_FINGERPRINT_PROGRESS, body: data)
        }, success: { fingerprintNumber in
            resolve(fingerprintNumber ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyFingerprintValidityPeriod(fingerprintNumber: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.modifyFingerprintValidityPeriod(withCyclicConfig: cyclicConfig, fingerprintNumber: fingerprintNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteFingerprint(fingerprintNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.deleteFingerprintNumber(fingerprintNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllFingerprints(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearAllFingerprints(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Admin Passcode

    public func modifyAdminPasscode(adminPasscode: String, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.modifyAdminPasscode(adminPasscode, lockData: lockData, success: {
            resolve(adminPasscode)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Time

    public func setLockTime(timestamp: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.setLockTimeWithTimestamp(Int64(timestamp), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockTime(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getLockTime(withLockData: lockData, success: { lockTimestamp in
            resolve(String(lockTimestamp))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock System Info

    public func getLockSystem(lockData: String, resolve: @escaping (DeviceSystemModal) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getLockSystemInfo(withLockData: lockData, success: { systemModel in
            resolve(self.dictionaryFromModel(systemModel) as NSDictionary)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockElectricQuantity(lockData: String, resolve: @escaping (Double) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getElectricQuantity(withLockData: lockData, success: { electricQuantity in
            resolve(Double(electricQuantity))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Operation Record

    public func getLockOperationRecord(type: Double, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        let logType = TTOperateLogType(rawValue: Int(type) + 1)!
        TTLock.getOperationLog(with: logType, lockData: lockData, success: { operateRecord in
            resolve(operateRecord ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Automatic Locking

    public func getLockAutomaticLockingPeriodicTime(lockData: String, resolve: @escaping (NumberNumberNumberTriple) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getAutomaticLockingPeriodicTime(withLockData: lockData, success: { currentTime, minTime, maxTime in
            resolve((Double(currentTime), Double(maxTime), Double(minTime)))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockAutomaticLockingPeriodicTime(seconds: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.setAutomaticLockingPeriodicTime(Int32(seconds), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Remote Unlock Switch

    public func getLockRemoteUnlockSwitchState(lockData: String, resolve: @escaping (Bool) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getRemoteUnlockSwitch(withLockData: lockData, success: { isOn in
            resolve(isOn)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockRemoteUnlockSwitchState(isOn: Bool, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.setRemoteUnlockSwitchOn(isOn, lockData: lockData, success: { lockData in
            resolve(lockData ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Config

    public func getLockConfig(config: Double, lockData: String, resolve: @escaping (NumberBooleanPair) -> Void, reject: @escaping (Double, String) -> Void) {
        let type = TTLockConfigType(rawValue: Int(config) + 1)!
        TTLock.getConfigWith(type, lockData: lockData, success: { type, isOn in
            resolve((Double(type.rawValue), isOn))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockConfig(config: Double, isOn: Bool, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let type = TTLockConfigType(rawValue: Int(config) + 1)!
        TTLock.setLockConfigWith(type, on: isOn, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Sound Volume

    public func setLockSoundVolume(soundVolumeValue: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let soundVolume: TTSoundVolume
        switch soundVolumeValue {
        case Double(TTSoundVolumeValue.TTSoundVolumeOn):
            soundVolume = .on
        case Double(TTSoundVolumeValue.TTSoundVolumeOff):
            soundVolume = .off
        case Double(TTSoundVolumeValue.TTSoundVolumeFirstLevel):
            soundVolume = .firstLevel
        case Double(TTSoundVolumeValue.TTSoundVolumeSecondLevel):
            soundVolume = .secondLevel
        case Double(TTSoundVolumeValue.TTSoundVolumeThirdLevel):
            soundVolume = .thirdLevel
        case Double(TTSoundVolumeValue.TTSoundVolumeFourthLevel):
            soundVolume = .fourthLevel
        case Double(TTSoundVolumeValue.TTSoundVolumeFifthLevel):
            soundVolume = .fifthLevel
        default:
            soundVolume = .off
        }

        TTLock.setLockSoundWith(soundVolume, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockSoundVolume(lockData: String, resolve: @escaping (Double) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getLockSound(withLockData: lockData, success: { soundVolume in
            resolve(Double(soundVolume.rawValue))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Unlock Direction

    public func getUnlockDirection(lockData: String, resolve: @escaping (Double) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getUnlockDirection(withLockData: lockData, success: { direction in
            resolve(Double(direction.rawValue))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setUnlockDirection(direction: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let unlockDirection = TTUnlockDirection(rawValue: Int32(direction)) ?? .left
        TTLock.setUnlockDirection(unlockDirection, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setUnlockDirectionAutomatic(lockData: String, resolve: @escaping (Double) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.autoSetUnlockDirection(withLockData: lockData, success: { state in
            resolve(Double(state.rawValue))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Passage Mode

    public func addPassageMode(type: Double, weekly: [Double]?, monthly: [Double]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let modeType = TTPassageModeType(rawValue: Int(type) + 1)!
        let weeklyArray = weekly?.map { NSNumber(value: $0) } ?? []
        let monthlyArray = monthly?.map { NSNumber(value: $0) } ?? []

        TTLock.configPassageMode(with: modeType, weekly: weeklyArray, monthly: monthlyArray, startDate: Int32(startDate), endDate: Int32(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllPassageModes(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearPassageMode(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Remote Key

    public func addRemoteKey(remoteMac: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.addWirelessKeyFob(withCyclicConfig: cyclicConfig, keyFobMac: remoteMac, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyRemoteKey(remoteMac: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.modifyWirelessKeyFobValidityPeriod(withCyclicConfig: cyclicConfig, keyFobMac: remoteMac, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteRemoteKey(remoteMac: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.deleteWirelessKeyFob(withKeyFobMac: remoteMac, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllRemoteKey(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearWirelessKeyFobs(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Door Sensor

    public func addDoorSensor(doorSensorMac: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.addDoorSensor(withDoorSensorMac: doorSensorMac, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllDoorSensor(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearDoorSensor(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setDoorSensorAlertTime(time: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.setDoorSensorAlertTime(Int32(time), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Wifi Operations

    public func scanWifi(lockData: String, reject: @escaping (Double, String) -> Void) {
        TTLock.scanWifi(withLockData: lockData, success: { isFinished, wifiArr in
            let data: [String: Any] = [
                "isFinished": isFinished,
                "wifis": wifiArr
            ]
            self.sendEvent(EVENT_SCAN_LOCK_WIFI, body: data)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configWifi(wifiName: String, wifiPassword: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.configWifi(withSSID: wifiName, wifiPassword: wifiPassword, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configServer(ip: String, port: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.configServer(withServerAddress: ip, portNumber: port, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getWifiInfo(lockData: String, resolve: @escaping (StringNumberPair) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getWifiInfo(withLockData: lockData, success: { wifiMac, wifiRssi in
            resolve((wifiMac ?? "", Double(wifiRssi)))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configIp(info: WifiLockServerInfo, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.configIp(withInfo: info, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Wifi Power Saving

    public func getWifiPowerSavingTime(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.getWifiPowerSavingTime(withLockData: lockData, success: { timesJsonString in
            resolve(timesJsonString ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configWifiPowerSavingTime(days: [Double]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let weekDays = days?.map { NSNumber(value: $0) } ?? []
        TTLock.configWifiPowerSavingTime(withWeekDays: weekDays, startDate: Int32(startDate), endDate: Int32(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearWifiPowerSavingTime(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearWifiPowerSavingTime(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Face Operations

    public func addFace(cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.addFace(withCyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, progress: { state, faceErrorCode in
            if state == .canStartAdd || state == .error {
                let stateValue = state.rawValue - 2
                let data: [String: Any] = [
                    "step": stateValue,
                    "status": faceErrorCode.rawValue
                ]
                self.sendEvent(EVENT_ADD_FACE_PROGRESS, body: data)
            }
        }, success: { faceNumber in
            resolve(faceNumber ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func addFaceFeatureData(faceFeatureData: String, cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.addFaceFeatureData(faceFeatureData, cyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: { faceNumber in
            resolve(faceNumber ?? "")
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyFaceValidityPeriod(cycleList: [CycleDateParam]?, startDate: Double, endDate: Double, faceNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let cyclicConfig = cycleList ?? []
        TTLock.modifyFaceValidity(withCyclicConfig: cyclicConfig, faceNumber: faceNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteFace(faceNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.deleteFaceNumber(faceNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearFace(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.clearFace(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lift Operations

    public func activateLiftFloors(floors: String, lockData: String, resolve: @escaping (NumberNumberNumberTriple) -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.activateLiftFloors(floors, lockData: lockData, success: { lockTime, electricQuantity, uniqueId in
            resolve((Double(lockTime), Double(electricQuantity), Double(uniqueId)))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLiftControlEnableFloors(floors: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        TTLock.setLiftControlableFloors(floors, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLiftWorkMode(workMode: Double, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Double, String) -> Void) {
        let liftWorkMode = TTLiftWorkMode(rawValue: Int32(workMode))!
        TTLock.setLiftWorkMode(liftWorkMode, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Gateway

    public func startScanGateway() {
        TTGateway.startScanGateway { model in
            let gateway = model!
            var dict: [String: Any] = [:]
            dict["gatewayMac"] = gateway.gatewayMac ?? ""
            dict["gatewayName"] = gateway.gatewayName ?? ""
            dict["rssi"] = gateway.rssi
            dict["isDfuMode"] = gateway.isDfuMode
            dict["type"] = gateway.type.rawValue
            self.sendEvent(EVENT_SCAN_GATEWAY, body: dict)
        }
    }

    public func stopScanGateway() {
        TTGateway.stopScanGateway()
    }

    public func connect(mac: String, resolve: @escaping (Double) -> Void) {
        TTGateway.connectGateway(withGatewayMac: mac) { connectStatus in
            resolve(connectStatus.rawValue)
        }
    }

    public func getNearbyWifi(resolve: @escaping (Double) -> Void) {
        TTGateway.scanWiFiByGateway({ isFinished, wifiArr, status in
            if status == .success {
                var wifiList: [[String: Any]] = []
                for dict in wifiArr ?? [] {
                    var wifiDict: [String: Any] = [:]
                    if let dict = dict as? [String: Any] {
                        wifiDict["wifi"] = dict["SSID"] ?? ""
                        wifiDict["rssi"] = dict["RSSI"] ?? 0
                    }
                    wifiList.append(wifiDict)
                }
                let data: [String: Any] = [
                    "wifis": wifiList
                ]
                self.sendEvent(EVENT_SCAN_WIFI, body: data)

                if isFinished {
                    resolve(status.rawValue)
                }
            } else {
                resolve(status.rawValue)
            }
        })
    }

    public func initGateway(params: InitGatewayParam, resolve: @escaping (InitGatewayModal) -> Void, reject: @escaping (Double) -> Void) {
        let gatewayType = TTGatewayType(rawValue: Int(params.type))!

        var paramDict: [String: Any] = [:]
        paramDict["SSID"] = params.wifi ?? ""
        paramDict["wifiPwd"] = params.wifiPassword ?? ""
        paramDict["uid"] = params.ttLockUid ?? ""
        paramDict["userPwd"] = params.ttLockLoginPassword ?? ""
        paramDict["gatewayName"] = params.gatewayName ?? ""
        paramDict["serverAddress"] = params.serverIp ?? ""
        paramDict["portNumber"] = params.serverPort ?? ""
        paramDict["gatewayVersion"] = gatewayType.rawValue

        if gatewayType == .gateWayTypeG3 || gatewayType == .gateWayTypeG4 {
            paramDict["SSID"] = "1"
            paramDict["wifiPwd"] = "1"
        }

        if let ipAddress = params.ipAddress {
            var staticIpDict: [String: Any] = [:]
            staticIpDict["type"] = params.ipSettingType ?? 0
            staticIpDict["ipAddress"] = ipAddress
            staticIpDict["subnetMask"] = params.subnetMask ?? ""
            staticIpDict["router"] = params.router ?? ""
            staticIpDict["preferredDns"] = params.preferredDns ?? ""
            staticIpDict["alternateDns"] = params.alternateDns ?? ""

            TTGateway.configIp(withInfo: staticIpDict) { status in
                if status == TTGatewayStatus.success {
                    TTGateway.initializeGateway(withInfoDic: paramDict) { systemInfoModel, status in
                        if status == TTGatewayStatus.success {
                            let resultDict: [String: Any] = [
                                "modelNum": systemInfoModel?.modelNum ?? "",
                                "hardwareRevision": systemInfoModel?.hardwareRevision ?? "",
                                "firmwareRevision": systemInfoModel?.firmwareRevision ?? ""
                            ]
                            resolve(resultDict as NSDictionary)
                        } else {
                            reject(self.getTTGatewayErrorCode(status))
                        }
                    }
                } else {
                    reject(self.getTTGatewayErrorCode(status))
                }
            }
        } else {
            TTGateway.initializeGateway(withInfoDic: paramDict) { systemInfoModel, status in
                if status == TTGatewayStatus.success {
                    let resultDict: [String: Any] = [
                        "modelNum": systemInfoModel?.modelNum ?? "",
                        "hardwareRevision": systemInfoModel?.hardwareRevision ?? "",
                        "firmwareRevision": systemInfoModel?.firmwareRevision ?? ""
                    ]
                    resolve(resultDict as NSDictionary)
                } else {
                    reject(self.getTTGatewayErrorCode(status))
                }
            }
        }
    }

    // MARK: - Remote Key

    public func startScanRemoteKey() {
        TTWirelessKeyFob.startScan { model in
            let keyFob = model!
            var data: [String: Any] = [:]
            data["remoteKeyName"] = keyFob.keyFobName ?? ""
            data["rssi"] = keyFob.rssi
            data["remoteKeyMac"] = keyFob.keyFobMac ?? ""
            self.sendEvent(EVENT_SCAN_REMOTE_KEY, body: data)
        }
    }

    public func stopScanRemoteKey() {
        TTWirelessKeyFob.stopScan()
    }

    public func initRemoteKey(remoteMac: String, lockData: String, resolve: @escaping (NumberStringPair) -> Void, reject: @escaping (Double, String) -> Void) {
        TTWirelessKeyFob.newInitialize(withKeyFobMac: remoteMac, lockData: lockData) { status, electricQuantity, systemModel in
            if status.rawValue == TTKeyFobStatusValue.TTKeyFobSuccess {
                resolve((Double(electricQuantity), self.dictionaryToJson(self.dictionaryFromModel(systemModel!))))
            } else {
                self.responseFail(.REMOTE_KEY, code: status, errorMessage: nil, reject: reject)
            }
        }
    }

    public func getRemoteKeySystemInfo(remoteMac: String, resolve: @escaping (DeviceSystemModal) -> Void, reject: @escaping (Double, String) -> Void) {
        // This method might need to be implemented based on TTLock SDK
        // For now, returning empty dict - this should be updated when the actual implementation is available
        resolve([:] as NSDictionary)
    }

    // MARK: - Door Sensor

    public func startScanDoorSensor() {
        TTDoorSensor.startScan(success: { model in
            var data: [String: Any] = [:]
            data["name"] = model.name ?? ""
            data["rssi"] = model.rssi
            data["mac"] = model.mac ?? ""
            data["scanTime"] = model.scanTime
            self.sendEvent(EVENT_SCAN_DOOR_SENSOR, body: data)
        }, failure: { _ in })
    }

    public func stopScanDoorSensor() {
        TTDoorSensor.stopScan()
    }

    public func initDoorSensor(doorSensorMac: String, lockData: String, resolve: @escaping (NumberStringPair) -> Void, reject: @escaping (Double, String) -> Void) {
        TTDoorSensor.initialize(withDoorSensorMac: doorSensorMac, lockData: lockData, success: { electricQuantity, systemModel in
            resolve((Double(electricQuantity), self.dictionaryToJson(self.dictionaryFromModel(systemModel))))
        }, failure: { error in
            self.responseFail(.DOOR_SENSOR, code: error, errorMessage: nil, reject: reject)
        })
    }

    // MARK: - Wireless Keypad

    public func startScanWirelessKeypad() {
        TTWirelessKeypad.startScanKeypad { model in
            let keypad = model!
            var data: [String: Any] = [:]
            data["name"] = keypad.keypadName ?? ""
            data["rssi"] = keypad.rssi
            data["mac"] = keypad.keypadMac ?? ""
            self.sendEvent(EVENT_SCAN_WIRELESS_KEYPAD, body: data)
        }
    }

    public func stopScanWirelessKeypad() {
        TTWirelessKeypad.stopScanKeypad()
    }

    public func initWirelessKeypad(keypadMac: String, lockMac: String, resolve: @escaping (NumberStringPair) -> Void, reject: @escaping (Double, String) -> Void) {
        TTWirelessKeypad.initializeKeypad(withKeypadMac: keypadMac, lockMac: lockMac) { wirelessKeypadFeatureValue, status, electricQuantity in
            if status.rawValue == TTKeypadStatusValue.TTKeypadSuccess {
                resolve((Double(electricQuantity), wirelessKeypadFeatureValue ?? ""))
            } else {
                self.responseFail(.REMOTE_KEY_PAD, code: status, errorMessage: nil, reject: reject)
            }
        }
    }

    // Helper to convert dictionary to JSON string
    private func dictionaryToJson(_ dict: [String: Any]) -> String {
        guard let jsonData = try? JSONSerialization.data(withJSONObject: dict, options: []),
              let jsonString = String(data: jsonData, encoding: .utf8) else {
            return "{}"
        }
        return jsonString
    }

    // MARK: - Event Listeners
    public func addListener(eventName: String, listener: @escaping (String, AnyMap?) -> Void) {
        if listeners[eventName] == nil {
            listeners[eventName] = []
        }
        listeners[eventName]?.append(listener)
    }

    public func removeListener(eventName: String, listener: @escaping (String, AnyMap?) -> Void) {
        // Note: Swift closures cannot be compared directly
        // In practice, you may want to implement this using listener IDs
        // For now, we'll clear all listeners for the event
        listeners[eventName]?.removeAll()
    }
}
