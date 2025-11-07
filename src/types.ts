export interface ScanLockModal {
  lockName: string;
  lockMac: string;
  isInited: boolean;
  isKeyboardActivated: boolean;
  electricQuantity: number;
  lockVersion: string;
  lockSwitchState: number;
  rssi: number;
  oneMeterRSSI: number;
}

export interface ScanGatewayModal {
  gatewayName: string;
  gatewayMac: string;
  isDfuMode: boolean;
  rssi: number;
  type: number;
}

export interface WifiLockServerInfo {
  type: number; // staticIp: 0, dhcp: 1
  ipAddress: string | undefined;
  subnetMask: string | undefined;
  router: string | undefined;
  preferredDns: string | undefined;
  alternateDns: string | undefined;
}

export interface ScanRemoteKeyModal {
  remoteKeyName: string;
  remoteKeyMac: string;
  rssi: number;
}

export interface ScanDoorSensorModal {
  name: string;
  mac: string;
  rssi: number;
  scanTime: number;
}

export interface ScanWirelessKeypadModal {
  name: string;
  mac: string;
  rssi: number;
}

export interface LockVersion {
  protocolVersion: string;
  protocolType: string;
  groupId: string;
  orgId: string;
  scene: string;
}

export interface ScanWifiModal {
  wifi: string;
  rssi: number;
}

export interface InitGatewayParam {
  type: number;
  gatewayName: string;
  wifi: string | undefined;
  wifiPassword: string | undefined;
  ttLockUid: number;
  ttLockLoginPassword: string;
  serverIp: string | undefined;
  serverPort: number | undefined;
  ipSettingType: number | undefined;
  ipAddress: string | undefined;
  subnetMask: string | undefined;
  router: string | undefined;
  preferredDns: string | undefined;
  alternateDns: string | undefined;
}

export interface InitLockParam {
  lockMac: string;
  clientPara?: string;
}

export interface NumberStringPair {
  first: number;
  second: string;
}

export interface NumberNumberPair {
  first: number;
  second: number;
}

export interface NumberNumberNumberTriple {
  first: number;
  second: number;
  third: number;
}

export interface NumberBooleanPair {
  first: number;
  second: boolean;
}

export interface StringNumberPair {
  first: string;
  second: number;
}

export interface InitGatewayModal {
  modelNum: string;
  hardwareRevision: string;
  firmwareRevision: string;
}

export interface DeviceSystemModal {
  modelNum: string;
  hardwareRevision: string;
  firmwareRevision: string;
  //NB IOT LOCK
  nbOperator: string;
  nbNodeId: string;
  nbCardNumber: string;
  nbRssi: string;
  //support TTLockFeatureValuePasscodeKeyNumber
  passcodeKeyNumber: string;
  lockData: string;
}

export interface CycleDateParam {
  weekDay: number;
  startTime: number;
  endTime: number;
}

export enum BluetoothState {
  Unknown = 0,
  Resetting = 1,
  Unsupported = 2,
  Unauthorized = 3,
  On = 4,
  Off = 5,
}

export enum LockFunction {
  Passcode = 0,
  IcCard = 1,
  Fingerprint = 2,
  Wristband = 3,
  AutoLock = 4,
  DeletePasscode = 5,
  ManagePasscode = 7,
  Locking = 8,
  PasscodeVisible = 9,
  GatewayUnlock = 10,
  LockFreeze = 11,
  CyclePassword = 12,
  RemoteUnlockSwitch = 14,
  AudioSwitch = 15,
  NbIot = 16,
  GetAdminPasscode = 18,
  HotelCard = 19,
  NoClock = 20,
  NoBroadcastInNormal = 21,
  PassageMode = 22,
  TurnOffAutoLock = 23,
  WirelessKeypad = 24,
  Light = 25,
  HotelCardBlacklist = 26,
  IdentityCard = 27,
  TamperAlert = 28,
  ResetButton = 29,
  PrivacyLock = 30,
  DeadLock = 32,
  CyclicCardOrFingerprint = 34,
  UnlockDirection = 36,
  FingerVein = 37,
  NbAwake = 39,
  RecoverCyclePasscode = 40,
  RemoteKey = 41,
  GetAccessoryElectricQuantity = 42,
  SoundVolume = 43,
  QRCode = 44,
  SensorState = 45,
  PassageModeAutoUnlock = 46,
  DoorSensor = 50,
  DoorSensorAlert = 51,
  Sensitivity = 52,
  Face = 53,
  CpuCard = 55,
  Wifi = 56,
  WifiStaticIP = 58,
  PasscodeKeyNumber = 60,
  AutoSetUnlockDirection = 81,
}

export enum LockRecordType {
  Latest = 0,
  All = 1,
}

export enum LockConfigType {
  Audio = 0,
  PasscodeVisible = 1,
  Freeze = 2,
  TamperAlert = 3,
  ResetButton = 4,
  PrivacyLock = 5,
  PassageModeAutoUnlock = 6,
  WifiPowerSavingMode = 7,
  DoubleAuth = 8,
  PublicMode = 9,
  LowBatteryAutoUnlock = 10,
}

