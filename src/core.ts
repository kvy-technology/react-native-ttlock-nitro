
import { NitroModules } from 'react-native-nitro-modules';
import type { AnyMap } from 'react-native-nitro-modules';
import type { TtlockNitro } from './TtlockNitro.nitro';
import type {
  InitGatewayParam,
  InitGatewayModal,
  DeviceSystemModal,
  WifiLockServerInfo,
  CycleDateParam,
  NumberStringPair,
  NumberNumberPair,
  NumberNumberNumberTriple,
  NumberBooleanPair,
  StringNumberPair,
  ScanLockModal,
  ScanGatewayModal,
  ScanWifiModal,
  ScanRemoteKeyModal,
  ScanDoorSensorModal,
  ScanWirelessKeypadModal,
} from './types';
import {
  ConnectState,
  LockState,
  LockControlType,
  LockUnlockDirection,
  LockSoundVolume,
  LockConfigType,
  LockPassageMode,
  LockRecordType,
  LockAccessoryType,
  LiftWorkMode,
  BluetoothState,
  LockFunction,
  TTLockEvent,
  GatewayEvent,
  TtRemoteKeyEvent,
  TtDoorSensorEvent,
  WirelessKeypadEvent,
} from './types';

const TtlockNitroHybridObject =
  NitroModules.createHybridObject<TtlockNitro>('TtlockNitro');

// Event emitter wrapper for nitro module events
// Matches the nitro module interface: listener: (eventName: string, data: AnyMap | undefined) => void
class NitroEventEmitter {
  private listeners: Map<string, Set<(data: any) => void>> = new Map();
  private nativeListeners: Map<
    string,
    (eventName: string, data: AnyMap | undefined) => void
  > = new Map();

  addListener(eventName: string, callback: (data: any) => void): { remove: () => void } {
    if (!this.listeners.has(eventName)) {
      this.listeners.set(eventName, new Set());
      // Create native listener that matches nitro module signature
      // Signature: (eventName: string, data: AnyMap | undefined) => void
      const nativeListener = (eventName: string, data: AnyMap | undefined) => {
        const callbacks = this.listeners.get(eventName);
        if (callbacks) {
          // Dispatch to all registered callbacks, passing only the data
          callbacks.forEach((cb) => cb(data));
        }
      };
      this.nativeListeners.set(eventName, nativeListener);
      // Register with native module using the correct signature
      TtlockNitroHybridObject.addListener(eventName, nativeListener);
    }
    this.listeners.get(eventName)!.add(callback);

    return {
      remove: () => {
        const callbacks = this.listeners.get(eventName);
        if (callbacks) {
          callbacks.delete(callback);
          if (callbacks.size === 0) {
            // Remove native listener when no more callbacks exist
            const nativeListener = this.nativeListeners.get(eventName);
            if (nativeListener) {
              TtlockNitroHybridObject.removeListener(eventName, nativeListener);
              this.nativeListeners.delete(eventName);
            }
            this.listeners.delete(eventName);
          }
        }
      },
    };
  }
}

const ttLockEventEmitter = new NitroEventEmitter();

// Subscription map to track subscriptions
const subscriptionMap = new Map<string, any>();

// Helper function to convert callback to promise
function promisify<T>(
  fn: (
    resolve: (value: T) => void,
    reject: (errorCode: number, description: string) => void
  ) => void
): Promise<T> {
  return new Promise((resolve, reject) => {
    fn(
      (value) => resolve(value),
      (errorCode, description) => reject({ errorCode, description })
    );
  });
}

// Helper function for void promises
function promisifyVoid(
  fn: (
    resolve: () => void,
    reject: (errorCode: number, description: string) => void
  ) => void
): Promise<void> {
  return new Promise((resolve, reject) => {
    fn(
      () => resolve(),
      (errorCode, description) => reject({ errorCode, description })
    );
  });
}

