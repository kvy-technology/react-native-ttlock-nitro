"use strict";

export let BluetoothState = /*#__PURE__*/function (BluetoothState) {
  BluetoothState[BluetoothState["Unknown"] = 0] = "Unknown";
  BluetoothState[BluetoothState["Resetting"] = 1] = "Resetting";
  BluetoothState[BluetoothState["Unsupported"] = 2] = "Unsupported";
  BluetoothState[BluetoothState["Unauthorized"] = 3] = "Unauthorized";
  BluetoothState[BluetoothState["On"] = 4] = "On";
  BluetoothState[BluetoothState["Off"] = 5] = "Off";
  return BluetoothState;
}({});
export let LockFunction = /*#__PURE__*/function (LockFunction) {
  LockFunction[LockFunction["Passcode"] = 0] = "Passcode";
  LockFunction[LockFunction["IcCard"] = 1] = "IcCard";
  LockFunction[LockFunction["Fingerprint"] = 2] = "Fingerprint";
  LockFunction[LockFunction["Wristband"] = 3] = "Wristband";
  LockFunction[LockFunction["AutoLock"] = 4] = "AutoLock";
  LockFunction[LockFunction["DeletePasscode"] = 5] = "DeletePasscode";
  LockFunction[LockFunction["ManagePasscode"] = 7] = "ManagePasscode";
  LockFunction[LockFunction["Locking"] = 8] = "Locking";
  LockFunction[LockFunction["PasscodeVisible"] = 9] = "PasscodeVisible";
  LockFunction[LockFunction["GatewayUnlock"] = 10] = "GatewayUnlock";
  LockFunction[LockFunction["LockFreeze"] = 11] = "LockFreeze";
  LockFunction[LockFunction["CyclePassword"] = 12] = "CyclePassword";
  LockFunction[LockFunction["RemoteUnlockSwitch"] = 14] = "RemoteUnlockSwitch";
  LockFunction[LockFunction["AudioSwitch"] = 15] = "AudioSwitch";
  LockFunction[LockFunction["NbIot"] = 16] = "NbIot";
  LockFunction[LockFunction["GetAdminPasscode"] = 18] = "GetAdminPasscode";
  LockFunction[LockFunction["HotelCard"] = 19] = "HotelCard";
  LockFunction[LockFunction["NoClock"] = 20] = "NoClock";
  LockFunction[LockFunction["NoBroadcastInNormal"] = 21] = "NoBroadcastInNormal";
  LockFunction[LockFunction["PassageMode"] = 22] = "PassageMode";
  LockFunction[LockFunction["TurnOffAutoLock"] = 23] = "TurnOffAutoLock";
  LockFunction[LockFunction["WirelessKeypad"] = 24] = "WirelessKeypad";
  LockFunction[LockFunction["Light"] = 25] = "Light";
  LockFunction[LockFunction["HotelCardBlacklist"] = 26] = "HotelCardBlacklist";
  LockFunction[LockFunction["IdentityCard"] = 27] = "IdentityCard";
  LockFunction[LockFunction["TamperAlert"] = 28] = "TamperAlert";
  LockFunction[LockFunction["ResetButton"] = 29] = "ResetButton";
  LockFunction[LockFunction["PrivacyLock"] = 30] = "PrivacyLock";
  LockFunction[LockFunction["DeadLock"] = 32] = "DeadLock";
  LockFunction[LockFunction["CyclicCardOrFingerprint"] = 34] = "CyclicCardOrFingerprint";
  LockFunction[LockFunction["UnlockDirection"] = 36] = "UnlockDirection";
  LockFunction[LockFunction["FingerVein"] = 37] = "FingerVein";
  LockFunction[LockFunction["NbAwake"] = 39] = "NbAwake";
  LockFunction[LockFunction["RecoverCyclePasscode"] = 40] = "RecoverCyclePasscode";
  LockFunction[LockFunction["RemoteKey"] = 41] = "RemoteKey";
  LockFunction[LockFunction["GetAccessoryElectricQuantity"] = 42] = "GetAccessoryElectricQuantity";
  LockFunction[LockFunction["SoundVolume"] = 43] = "SoundVolume";
  LockFunction[LockFunction["QRCode"] = 44] = "QRCode";
  LockFunction[LockFunction["SensorState"] = 45] = "SensorState";
  LockFunction[LockFunction["PassageModeAutoUnlock"] = 46] = "PassageModeAutoUnlock";
  LockFunction[LockFunction["DoorSensor"] = 50] = "DoorSensor";
  LockFunction[LockFunction["DoorSensorAlert"] = 51] = "DoorSensorAlert";
  LockFunction[LockFunction["Sensitivity"] = 52] = "Sensitivity";
  LockFunction[LockFunction["Face"] = 53] = "Face";
  LockFunction[LockFunction["CpuCard"] = 55] = "CpuCard";
  LockFunction[LockFunction["Wifi"] = 56] = "Wifi";
  LockFunction[LockFunction["WifiStaticIP"] = 58] = "WifiStaticIP";
  LockFunction[LockFunction["PasscodeKeyNumber"] = 60] = "PasscodeKeyNumber";
  LockFunction[LockFunction["AutoSetUnlockDirection"] = 81] = "AutoSetUnlockDirection";
  return LockFunction;
}({});
export let LockRecordType = /*#__PURE__*/function (LockRecordType) {
  LockRecordType[LockRecordType["Latest"] = 0] = "Latest";
  LockRecordType[LockRecordType["All"] = 1] = "All";
  return LockRecordType;
}({});
export let LockConfigType = /*#__PURE__*/function (LockConfigType) {
  LockConfigType[LockConfigType["Audio"] = 0] = "Audio";
  LockConfigType[LockConfigType["PasscodeVisible"] = 1] = "PasscodeVisible";
  LockConfigType[LockConfigType["Freeze"] = 2] = "Freeze";
  LockConfigType[LockConfigType["TamperAlert"] = 3] = "TamperAlert";
  LockConfigType[LockConfigType["ResetButton"] = 4] = "ResetButton";
  LockConfigType[LockConfigType["PrivacyLock"] = 5] = "PrivacyLock";
  LockConfigType[LockConfigType["PassageModeAutoUnlock"] = 6] = "PassageModeAutoUnlock";
  LockConfigType[LockConfigType["WifiPowerSavingMode"] = 7] = "WifiPowerSavingMode";
  LockConfigType[LockConfigType["DoubleAuth"] = 8] = "DoubleAuth";
  LockConfigType[LockConfigType["PublicMode"] = 9] = "PublicMode";
  LockConfigType[LockConfigType["LowBatteryAutoUnlock"] = 10] = "LowBatteryAutoUnlock";
  return LockConfigType;
}({});
export let LockSoundVolume = /*#__PURE__*/function (LockSoundVolume) {
  LockSoundVolume[LockSoundVolume["On"] = -1] = "On";
  LockSoundVolume[LockSoundVolume["Off"] = 0] = "Off";
  LockSoundVolume[LockSoundVolume["Level_1"] = 1] = "Level_1";
  LockSoundVolume[LockSoundVolume["Level_2"] = 2] = "Level_2";
  LockSoundVolume[LockSoundVolume["Level_3"] = 3] = "Level_3";
  LockSoundVolume[LockSoundVolume["Level_4"] = 4] = "Level_4";
  LockSoundVolume[LockSoundVolume["Level_5"] = 5] = "Level_5";
  return LockSoundVolume;
}({});
export let LockUnlockDirection = /*#__PURE__*/function (LockUnlockDirection) {
  LockUnlockDirection[LockUnlockDirection["Unknown"] = 0] = "Unknown";
  LockUnlockDirection[LockUnlockDirection["Left"] = 1] = "Left";
  LockUnlockDirection[LockUnlockDirection["Right"] = 2] = "Right";
  return LockUnlockDirection;
}({});
export let LockPassageMode = /*#__PURE__*/function (LockPassageMode) {
  LockPassageMode[LockPassageMode["Weekly"] = 0] = "Weekly";
  LockPassageMode[LockPassageMode["Monthly"] = 1] = "Monthly";
  return LockPassageMode;
}({});
export let LockControlType = /*#__PURE__*/function (LockControlType) {
  LockControlType[LockControlType["Unlock"] = 0] = "Unlock";
  LockControlType[LockControlType["Lock"] = 1] = "Lock";
  return LockControlType;
}({});
export let LockState = /*#__PURE__*/function (LockState) {
  LockState[LockState["Locked"] = 0] = "Locked";
  LockState[LockState["Unlock"] = 1] = "Unlock";
  LockState[LockState["Unknown"] = 2] = "Unknown";
  LockState[LockState["CarOnLock"] = 3] = "CarOnLock";
  return LockState;
}({});
export let FaceState = /*#__PURE__*/function (FaceState) {
  FaceState[FaceState["canAddFace"] = 0] = "canAddFace";
  FaceState[FaceState["addFail"] = 1] = "addFail";
  return FaceState;
}({});
export let FaceErrorCode = /*#__PURE__*/function (FaceErrorCode) {
  FaceErrorCode[FaceErrorCode["normal"] = 0] = "normal";
  FaceErrorCode[FaceErrorCode["noFaceDetected"] = 1] = "noFaceDetected";
  FaceErrorCode[FaceErrorCode["tooCloseToTheTop"] = 2] = "tooCloseToTheTop";
  FaceErrorCode[FaceErrorCode["tooCloseToTheBottom"] = 3] = "tooCloseToTheBottom";
  FaceErrorCode[FaceErrorCode["tooCloseToTheLeft"] = 4] = "tooCloseToTheLeft";
  FaceErrorCode[FaceErrorCode["tooCloseToTheRight"] = 5] = "tooCloseToTheRight";
  FaceErrorCode[FaceErrorCode["tooFarAway"] = 6] = "tooFarAway";
  FaceErrorCode[FaceErrorCode["tooClose"] = 7] = "tooClose";
  FaceErrorCode[FaceErrorCode["eyebrowsCovered"] = 8] = "eyebrowsCovered";
  FaceErrorCode[FaceErrorCode["eyesCovered"] = 9] = "eyesCovered";
  FaceErrorCode[FaceErrorCode["faceCovered"] = 10] = "faceCovered";
  FaceErrorCode[FaceErrorCode["wrongFaceDirection"] = 11] = "wrongFaceDirection";
  FaceErrorCode[FaceErrorCode["eyeOpeningDetected"] = 12] = "eyeOpeningDetected";
  FaceErrorCode[FaceErrorCode["eyesClosedStatus"] = 13] = "eyesClosedStatus";
  FaceErrorCode[FaceErrorCode["failedToDetectEye"] = 14] = "failedToDetectEye";
  FaceErrorCode[FaceErrorCode["needTurnHeadToLeft"] = 15] = "needTurnHeadToLeft";
  FaceErrorCode[FaceErrorCode["needTurnHeadToRight"] = 16] = "needTurnHeadToRight";
  FaceErrorCode[FaceErrorCode["needRaiseHead"] = 17] = "needRaiseHead";
  FaceErrorCode[FaceErrorCode["needLowerHead"] = 18] = "needLowerHead";
  FaceErrorCode[FaceErrorCode["needTiltHeadToLeft"] = 19] = "needTiltHeadToLeft";
  FaceErrorCode[FaceErrorCode["needTiltHeadToRight"] = 20] = "needTiltHeadToRight";
  return FaceErrorCode;
}({});
export let LockErrorCode = /*#__PURE__*/function (LockErrorCode) {
  LockErrorCode[LockErrorCode["hadReset"] = 0] = "hadReset";
  LockErrorCode[LockErrorCode["crcError"] = 1] = "crcError";
  LockErrorCode[LockErrorCode["noPermission"] = 2] = "noPermission";
  LockErrorCode[LockErrorCode["wrongAdminCode"] = 3] = "wrongAdminCode";
  LockErrorCode[LockErrorCode["lackOfStorageSpace"] = 4] = "lackOfStorageSpace";
  LockErrorCode[LockErrorCode["inSettingMode"] = 5] = "inSettingMode";
  LockErrorCode[LockErrorCode["noAdmin"] = 6] = "noAdmin";
  LockErrorCode[LockErrorCode["notInSettingMode"] = 7] = "notInSettingMode";
  LockErrorCode[LockErrorCode["wrongDynamicCode"] = 8] = "wrongDynamicCode";
  LockErrorCode[LockErrorCode["isNoPower"] = 9] = "isNoPower";
  LockErrorCode[LockErrorCode["resetPasscode"] = 10] = "resetPasscode";
  LockErrorCode[LockErrorCode["updatePasscodeIndex"] = 11] = "updatePasscodeIndex";
  LockErrorCode[LockErrorCode["invalidLockFlagPos"] = 12] = "invalidLockFlagPos";
  LockErrorCode[LockErrorCode["eKeyExpired"] = 13] = "eKeyExpired";
  LockErrorCode[LockErrorCode["passcodeLengthInvalid"] = 14] = "passcodeLengthInvalid";
  LockErrorCode[LockErrorCode["samePasscode"] = 15] = "samePasscode";
  LockErrorCode[LockErrorCode["eKeyInactive"] = 16] = "eKeyInactive";
  LockErrorCode[LockErrorCode["aesKey"] = 17] = "aesKey";
  LockErrorCode[LockErrorCode["fail"] = 18] = "fail";
  LockErrorCode[LockErrorCode["passcodeExist"] = 19] = "passcodeExist";
  LockErrorCode[LockErrorCode["passcodeNotExist"] = 20] = "passcodeNotExist";
  LockErrorCode[LockErrorCode["lackOfStorageSpaceWhenAddingPasscode"] = 21] = "lackOfStorageSpaceWhenAddingPasscode";
  LockErrorCode[LockErrorCode["invalidParaLength"] = 22] = "invalidParaLength";
  LockErrorCode[LockErrorCode["cardNotExist"] = 23] = "cardNotExist";
  LockErrorCode[LockErrorCode["fingerprintDuplication"] = 24] = "fingerprintDuplication";
  LockErrorCode[LockErrorCode["fingerprintNotExist"] = 25] = "fingerprintNotExist";
  LockErrorCode[LockErrorCode["invalidCommand"] = 26] = "invalidCommand";
  LockErrorCode[LockErrorCode["inFreezeMode"] = 27] = "inFreezeMode";
  LockErrorCode[LockErrorCode["invalidClientPara"] = 28] = "invalidClientPara";
  LockErrorCode[LockErrorCode["lockIsLocked"] = 29] = "lockIsLocked";
  LockErrorCode[LockErrorCode["recordNotExist"] = 30] = "recordNotExist";
  LockErrorCode[LockErrorCode["wrongSSID"] = 31] = "wrongSSID";
  LockErrorCode[LockErrorCode["wrongWifiPassword"] = 32] = "wrongWifiPassword";
  LockErrorCode[LockErrorCode["bluetoothPoweredOff"] = 33] = "bluetoothPoweredOff";
  LockErrorCode[LockErrorCode["connectionTimeout"] = 34] = "connectionTimeout";
  LockErrorCode[LockErrorCode["disconnection"] = 35] = "disconnection";
  LockErrorCode[LockErrorCode["lockIsBusy"] = 36] = "lockIsBusy";
  LockErrorCode[LockErrorCode["wrongLockData"] = 37] = "wrongLockData";
  LockErrorCode[LockErrorCode["invalidParameter"] = 38] = "invalidParameter";
  return LockErrorCode;
}({});
export let DoorSensorErrorCode = /*#__PURE__*/function (DoorSensorErrorCode) {
  DoorSensorErrorCode[DoorSensorErrorCode["bluetoothPowerOff"] = 0] = "bluetoothPowerOff";
  DoorSensorErrorCode[DoorSensorErrorCode["connectTimeout"] = 1] = "connectTimeout";
  DoorSensorErrorCode[DoorSensorErrorCode["fail"] = 2] = "fail";
  DoorSensorErrorCode[DoorSensorErrorCode["wrongCRC"] = 3] = "wrongCRC";
  return DoorSensorErrorCode;
}({});
export let RemoteKeyErrorCode = /*#__PURE__*/function (RemoteKeyErrorCode) {
  RemoteKeyErrorCode[RemoteKeyErrorCode["fail"] = 0] = "fail";
  RemoteKeyErrorCode[RemoteKeyErrorCode["wrongCRC"] = 1] = "wrongCRC";
  RemoteKeyErrorCode[RemoteKeyErrorCode["connectTimeout"] = 2] = "connectTimeout";
  return RemoteKeyErrorCode;
}({});
export let RemoteKeyPadErrorCode = /*#__PURE__*/function (RemoteKeyPadErrorCode) {
  RemoteKeyPadErrorCode[RemoteKeyPadErrorCode["fail"] = 0] = "fail";
  RemoteKeyPadErrorCode[RemoteKeyPadErrorCode["wrongCRC"] = 1] = "wrongCRC";
  RemoteKeyPadErrorCode[RemoteKeyPadErrorCode["connectTimeout"] = 2] = "connectTimeout";
  RemoteKeyPadErrorCode[RemoteKeyPadErrorCode["wrongFactoryDate"] = 3] = "wrongFactoryDate";
  return RemoteKeyPadErrorCode;
}({});
export let GatewayErrorCode = /*#__PURE__*/function (GatewayErrorCode) {
  GatewayErrorCode[GatewayErrorCode["fail"] = 0] = "fail";
  GatewayErrorCode[GatewayErrorCode["wrongSSID"] = 1] = "wrongSSID";
  GatewayErrorCode[GatewayErrorCode["wrongWifiPassword"] = 2] = "wrongWifiPassword";
  GatewayErrorCode[GatewayErrorCode["wrongCRC"] = 3] = "wrongCRC";
  GatewayErrorCode[GatewayErrorCode["wrongAesKey"] = 4] = "wrongAesKey";
  GatewayErrorCode[GatewayErrorCode["notConnect"] = 5] = "notConnect";
  GatewayErrorCode[GatewayErrorCode["disconnect"] = 6] = "disconnect";
  GatewayErrorCode[GatewayErrorCode["failConfigRouter"] = 7] = "failConfigRouter";
  GatewayErrorCode[GatewayErrorCode["failConfigServer"] = 8] = "failConfigServer";
  GatewayErrorCode[GatewayErrorCode["failConfigAccount"] = 9] = "failConfigAccount";
  GatewayErrorCode[GatewayErrorCode["noSIM"] = 10] = "noSIM";
  GatewayErrorCode[GatewayErrorCode["invalidCommand"] = 11] = "invalidCommand";
  GatewayErrorCode[GatewayErrorCode["failConfigIP"] = 12] = "failConfigIP";
  GatewayErrorCode[GatewayErrorCode["failInvalidIP"] = 13] = "failInvalidIP";
  return GatewayErrorCode;
}({});
export let ConnectState = /*#__PURE__*/function (ConnectState) {
  ConnectState[ConnectState["Timeout"] = 0] = "Timeout";
  ConnectState[ConnectState["Success"] = 1] = "Success";
  ConnectState[ConnectState["Fail"] = 2] = "Fail";
  return ConnectState;
}({});
export let TTLockEvent = /*#__PURE__*/function (TTLockEvent) {
  TTLockEvent["ScanLock"] = "EventScanLock";
  TTLockEvent["AddCardProgress"] = "EventAddCardProgrress";
  TTLockEvent["AddFingerprintProgress"] = "EventAddFingerprintProgrress";
  TTLockEvent["AddFaceProgress"] = "EventAddFaceProgrress";
  TTLockEvent["ListenBluetoothState"] = "EventBluetoothState";
  TTLockEvent["ScanLockWifi"] = "EventScanLockWifi";
  return TTLockEvent;
}({});
export let TtRemoteKeyEvent = /*#__PURE__*/function (TtRemoteKeyEvent) {
  TtRemoteKeyEvent["ScanRemoteKey"] = "EventScanRemoteKey";
  return TtRemoteKeyEvent;
}({});
export let TtDoorSensorEvent = /*#__PURE__*/function (TtDoorSensorEvent) {
  TtDoorSensorEvent["ScanDoorSensor"] = "EventScanDoorSensor";
  return TtDoorSensorEvent;
}({});
export let GatewayEvent = /*#__PURE__*/function (GatewayEvent) {
  GatewayEvent["ScanGateway"] = "EventScanGateway";
  GatewayEvent["ScanWifi"] = "EventScanWifi";
  return GatewayEvent;
}({});
export let WirelessKeypadEvent = /*#__PURE__*/function (WirelessKeypadEvent) {
  WirelessKeypadEvent["ScanWirelessKeypad"] = "EventWirelessKeypad";
  return WirelessKeypadEvent;
}({});
export let GatewayType = /*#__PURE__*/function (GatewayType) {
  GatewayType[GatewayType["G2"] = 2] = "G2";
  GatewayType[GatewayType["G3"] = 3] = "G3";
  GatewayType[GatewayType["G4"] = 4] = "G4";
  GatewayType[GatewayType["G5"] = 5] = "G5";
  GatewayType[GatewayType["G6"] = 6] = "G6";
  return GatewayType;
}({});
export let LockAccessoryType = /*#__PURE__*/function (LockAccessoryType) {
  LockAccessoryType[LockAccessoryType["KEYPAD"] = 1] = "KEYPAD";
  LockAccessoryType[LockAccessoryType["REMOTE_KEY"] = 2] = "REMOTE_KEY";
  LockAccessoryType[LockAccessoryType["DOOR_SENSOR"] = 3] = "DOOR_SENSOR";
  return LockAccessoryType;
}({});
export let GatewayIpSettingType = /*#__PURE__*/function (GatewayIpSettingType) {
  GatewayIpSettingType[GatewayIpSettingType["STATIC_IP"] = 0] = "STATIC_IP";
  GatewayIpSettingType[GatewayIpSettingType["DHCP"] = 1] = "DHCP";
  return GatewayIpSettingType;
}({});
export let LiftWorkMode = /*#__PURE__*/function (LiftWorkMode) {
  LiftWorkMode[LiftWorkMode["ACTIVATE_ALL_FLOORS"] = 0] = "ACTIVATE_ALL_FLOORS";
  LiftWorkMode[LiftWorkMode["ACTIVATE_SPECIFIC_FLOORS"] = 1] = "ACTIVATE_SPECIFIC_FLOORS";
  return LiftWorkMode;
}({});
//# sourceMappingURL=types.js.map