export enum LockSoundVolume {
  On = -1,
  Off = 0,
  Level_1 = 1,
  Level_2 = 2,
  Level_3 = 3,
  Level_4 = 4,
  Level_5 = 5,
}

export enum LockUnlockDirection {
  Unknown = 0,
  Left = 1,
  Right = 2,
}

export enum LockPassageMode {
  Weekly = 0,
  Monthly = 1,
}

export enum LockControlType {
  Unlock = 0,
  Lock = 1,
}

export enum LockState {
  Locked = 0,
  Unlock = 1,
  Unknown = 2,
  CarOnLock = 3,
}

export enum FaceState {
  canAddFace = 0,
  addFail = 1,
}

export enum FaceErrorCode {
  normal = 0,
  noFaceDetected = 1,
  tooCloseToTheTop = 2,
  tooCloseToTheBottom = 3,
  tooCloseToTheLeft = 4,
  tooCloseToTheRight = 5,
  tooFarAway = 6,
  tooClose = 7,
  eyebrowsCovered = 8,
  eyesCovered = 9,
  faceCovered = 10,
  wrongFaceDirection = 11,
  eyeOpeningDetected = 12,
  eyesClosedStatus = 13,
  failedToDetectEye = 14,
  needTurnHeadToLeft = 15,
  needTurnHeadToRight = 16,
  needRaiseHead = 17,
  needLowerHead = 18,
  needTiltHeadToLeft = 19,
  needTiltHeadToRight = 20,
}

export enum LockErrorCode {
  hadReset = 0,
  crcError = 1,
  noPermission = 2,
  wrongAdminCode = 3,
  lackOfStorageSpace = 4,
  inSettingMode = 5,
  noAdmin = 6,
  notInSettingMode = 7,
  wrongDynamicCode = 8,
  isNoPower = 9,
  resetPasscode = 10,
  updatePasscodeIndex = 11,
  invalidLockFlagPos = 12,
  eKeyExpired = 13,
  passcodeLengthInvalid = 14,
  samePasscode = 15,
  eKeyInactive = 16,
  aesKey = 17,
  fail = 18,
  passcodeExist = 19,
  passcodeNotExist = 20,
  lackOfStorageSpaceWhenAddingPasscode = 21,
  invalidParaLength = 22,
  cardNotExist = 23,
  fingerprintDuplication = 24,
  fingerprintNotExist = 25,
  invalidCommand = 26,
  inFreezeMode = 27,
  invalidClientPara = 28,
  lockIsLocked = 29,
  recordNotExist = 30,
  wrongSSID = 31,
  wrongWifiPassword = 32,
  bluetoothPoweredOff = 33,
  connectionTimeout = 34,
  disconnection = 35,
  lockIsBusy = 36,
  wrongLockData = 37,
  invalidParameter = 38,
}

export enum DoorSensorErrorCode {
  bluetoothPowerOff = 0,
  connectTimeout = 1,
  fail = 2,
  wrongCRC = 3,
}

export enum RemoteKeyErrorCode {
  fail = 0,
  wrongCRC = 1,
  connectTimeout = 2,
}

export enum RemoteKeyPadErrorCode {
  fail = 0,
  wrongCRC = 1,
  connectTimeout = 2,
  wrongFactoryDate = 3,
}

export enum GatewayErrorCode {
  fail = 0,
  wrongSSID = 1,
  wrongWifiPassword = 2,
  wrongCRC = 3,
  wrongAesKey = 4,
  notConnect = 5,
  disconnect = 6,
  failConfigRouter = 7,
  failConfigServer = 8,
  failConfigAccount = 9,
  noSIM = 10,
  invalidCommand = 11,
  failConfigIP = 12,
  failInvalidIP = 13,
}

export enum ConnectState {
  Timeout = 0,
  Success = 1,
  Fail = 2,
}

export enum TTLockEvent {
  ScanLock = 'EventScanLock',
  AddCardProgress = 'EventAddCardProgrress',
  AddFingerprintProgress = 'EventAddFingerprintProgrress',
  AddFaceProgress = 'EventAddFaceProgrress',
  ListenBluetoothState = 'EventBluetoothState',
  ScanLockWifi = 'EventScanLockWifi',
}

export enum TtRemoteKeyEvent {
  ScanRemoteKey = 'EventScanRemoteKey',
}

export enum TtDoorSensorEvent {
  ScanDoorSensor = 'EventScanDoorSensor',
}

export enum GatewayEvent {
  ScanGateway = 'EventScanGateway',
  ScanWifi = 'EventScanWifi',
}

export enum WirelessKeypadEvent {
  ScanWirelessKeypad = 'EventWirelessKeypad',
}

export enum GatewayType {
  G2 = 2,
  G3 = 3,
  G4 = 4,
  G5 = 5,
  G6 = 6,
}

export enum LockAccessoryType {
  KEYPAD = 1,
  REMOTE_KEY = 2,
  DOOR_SENSOR = 3,
}

export enum GatewayIpSettingType {
  STATIC_IP = 0,
  DHCP = 1,
}

export enum LiftWorkMode {
  ACTIVATE_ALL_FLOORS = 0,
  ACTIVATE_SPECIFIC_FLOORS = 1,
}