// Wireless Keypad
export function startScanWirelessKeypad(
  callback?: (scanModal: ScanWirelessKeypadModal) => void
): void {
  const eventName = WirelessKeypadEvent.ScanWirelessKeypad;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
      callback(data as ScanWirelessKeypadModal);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanWirelessKeypad();
}

export function stopScanWirelessKeypad(): void {
  TtlockNitroHybridObject.stopScanWirelessKeypad();
  const subscription = subscriptionMap.get(
    WirelessKeypadEvent.ScanWirelessKeypad
  );
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(WirelessKeypadEvent.ScanWirelessKeypad);
}

export function initWirelessKeypad(
  keypadMac: string,
  lockMac: string
): Promise<[number, string]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initWirelessKeypad(
      keypadMac,
      lockMac,
      (data: NumberStringPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

// Door Sensor
export function startScanDoorSensor(
  callback?: (scanModal: ScanDoorSensorModal) => void
): void {
  const eventName = TtDoorSensorEvent.ScanDoorSensor;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
      callback(data as ScanDoorSensorModal);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanDoorSensor();
}

export function stopScanDoorSensor(): void {
  TtlockNitroHybridObject.stopScanDoorSensor();
  const subscription = subscriptionMap.get(TtDoorSensorEvent.ScanDoorSensor);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TtDoorSensorEvent.ScanDoorSensor);
}

export function initDoorSensor(
  doorSensorMac: string,
  lockData: string
): Promise<[number, string]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initDoorSensor(
      doorSensorMac,
      lockData,
      (data: NumberStringPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

// Remote Key
export function startScanRemoteKey(
  callback?: (scanModal: ScanRemoteKeyModal) => void
): void {
  const eventName = TtRemoteKeyEvent.ScanRemoteKey;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
      callback(data as ScanRemoteKeyModal);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanRemoteKey();
}

export function stopScanRemoteKey(): void {
  TtlockNitroHybridObject.stopScanRemoteKey();
  const subscription = subscriptionMap.get(TtRemoteKeyEvent.ScanRemoteKey);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TtRemoteKeyEvent.ScanRemoteKey);
}

export function initRemoteKey(
  remoteMac: string,
  lockData: string
): Promise<[number, string]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initRemoteKey(
      remoteMac,
      lockData,
      (data: NumberStringPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

export function getRemoteKeySystemInfo(
  remoteMac: string
): Promise<DeviceSystemModal> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getRemoteKeySystemInfo(remoteMac, resolve, reject);
  });
}

// Gateway
export function startScanGateway(
  callback?: (scanGatewayModal: ScanGatewayModal) => void
): void {
  const eventName = GatewayEvent.ScanGateway;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
      callback(data as ScanGatewayModal);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanGateway();
}

export function stopScanGateway(): void {
  TtlockNitroHybridObject.stopScanGateway();
  const subscription = subscriptionMap.get(GatewayEvent.ScanGateway);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(GatewayEvent.ScanGateway);
}

export function connectGateway(mac: string): Promise<ConnectState> {
  return new Promise((resolve) => {
    TtlockNitroHybridObject.connect(mac, (state: number) => {
      const states = [
        ConnectState.Timeout,
        ConnectState.Success,
        ConnectState.Fail,
      ];
      resolve(states[state] ?? ConnectState.Fail);
    });
  });
}

export function getNearbyWifi(
  progress?: (scanWifiModalList: ScanWifiModal[]) => void,
  finish?: () => void,
  fail?: (errorCode: number, description: string) => void
): Promise<void> {
  return new Promise((resolve, reject) => {
    const eventName = GatewayEvent.ScanWifi;
    let subscription: any;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
        progress(data as ScanWifiModal[]);
      });
    }
    TtlockNitroHybridObject.getNearbyWifi((state: number) => {
      if (subscription) {
        subscription.remove();
      }
      if (state === 0) {
        if (finish) finish();
        resolve();
      } else {
        if (fail) fail(state, 'Scan WiFi failed');
        reject({ errorCode: state, description: 'Scan WiFi failed' });
      }
    });
  });
}

