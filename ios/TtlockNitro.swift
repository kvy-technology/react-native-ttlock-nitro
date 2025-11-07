import TTLock
import React
import NitroModules

// Event names
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

    // Helper: response fail
    private func responseFail(_ device: Device, code: Int, errorMessage: String?, reject: @escaping (Int, String) -> Void) {
        let errorCode: NSNumber
        switch device {
        case .GATEWAY:
            errorCode = NSNumber(value: getTTGatewayErrorCode(code))
        case .REMOTE_KEY:
            errorCode = NSNumber(value: getTTRemoteKeyErrorCode(code))
        case .REMOTE_KEY_PAD:
            errorCode = NSNumber(value: getTTRemoteKeypadErrorCode(code))
        case .DOOR_SENSOR:
            errorCode = NSNumber(value: getTTDoorSensorErrorCode(code))
        default:
            errorCode = getTTLockErrorCode(code)
        }
        reject(errorCode.intValue, errorMessage ?? "")
    }

    // MARK: - Error Code Converters

    private func getTTGatewayErrorCode(_ status: Int) -> Int {
        let codeArray = [
            TTGatewayFail,
            TTGatewayWrongSSID,
            TTGatewayWrongWifiPassword,
            TTGatewayWrongCRC,
            TTGatewayWrongAeskey,
            TTGatewayNotConnect,
            TTGatewayDisconnect,
            TTGatewayFailConfigRouter,
            TTGatewayFailConfigServer,
            TTGatewayFailConfigAccount,
            TTGatewayNoSIM,
            TTGatewayInvalidCommand,
            TTGatewayFailConfigIP,
            TTGatewayFailInvaildIP
        ]

        for (index, code) in codeArray.enumerated() {
            if code == status {
                return index
            }
        }
        return 0
    }

    private func getTTRemoteKeyErrorCode(_ status: Int) -> Int {
        let codeArray = [
            TTKeyFobFail,
            TTKeyFobWrongCRC,
            TTKeyFobConnectTimeout
        ]

        for (index, code) in codeArray.enumerated() {
            if code == status {
                return index
            }
        }
        return 0
    }

    private func getTTRemoteKeypadErrorCode(_ status: Int) -> Int {
        let codeArray = [
            TTKeypadFail,
            TTKeypadWrongCRC,
            TTKeypadConnectTimeout,
            TTKeypadWrongFactorydDate
        ]

        for (index, code) in codeArray.enumerated() {
            if code == status {
                return index
            }
        }
        return 0
    }

    private func getTTDoorSensorErrorCode(_ status: Int) -> Int {
        let codeArray = [
            TTDoorSensorErrorBluetoothPowerOff,
            TTDoorSensorErrorConnectTimeout,
            TTDoorSensorErrorFail,
            TTDoorSensorErrorWrongCRC
        ]

        for (index, code) in codeArray.enumerated() {
            if code == status {
                return index
            }
        }
        return 2
    }

    private func getTTLockErrorCode(_ code: Int) -> NSNumber {
        let codeArray = [
            TTErrorHadReseted,
            TTErrorCRCError,
            TTErrorNoPermisstion,
            TTErrorWrongAdminCode,
            TTErrorLackOfStorageSpace,
            TTErrorInSettingMode,
            TTErrorNoAdmin,
            TTErrorNotInSettingMode,
            TTErrorWrongDynamicCode,
            TTErrorIsNoPower,
            TTErrorResetPasscode,
            TTErrorUpdatePasscodeIndex,
            TTErrorInvalidLockFlagPos,
            TTErrorEkeyExpired,
            TTErrorPasscodeLengthInvalid,
            TTErrorSamePasscodes,
            TTErrorEkeyInactive,
            TTErrorAesKey,
            TTErrorFail,
            TTErrorPasscodeExist,
            TTErrorPasscodeNotExist,
            TTErrorLackOfStorageSpaceWhenAddingPasscodes,
            TTErrorInvalidParaLength,
            TTErrorCardNotExist,
            TTErrorFingerprintDuplication,
            TTErrorFingerprintNotExist,
            TTErrorInvalidCommand,
            TTErrorInFreezeMode,
            TTErrorInvalidClientPara,
            TTErrorLockIsLocked,
            TTErrorRecordNotExist,
            TTErrorWrongSSID,
            TTErrorWrongWifiPassword,
            TTErrorBluetoothPoweredOff,
            TTErrorConnectionTimeout,
            TTErrorDisconnection,
            TTErrorLockIsBusy,
            TTErrorWrongLockData,
            TTErrorInvalidParameter
        ]

        for (index, errorCode) in codeArray.enumerated() {
            if errorCode == code {
                return NSNumber(value: index)
            }
        }
        return NSNumber(value: 18)
    }

    // MARK: - Utility

    public func getBluetoothState(resolve: @escaping (Int) -> Void) {
        let bluetoothState = TTLock.bluetoothState()
        resolve(Int(bluetoothState.rawValue))
    }

    public func supportFunction(lockFunction: Int, lockData: String, resolve: @escaping (Bool) -> Void) {
        let isSupport = TTUtil.lockFeatureValue(lockData, suportFunction: lockFunction)
        resolve(isSupport)
    }

    // MARK: - Lock Operations

    public func startScan() {
        TTLock.startScan { scanModel in
            var data: [String: Any] = [:]
            data["lockName"] = scanModel.lockName ?? ""
            data["lockMac"] = scanModel.lockMac ?? ""
            data["isInited"] = scanModel.isInited
            data["isKeyboardActivated"] = scanModel.isAllowUnlock
            data["electricQuantity"] = scanModel.electricQuantity
            data["lockVersion"] = scanModel.lockVersion ?? ""
            data["lockSwitchState"] = scanModel.lockSwitchState.rawValue
            data["rssi"] = scanModel.rssi
            data["oneMeterRssi"] = scanModel.oneMeterRSSI
            self.sendEvent(EVENT_SCAN_LOCK, body: data)
        }
    }

    public func stopScan() {
        TTLock.stopScan()
    }

    public func initLock(params: [String: Any], resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        var dict: [String: Any] = [:]
        if let lockMac = params["lockMac"] as? String {
            dict["lockMac"] = lockMac
        }
        if let clientPara = params["clientPara"] as? String {
            dict["clientPara"] = clientPara
        }
        TTLock.initLock(withDict: dict, success: { lockData in
            resolve(lockData)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockVersionWithLockMac(lockMac: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getLockVersion(withLockMac: lockMac, success: { lockVersion in
            resolve(self.dictionaryToJson(lockVersion))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getAccessoryElectricQuantity(accessoryType: Int, accessoryMac: String, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getAccessoryElectricQuantity(withType: accessoryType, accessoryMac: accessoryMac, lockData: lockData, success: { electricQuantity, updateDate in
            resolve([electricQuantity, Int(updateDate)] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetLock(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.resetLock(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetEkey(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.resetEkey(withLockData: lockData, success: { lockData in
            resolve(lockData)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func controlLock(controlAction: Int, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        let action = TTControlAction(rawValue: controlAction + 1)!
        TTLock.controlLock(withControlAction: action, lockData: lockData, success: { lockTime, electricQuantity, uniqueId in
            resolve([Int(lockTime), electricQuantity, Int(uniqueId)] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Passcode Operations

    public func createCustomPasscode(passcode: String, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.createCustomPasscode(passcode, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func recoverPasscode(passcode: String, passcodeType: Int, cycleType: Int, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.recoverPasscode(passcode, newPasscode: passcode, passcodeType: passcodeType, startDate: Int64(startDate), endDate: Int64(endDate), cycleType: cycleType, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyPasscode(passcodeOrigin: String, passcodeNew: String, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.modifyPasscode(passcodeOrigin, newPasscode: passcodeNew, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deletePasscode(passcode: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.deletePasscode(passcode, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func resetPasscode(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.resetPasscodes(withLockData: lockData, success: { lockData in
            resolve(lockData)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Status

    public func getLockSwitchState(lockData: String, resolve: @escaping (Int) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getLockSwitchState(withLockData: lockData, success: { state, _ in
            resolve(state.rawValue)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Card Operations

    public func addCard(cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.addICCard(withCyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, progress: { _ in
            self.sendEvent(EVENT_ADD_CARD_PROGRESS, body: nil)
        }, success: { cardNumber in
            resolve(cardNumber)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func recoverCard(cardNumber: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.recoverICCard(withCyclicConfig: cyclicConfig, cardNumber: cardNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: { cardNumber in
            resolve(cardNumber)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyCardValidityPeriod(cardNumber: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.modifyICCardValidityPeriod(withCyclicConfig: cyclicConfig, cardNumber: cardNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteCard(cardNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.deleteICCard(number: cardNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllCards(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearAllICCards(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Fingerprint Operations

    public func addFingerprint(cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.addFingerprint(withCyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, progress: { currentCount, totalCount in
            let data: [String: Any] = [
                "current": currentCount,
                "total": totalCount
            ]
            self.sendEvent(EVENT_ADD_FINGERPRINT_PROGRESS, body: data)
        }, success: { fingerprintNumber in
            resolve(fingerprintNumber)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyFingerprintValidityPeriod(fingerprintNumber: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.modifyFingerprintValidityPeriod(withCyclicConfig: cyclicConfig, fingerprintNumber: fingerprintNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteFingerprint(fingerprintNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.deleteFingerprint(number: fingerprintNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllFingerprints(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearAllFingerprints(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Admin Passcode

    public func modifyAdminPasscode(adminPasscode: String, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.modifyAdminPasscode(adminPasscode, lockData: lockData, success: {
            resolve(adminPasscode)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Time

    public func setLockTime(timestamp: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setLockTime(withTimestamp: Int64(timestamp), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockTime(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getLockTime(withLockData: lockData, success: { lockTimestamp in
            resolve(String(lockTimestamp))
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock System Info

    public func getLockSystem(lockData: String, resolve: @escaping (NSDictionary) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getLockSystemInfo(withLockData: lockData, success: { systemModel in
            resolve(self.dictionaryFromModel(systemModel) as NSDictionary)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockElectricQuantity(lockData: String, resolve: @escaping (Int) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getElectricQuantity(withLockData: lockData, success: { electricQuantity in
            resolve(electricQuantity)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Operation Record

    public func getLockOperationRecord(type: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let logType = TTOperateLogType(rawValue: type + 1)!
        TTLock.getOperationLog(withType: logType, lockData: lockData, success: { operateRecord in
            resolve(operateRecord)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Automatic Locking

    public func getLockAutomaticLockingPeriodicTime(lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getAutomaticLockingPeriodicTime(withLockData: lockData, success: { currentTime, minTime, maxTime in
            resolve([currentTime, maxTime, minTime] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockAutomaticLockingPeriodicTime(seconds: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setAutomaticLockingPeriodicTime(seconds, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Remote Unlock Switch

    public func getLockRemoteUnlockSwitchState(lockData: String, resolve: @escaping (Bool) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getRemoteUnlockSwitch(withLockData: lockData, success: { isOn in
            resolve(isOn)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockRemoteUnlockSwitchState(isOn: Bool, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setRemoteUnlockSwitch(on: isOn, lockData: lockData, success: { lockData in
            resolve(lockData)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lock Config

    public func getLockConfig(config: Int, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        let type = TTLockConfigType(rawValue: config + 1)!
        TTLock.getLockConfig(withType: type, lockData: lockData, success: { type, isOn in
            resolve([type.rawValue, isOn] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLockConfig(config: Int, isOn: Bool, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let type = TTLockConfigType(rawValue: config + 1)!
        TTLock.setLockConfig(withType: type, on: isOn, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Sound Volume

    public func setLockSoundVolume(soundVolumeValue: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setLockSound(withSoundVolume: soundVolumeValue, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getLockSoundVolume(lockData: String, resolve: @escaping (Int) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getLockSound(withLockData: lockData, success: { soundVolume in
            resolve(soundVolume.rawValue)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Unlock Direction

    public func getUnlockDirection(lockData: String, resolve: @escaping (Int) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getUnlockDirection(withLockData: lockData, success: { direction in
            resolve(direction.rawValue)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setUnlockDirection(direction: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setUnlockDirection(direction, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setUnlockDirectionAutomatic(lockData: String, resolve: @escaping (Int) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.autoSetUnlockDirection(withLockData: lockData, success: { state in
            resolve(state.rawValue)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Passage Mode

    public func addPassageMode(type: Int, weekly: [Int]?, monthly: [Int]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let modeType = TTPassageModeType(rawValue: type + 1)!
        let weeklyArray = weekly?.map { NSNumber(value: $0) } ?? []
        let monthlyArray = monthly?.map { NSNumber(value: $0) } ?? []

        TTLock.configPassageMode(withType: modeType, weekly: weeklyArray, monthly: monthlyArray, startDate: startDate, endDate: endDate, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllPassageModes(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearPassageMode(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Remote Key

    public func addRemoteKey(remoteMac: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.addWirelessKeyFob(withCyclicConfig: cyclicConfig, keyFobMac: remoteMac, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyRemoteKey(remoteMac: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.modifyWirelessKeyFobValidityPeriod(withCyclicConfig: cyclicConfig, keyFobMac: remoteMac, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteRemoteKey(remoteMac: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.deleteWirelessKeyFob(withKeyFobMac: remoteMac, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllRemoteKey(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearWirelessKeyFobs(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Door Sensor

    public func addDoorSensor(doorSensorMac: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.addDoorSensor(withDoorSensorMac: doorSensorMac, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearAllDoorSensor(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearDoorSensor(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setDoorSensorAlertTime(time: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setDoorSensorAlertTime(time, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Wifi Operations

    public func scanWifi(lockData: String, reject: @escaping (Int, String) -> Void) {
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

    public func configWifi(wifiName: String, wifiPassword: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.configWifi(withSSID: wifiName, wifiPassword: wifiPassword, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configServer(ip: String, port: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.configServer(withServerAddress: ip, portNumber: port, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func getWifiInfo(lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getWifiInfo(withLockData: lockData, success: { wifiMac, wifiRssi in
            resolve([wifiMac ?? "", wifiRssi] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configIp(info: [String: Any], lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.configIp(withInfo: info, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Wifi Power Saving

    public func getWifiPowerSavingTime(lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.getWifiPowerSavingTime(withLockData: lockData, success: { timesJsonString in
            resolve(timesJsonString)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func configWifiPowerSavingTime(days: [Int]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let weekDays = days?.map { NSNumber(value: $0) } ?? []
        TTLock.configWifiPowerSavingTime(withWeekDays: weekDays, startDate: startDate, endDate: endDate, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearWifiPowerSavingTime(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearWifiPowerSavingTime(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Face Operations

    public func addFace(cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

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
            resolve(faceNumber)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func addFaceFeatureData(faceFeatureData: String, cycleList: [[String: Any]]?, startDate: Int, endDate: Int, lockData: String, resolve: @escaping (String) -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.addFaceFeatureData(faceFeatureData, cyclicConfig: cyclicConfig, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: { faceNumber in
            resolve(faceNumber)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func modifyFaceValidityPeriod(cycleList: [[String: Any]]?, startDate: Int, endDate: Int, faceNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let cyclicConfig = cycleList?.map { dict -> TTCyclicConfig in
            let config = TTCyclicConfig()
            config.weekDay = dict["weekDay"] as? Int ?? 0
            config.startTime = dict["startTime"] as? Int ?? 0
            config.endTime = dict["endTime"] as? Int ?? 0
            return config
        } ?? []

        TTLock.modifyFaceValidity(withCyclicConfig: cyclicConfig, faceNumber: faceNumber, startDate: Int64(startDate), endDate: Int64(endDate), lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func deleteFace(faceNumber: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.deleteFace(number: faceNumber, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func clearFace(lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.clearFace(withLockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Lift Operations

    public func activateLiftFloors(floors: String, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.activateLiftFloors(floors, lockData: lockData, success: { lockTime, electricQuantity, uniqueId in
            resolve([Int(lockTime), electricQuantity, Int(uniqueId)] as NSArray)
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLiftControlEnableFloors(floors: String, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        TTLock.setLiftControlableFloors(floors, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    public func setLiftWorkMode(workMode: Int, lockData: String, resolve: @escaping () -> Void, reject: @escaping (Int, String) -> Void) {
        let liftWorkMode = TTLiftWorkMode(rawValue: workMode)!
        TTLock.setLiftWorkMode(liftWorkMode, lockData: lockData, success: {
            resolve()
        }, failure: { errorCode, errorMsg in
            self.responseFail(.LOCK, code: errorCode, errorMessage: errorMsg, reject: reject)
        })
    }

    // MARK: - Gateway

    public func startScanGateway() {
        TTGateway.startScanGateway { model in
            var dict: [String: Any] = [:]
            dict["gatewayMac"] = model.gatewayMac ?? ""
            dict["gatewayName"] = model.gatewayName ?? ""
            dict["rssi"] = model.rssi
            dict["isDfuMode"] = model.isDfuMode
            dict["type"] = model.type.rawValue
            self.sendEvent(EVENT_SCAN_GATEWAY, body: dict)
        }
    }

    public func stopScanGateway() {
        TTGateway.stopScanGateway()
    }

    public func connect(mac: String, resolve: @escaping (Int) -> Void) {
        TTGateway.connectGateway(withGatewayMac: mac) { connectStatus in
            resolve(connectStatus.rawValue)
        }
    }

    public func getNearbyWifi(resolve: @escaping (Int) -> Void) {
        TTGateway.scanWiFi(byGateway: { isFinished, wifiArr, status in
            if status == .success {
                var wifiList: [[String: Any]] = []
                for dict in wifiArr {
                    var wifiDict: [String: Any] = [:]
                    wifiDict["wifi"] = dict["SSID"] ?? ""
                    wifiDict["rssi"] = dict["RSSI"] ?? 0
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

    public func initGateway(params: [String: Any], resolve: @escaping (NSDictionary) -> Void, reject: @escaping (Int) -> Void) {
        let gatewayType = TTGatewayType(rawValue: params["type"] as? Int ?? 0)!

        var paramDict: [String: Any] = [:]
        paramDict["SSID"] = params["wifi"] ?? ""
        paramDict["wifiPwd"] = params["wifiPassword"] ?? ""
        paramDict["uid"] = params["ttLockUid"] ?? ""
        paramDict["userPwd"] = params["ttLockLoginPassword"] ?? ""
        paramDict["gatewayName"] = params["gatewayName"] ?? ""
        paramDict["serverAddress"] = params["serverIp"] ?? ""
        paramDict["portNumber"] = params["serverPort"] ?? ""
        paramDict["gatewayVersion"] = gatewayType.rawValue

        if gatewayType == .g3 || gatewayType == .g4 {
            paramDict["SSID"] = "1"
            paramDict["wifiPwd"] = "1"
        }

        if let ipAddress = params["ipAddress"] as? String {
            var staticIpDict: [String: Any] = [:]
            staticIpDict["type"] = params["ipSettingType"] ?? 0
            staticIpDict["ipAddress"] = ipAddress
            staticIpDict["subnetMask"] = params["subnetMask"] ?? ""
            staticIpDict["router"] = params["router"] ?? ""
            staticIpDict["preferredDns"] = params["preferredDns"] ?? ""
            staticIpDict["alternateDns"] = params["alternateDns"] ?? ""

            TTGateway.configIp(withInfo: staticIpDict) { status in
                if status == .success {
                    TTGateway.initializeGateway(withInfoDic: paramDict) { systemInfoModel, status in
                        if status == .success {
                            let resultDict: [String: Any] = [
                                "modelNum": systemInfoModel?.modelNum ?? "",
                                "hardwareRevision": systemInfoModel?.hardwareRevision ?? "",
                                "firmwareRevision": systemInfoModel?.firmwareRevision ?? ""
                            ]
                            resolve(resultDict as NSDictionary)
                        } else {
                            reject(self.getTTGatewayErrorCode(status.rawValue))
                        }
                    }
                } else {
                    reject(self.getTTGatewayErrorCode(status.rawValue))
                }
            }
        } else {
            TTGateway.initializeGateway(withInfoDic: paramDict) { systemInfoModel, status in
                if status == .success {
                    let resultDict: [String: Any] = [
                        "modelNum": systemInfoModel?.modelNum ?? "",
                        "hardwareRevision": systemInfoModel?.hardwareRevision ?? "",
                        "firmwareRevision": systemInfoModel?.firmwareRevision ?? ""
                    ]
                    resolve(resultDict as NSDictionary)
                } else {
                    reject(self.getTTGatewayErrorCode(status.rawValue))
                }
            }
        }
    }

    // MARK: - Remote Key

    public func startScanRemoteKey() {
        TTWirelessKeyFob.startScan { model in
            var data: [String: Any] = [:]
            data["remoteKeyName"] = model.keyFobName ?? ""
            data["rssi"] = model.rssi
            data["remoteKeyMac"] = model.keyFobMac ?? ""
            self.sendEvent(EVENT_SCAN_REMOTE_KEY, body: data)
        }
    }

    public func stopScanRemoteKey() {
        TTWirelessKeyFob.stopScan()
    }

    public func initRemoteKey(remoteMac: String, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTWirelessKeyFob.newInitialize(withKeyFobMac: remoteMac, lockData: lockData) { status, electricQuantity, systemModel in
            if status == .success {
                resolve([electricQuantity, self.dictionaryToJson(self.dictionaryFromModel(systemModel))] as NSArray)
            } else {
                self.responseFail(.REMOTE_KEY, code: status.rawValue, errorMessage: nil, reject: reject)
            }
        }
    }

    public func getRemoteKeySystemInfo(remoteMac: String, resolve: @escaping (NSDictionary) -> Void, reject: @escaping (Int, String) -> Void) {
        // This method might need to be implemented based on TTLock SDK
        // For now, returning empty dict
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

    public func initDoorSensor(doorSensorMac: String, lockData: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTDoorSensor.initialize(withDoorSensorMac: doorSensorMac, lockData: lockData, success: { electricQuantity, systemModel in
            resolve([electricQuantity, self.dictionaryToJson(self.dictionaryFromModel(systemModel))] as NSArray)
        }, failure: { error in
            self.responseFail(.DOOR_SENSOR, code: error.rawValue, errorMessage: nil, reject: reject)
        })
    }

    // MARK: - Wireless Keypad

    public func startScanWirelessKeypad() {
        TTWirelessKeypad.startScanKeypad { model in
            var data: [String: Any] = [:]
            data["name"] = model.keypadName ?? ""
            data["rssi"] = model.rssi
            data["mac"] = model.keypadMac ?? ""
            self.sendEvent(EVENT_SCAN_WIRELESS_KEYPAD, body: data)
        }
    }

    public func stopScanWirelessKeypad() {
        TTWirelessKeypad.stopScanKeypad()
    }

    public func initWirelessKeypad(keypadMac: String, lockMac: String, resolve: @escaping (NSArray) -> Void, reject: @escaping (Int, String) -> Void) {
        TTWirelessKeypad.initializeKeypad(withKeypadMac: keypadMac, lockMac: lockMac) { wirelessKeypadFeatureValue, status, electricQuantity in
            if status == .success {
                resolve([electricQuantity, wirelessKeypadFeatureValue ?? ""] as NSArray)
            } else {
                self.responseFail(.REMOTE_KEY_PAD, code: status.rawValue, errorMessage: nil, reject: reject)
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
