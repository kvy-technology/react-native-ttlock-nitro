import type { AnyMap, HybridObject } from 'react-native-nitro-modules';
import type {
  InitGatewayParam,
  InitGatewayModal,
  DeviceSystemModal,
  WifiLockServerInfo,
  CycleDateParam,
  InitLockParam,
  NumberStringPair,
  NumberNumberPair,
  NumberNumberNumberTriple,
  NumberBooleanPair,
  StringNumberPair,
} from './types';

export interface TtlockNitro
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Wireless Keypad
  startScanWirelessKeypad(): void;
  stopScanWirelessKeypad(): void;
  initWirelessKeypad(
    keypadMac: string,
    lockMac: string,
    resolve: (data: NumberStringPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Door Sensor
  startScanDoorSensor(): void;
  stopScanDoorSensor(): void;
  initDoorSensor(
    doorSensorMac: string,
    lockData: string,
    resolve: (data: NumberStringPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Remote Key
  startScanRemoteKey(): void;
  stopScanRemoteKey(): void;
  initRemoteKey(
    remoteMac: string,
    lockData: string,
    resolve: (data: NumberStringPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getRemoteKeySystemInfo(
    remoteMac: string,
    resolve: (data: DeviceSystemModal) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Gateway
  startScanGateway(): void;
  stopScanGateway(): void;
  connect(mac: string, resolve: (state: number) => void): void;
  getNearbyWifi(resolve: (state: number) => void): void;
  initGateway(
    params: InitGatewayParam,
    resolve: (data: InitGatewayModal) => void,
    reject: (errorCode: number) => void
  ): void;

  // Lock Operations
  startScan(): void;
  stopScan(): void;
  initLock(
    params: InitLockParam,
    resolve: (lockData: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getLockVersionWithLockMac(
    lockMac: string,
    resolve: (lockVersion: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getAccessoryElectricQuantity(
    accessoryType: number,
    accessoryMac: string,
    lockData: string,
    resolve: (data: NumberNumberPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  resetLock(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  resetEkey(
    lockData: string,
    resolve: (lockData: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  controlLock(
    controlAction: number,
    lockData: string,
    resolve: (data: NumberNumberNumberTriple) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Passcode Operations
  createCustomPasscode(
    passcode: string,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  recoverPasscode(
    passcode: string,
    passcodeType: number,
    cycleType: number,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  modifyPasscode(
    passcodeOrigin: string,
    passcodeNew: string,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  deletePasscode(
    passcode: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  resetPasscode(
    lockData: string,
    resolve: (lockData: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Lock Status
  getLockSwitchState(
    lockData: string,
    resolve: (state: number) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Card Operations
  addCard(
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: (cardNumber: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  recoverCard(
    cardNumber: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  modifyCardValidityPeriod(
    cardNumber: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  deleteCard(
    cardNumber: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearAllCards(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Fingerprint Operations
  addFingerprint(
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: (fingerprintNumber: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  modifyFingerprintValidityPeriod(
    fingerprintNumber: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  deleteFingerprint(
    fingerprintNumber: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearAllFingerprints(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Admin Passcode
  modifyAdminPasscode(
    adminPasscode: string,
    lockData: string,
    resolve: (passcode: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Lock Time
  setLockTime(
    timestamp: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getLockTime(
    lockData: string,
    resolve: (lockTimestamp: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Lock System Info
  getLockSystem(
    lockData: string,
    resolve: (systemInfo: DeviceSystemModal) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getLockElectricQuantity(
    lockData: string,
    resolve: (electricQuantity: number) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Operation Record
  getLockOperationRecord(
    type: number,
    lockData: string,
    resolve: (records: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Automatic Locking
  getLockAutomaticLockingPeriodicTime(
    lockData: string,
    resolve: (data: NumberNumberNumberTriple) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setLockAutomaticLockingPeriodicTime(
    seconds: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Remote Unlock Switch
  getLockRemoteUnlockSwitchState(
    lockData: string,
    resolve: (isOn: boolean) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setLockRemoteUnlockSwitchState(
    isOn: boolean,
    lockData: string,
    resolve: (lockData: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Lock Config
  getLockConfig(
    config: number,
    lockData: string,
    resolve: (data: NumberBooleanPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setLockConfig(
    config: number,
    isOn: boolean,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Sound Volume
  setLockSoundVolume(
    soundVolumeValue: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getLockSoundVolume(
    lockData: string,
    resolve: (soundVolumeValue: number) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Unlock Direction
  getUnlockDirection(
    lockData: string,
    resolve: (direction: number) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setUnlockDirection(
    direction: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setUnlockDirectionAutomatic(
    lockData: string,
    resolve: (direction: number) => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Passage Mode
  addPassageMode(
    type: number,
    weekly: number[] | null,
    monthly: number[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearAllPassageModes(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Remote Key
  addRemoteKey(
    remoteMac: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  modifyRemoteKey(
    remoteMac: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  deleteRemoteKey(
    remoteMac: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearAllRemoteKey(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Door Sensor
  addDoorSensor(
    doorSensorMac: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearAllDoorSensor(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setDoorSensorAlertTime(
    time: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Wifi Operations
  scanWifi(
    lockData: string,
    reject: (errorCode: number, description: string) => void
  ): void;
  configWifi(
    wifiName: string,
    wifiPassword: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  configServer(
    ip: string,
    port: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  getWifiInfo(
    lockData: string,
    resolve: (data: StringNumberPair) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  configIp(
    info: WifiLockServerInfo,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Wifi Power Saving
  getWifiPowerSavingTime(
    lockData: string,
    resolve: (powerSavingData: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  configWifiPowerSavingTime(
    days: number[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearWifiPowerSavingTime(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Face Operations
  addFace(
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: (faceNumber: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  addFaceFeatureData(
    faceFeatureData: string,
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    lockData: string,
    resolve: (faceNumber: string) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  modifyFaceValidityPeriod(
    cycleList: CycleDateParam[] | null,
    startDate: number,
    endDate: number,
    faceNumber: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  deleteFace(
    faceNumber: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  clearFace(
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Lift Operations
  activateLiftFloors(
    floors: string,
    lockData: string,
    resolve: (data: NumberNumberNumberTriple) => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setLiftControlEnableFloors(
    floors: string,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;
  setLiftWorkMode(
    workMode: number,
    lockData: string,
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ): void;

  // Utility
  getBluetoothState(resolve: (state: number) => void): void;
  supportFunction(
    lockFunction: number,
    lockData: string,
    resolve: (isSupport: boolean) => void
  ): void;

  // Event Listeners
  addListener(
    eventName: string,
    listener: (eventName: string, data: AnyMap | undefined) => void
  ): void;
  removeListener(
    eventName: string,
    listener: (eventName: string, data: AnyMap | undefined) => void
  ): void;
}