export function initGateway(
  params: InitGatewayParam
): Promise<InitGatewayModal> {
  return new Promise((resolve, reject) => {
    TtlockNitroHybridObject.initGateway(params, resolve, (errorCode: number) =>
      reject({ errorCode, description: '' })
    );
  });
}

// Lock Operations
export function startScan(
  callback?: (scanLockModal: ScanLockModal) => void
): void {
  const eventName = TTLockEvent.ScanLock;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {

      callback(data as ScanLockModal);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScan();
}

export function stopScan(): void {
  TtlockNitroHybridObject.stopScan();
  const subscription = subscriptionMap.get(TTLockEvent.ScanLock);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TTLockEvent.ScanLock);
}

export function initLock(params: {
  lockMac: string;
  clientPara?: string;
}): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initLock(params, resolve, reject);
  });
}

export function getLockVersionWithLockMac(lockMac: string): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockVersionWithLockMac(lockMac, resolve, reject);
  });
}

export function getAccessoryElectricQuantity(
  accessoryType: LockAccessoryType,
  accessoryMac: string,
  lockData: string
): Promise<[number, number]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getAccessoryElectricQuantity(
      accessoryType,
      accessoryMac,
      lockData,
      (data: NumberNumberPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

export function resetLock(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.resetLock(lockData, resolve, reject);
  });
}

export function resetEkey(lockData: string): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.resetEkey(lockData, resolve, reject);
  });
}

export function controlLock(
  control: LockControlType,
  lockData: string
): Promise<[number, number, number]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.controlLock(
      control,
      lockData,
      (data: NumberNumberNumberTriple) =>
        {
          resolve([data.first, data.second, data.third]);
        },
      reject
    );
  });
}

// Passcode Operations
export function createCustomPasscode(
  passcode: string,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.createCustomPasscode(
      passcode,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function recoverPasscode(
  passcode: string,
  passcodeType: number,
  cycleType: number,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.recoverPasscode(
      passcode,
      passcodeType,
      cycleType,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function modifyPasscode(
  passcodeOrigin: string,
  passcodeNew: string,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyPasscode(
      passcodeOrigin,
      passcodeNew,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function deletePasscode(
  passcode: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deletePasscode(passcode, lockData, resolve, reject);
  });
}

export function resetPasscode(lockData: string): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.resetPasscode(lockData, resolve, reject);
  });
}

// Lock Status
export function getLockSwitchState(lockData: string): Promise<LockState> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSwitchState(
      lockData,
      (state: number) => {
        const states = [
          LockState.Locked,
          LockState.Unlock,
          LockState.Unknown,
          LockState.CarOnLock,
        ];
        resolve(states[state] ?? LockState.Unknown);
      },
      reject
    );
  });
}

// Card Operations
export function addCard(
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string,
  progress?: () => void
): Promise<string> {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddCardProgress;
    let subscription: any;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, progress);
    }
    TtlockNitroHybridObject.addCard(
      cycleList,
      startDate,
      endDate,
      lockData,
      (cardNumber: string) => {
        if (subscription) {
          subscription.remove();
        }
        resolve(cardNumber);
      },
      (errorCode: number, description: string) => {
        if (subscription) {
          subscription.remove();
        }
        reject(errorCode, description);
      }
    );
  });
}

export function recoverCard(
  cardNumber: string,
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.recoverCard(
      cardNumber,
      cycleList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function modifyCardValidityPeriod(
  cardNumber: string,
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyCardValidityPeriod(
      cardNumber,
      cycleList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function deleteCard(
  cardNumber: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteCard(cardNumber, lockData, resolve, reject);
  });
}

export function clearAllCards(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllCards(lockData, resolve, reject);
  });
}

// Fingerprint Operations
export function addFingerprint(
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string,
  progress?: (currentCount: number, totalCount: number) => void
): Promise<string> {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddFingerprintProgress;
    let subscription: any;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
        const dataArray = data as number[];
        if (dataArray && dataArray.length >= 2) {
          progress(dataArray[0] ?? 0, dataArray[1] ?? 0);
        }
      });
    }
    TtlockNitroHybridObject.addFingerprint(
      cycleList,
      startDate,
      endDate,
      lockData,
      (fingerprintNumber: string) => {
        if (subscription) {
          subscription.remove();
        }
        resolve(fingerprintNumber);
      },
      (errorCode: number, description: string) => {
        if (subscription) {
          subscription.remove();
        }
        reject(errorCode, description);
      }
    );
  });
}

export function modifyFingerprintValidityPeriod(
  fingerprintNumber: string,
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyFingerprintValidityPeriod(
      fingerprintNumber,
      cycleList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function deleteFingerprint(
  fingerprintNumber: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteFingerprint(
      fingerprintNumber,
      lockData,
      resolve,
      reject
    );
  });
}

export function clearAllFingerprints(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllFingerprints(lockData, resolve, reject);
  });
}

// Admin Passcode
export function modifyAdminPasscode(
  adminPasscode: string,
  lockData: string
): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.modifyAdminPasscode(
      adminPasscode,
      lockData,
      resolve,
      reject
    );
  });
}

// Lock Time
export function setLockTime(
  timestamp: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockTime(timestamp, lockData, resolve, reject);
  });
}

export function getLockTime(lockData: string): Promise<number> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockTime(
      lockData,
      (timestamp: string) => {
        resolve(Number(timestamp));
      },
      reject
    );
  });
}

// Lock System Info
export function getLockSystem(lockData: string): Promise<DeviceSystemModal> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSystem(lockData, resolve, reject);
  });
}

export function getLockElectricQuantity(lockData: string): Promise<number> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockElectricQuantity(lockData, resolve, reject);
  });
}

// Operation Record
export function getLockOperationRecord(
  type: LockRecordType,
  lockData: string
): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockOperationRecord(
      type,
      lockData,
      resolve,
      reject
    );
  });
}

// Automatic Locking
export function getLockAutomaticLockingPeriodicTime(
  lockData: string
): Promise<[number, number, number]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockAutomaticLockingPeriodicTime(
      lockData,
      (data: NumberNumberNumberTriple) =>
        resolve([data.first, data.second, data.third]),
      reject
    );
  });
}

export function setLockAutomaticLockingPeriodicTime(
  seconds: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockAutomaticLockingPeriodicTime(
      seconds,
      lockData,
      resolve,
      reject
    );
  });
}

// Remote Unlock Switch
export function getLockRemoteUnlockSwitchState(
  lockData: string
): Promise<boolean> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockRemoteUnlockSwitchState(
      lockData,
      resolve,
      reject
    );
  });
}

export function setLockRemoteUnlockSwitchState(
  isOn: boolean,
  lockData: string
): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.setLockRemoteUnlockSwitchState(
      isOn,
      lockData,
      resolve,
      reject
    );
  });
}

// Lock Config
export function getLockConfig(
  config: LockConfigType,
  lockData: string
): Promise<[number, boolean]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockConfig(
      config,
      lockData,
      (data: NumberBooleanPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

export function setLockConfig(
  config: LockConfigType,
  isOn: boolean,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockConfig(
      config,
      isOn,
      lockData,
      resolve,
      reject
    );
  });
}

// Sound Volume
export function setLockSoundVolume(
  soundVolume: LockSoundVolume,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockSoundVolume(
      soundVolume,
      lockData,
      resolve,
      reject
    );
  });
}

export function getLockSoundVolume(lockData: string): Promise<LockSoundVolume> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSoundVolume(
      lockData,
      (value: number) => {
        resolve(value as LockSoundVolume);
      },
      reject
    );
  });
}

// Unlock Direction
export function getUnlockDirection(
  lockData: string
): Promise<LockUnlockDirection> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getUnlockDirection(
      lockData,
      (direction: number) => {
        const directions = [
          LockUnlockDirection.Unknown,
          LockUnlockDirection.Left,
          LockUnlockDirection.Right,
        ];
        resolve(directions[direction] ?? LockUnlockDirection.Unknown);
      },
      reject
    );
  });
}

export function setUnlockDirection(
  direction: LockUnlockDirection,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setUnlockDirection(
      direction,
      lockData,
      resolve,
      reject
    );
  });
}

export function setUnlockDirectionAutomatic(
  lockData: string
): Promise<LockUnlockDirection> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.setUnlockDirectionAutomatic(
      lockData,
      (direction: number) => {
        const directions = [
          LockUnlockDirection.Unknown,
          LockUnlockDirection.Left,
          LockUnlockDirection.Right,
        ];
        resolve(directions[direction] ?? LockUnlockDirection.Unknown);
      },
      reject
    );
  });
}

// Passage Mode
export function addPassageMode(
  mode: LockPassageMode,
  days: number[],
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    const weekly = mode === LockPassageMode.Weekly ? days : null;
    const monthly = mode === LockPassageMode.Monthly ? days : null;
    TtlockNitroHybridObject.addPassageMode(
      mode,
      weekly,
      monthly,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function clearAllPassageModes(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllPassageModes(lockData, resolve, reject);
  });
}

// Remote Key
export function addRemoteKey(
  remoteKeyMac: string,
  cycleDateList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.addRemoteKey(
      remoteKeyMac,
      cycleDateList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function modifyRemoteKey(
  remoteKeyMac: string,
  cycleDateList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyRemoteKey(
      remoteKeyMac,
      cycleDateList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function deleteRemoteKey(
  remoteKeyMac: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteRemoteKey(
      remoteKeyMac,
      lockData,
      resolve,
      reject
    );
  });
}

export function clearAllRemoteKey(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllRemoteKey(lockData, resolve, reject);
  });
}

// Door Sensor
export function addDoorSensor(
  doorSensorMac: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.addDoorSensor(
      doorSensorMac,
      lockData,
      resolve,
      reject
    );
  });
}

export function clearAllDoorSensor(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllDoorSensor(lockData, resolve, reject);
  });
}

export function setDoorSensorAlertTime(
  time: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setDoorSensorAlertTime(
      time,
      lockData,
      resolve,
      reject
    );
  });
}

// Wifi Operations
export function scanWifi(
  lockData: string,
  callback?: (isFinished: boolean, wifiList: ScanWifiModal[]) => void,
  fail?: (errorCode: number, description: string) => void
): Promise<void> {
  return new Promise((resolve, reject) => {
    const eventName = TTLockEvent.ScanLockWifi;
    let subscription: any;
    if (callback) {
      subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
        const dataArray = data as any[];
        callback(dataArray[0], dataArray[1] as ScanWifiModal[]);
      });
    }
    TtlockNitroHybridObject.scanWifi(
      lockData,
      (errorCode: number, description: string) => {
        if (subscription) {
          subscription.remove();
        }
        if (fail) {
          fail(errorCode, description);
        }
        reject({ errorCode, description });
      }
    );
    // Note: scanWifi emits events via DeviceEventManagerModule
    // The promise resolves when scanning completes (handled by event)
    resolve();
  });
}

export function configWifi(
  wifiName: string,
  wifiPassword: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configWifi(
      wifiName,
      wifiPassword,
      lockData,
      resolve,
      reject
    );
  });
}

export function configServer(
  ip: string,
  port: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configServer(ip, port, lockData, resolve, reject);
  });
}

export function getWifiInfo(lockData: string): Promise<[string, number]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getWifiInfo(
      lockData,
      (data: StringNumberPair) => resolve([data.first, data.second]),
      reject
    );
  });
}

export function configIp(
  info: WifiLockServerInfo,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configIp(info, lockData, resolve, reject);
  });
}

// Wifi Power Saving
export function getWifiPowerSavingTime(lockData: string): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getWifiPowerSavingTime(lockData, resolve, reject);
  });
}

export function configWifiPowerSavingTime(
  weekDays: number[],
  startDate: number,
  endDate: number,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configWifiPowerSavingTime(
      weekDays,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function clearWifiPowerSavingTime(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearWifiPowerSavingTime(lockData, resolve, reject);
  });
}

// Face Operations
export function addFace(
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string,
  progress?: (state: number, faceErrorCode: number) => void
): Promise<string> {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddFaceProgress;
    let subscription: any;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, (data: any) => {
        const dataArray = data as number[];
        if (dataArray && dataArray.length >= 2) {
          progress(dataArray[0] ?? 0, dataArray[1] ?? 0);
        }
      });
    }
    TtlockNitroHybridObject.addFace(
      cycleList,
      startDate,
      endDate,
      lockData,
      (faceNumber: string) => {
        if (subscription) {
          subscription.remove();
        }
        resolve(faceNumber);
      },
      (errorCode: number, description: string) => {
        if (subscription) {
          subscription.remove();
        }
        reject(errorCode, description);
      }
    );
  });
}

export function addFaceFeatureData(
  faceFeatureData: string,
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  lockData: string
): Promise<string> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.addFaceFeatureData(
      faceFeatureData,
      cycleList,
      startDate,
      endDate,
      lockData,
      resolve,
      reject
    );
  });
}

export function modifyFaceValidityPeriod(
  cycleList: CycleDateParam[] | null,
  startDate: number,
  endDate: number,
  faceNumber: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyFaceValidityPeriod(
      cycleList,
      startDate,
      endDate,
      faceNumber,
      lockData,
      resolve,
      reject
    );
  });
}

export function deleteFace(
  faceNumber: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteFace(faceNumber, lockData, resolve, reject);
  });
}

export function clearFace(lockData: string): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearFace(lockData, resolve, reject);
  });
}

// Lift Operations
export function activateLiftFloors(
  floors: string,
  lockData: string
): Promise<[number, number, number]> {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.activateLiftFloors(
      floors,
      lockData,
      (data: NumberNumberNumberTriple) =>
        resolve([data.first, data.second, data.third]),
      reject
    );
  });
}

export function setLiftControlEnableFloors(
  floors: string,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLiftControlEnableFloors(
      floors,
      lockData,
      resolve,
      reject
    );
  });
}

export function setLiftWorkMode(
  workMode: LiftWorkMode,
  lockData: string
): Promise<void> {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLiftWorkMode(
      workMode,
      lockData,
      resolve,
      reject
    );
  });
}

// Utility
export function getBluetoothState(): Promise<BluetoothState> {
  return new Promise((resolve) => {
    TtlockNitroHybridObject.getBluetoothState((state: number) => {
      const states = [
        BluetoothState.Unknown,
        BluetoothState.Resetting,
        BluetoothState.Unsupported,
        BluetoothState.Unauthorized,
        BluetoothState.On,
        BluetoothState.Off,
      ];
      resolve(states[state] ?? BluetoothState.Unknown);
    });
  });
}

export function supportFunction(
  lockFunction: LockFunction,
  lockData: string
): Promise<boolean> {
  return new Promise((resolve) => {
    TtlockNitroHybridObject.supportFunction(lockFunction, lockData, resolve);
  });
}

// Event Listeners
export function addListener(
  eventName: string,
  listener: (eventName: string) => void
): void {
  TtlockNitroHybridObject.addListener(eventName, listener);
}

export function removeListener(
  eventName: string,
  listener: (eventName: string) => void
): void {
  TtlockNitroHybridObject.removeListener(eventName, listener);
}
