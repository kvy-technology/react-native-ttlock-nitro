"use strict";

import { NativeEventEmitter, NativeModules } from 'react-native';
import { NitroModules } from 'react-native-nitro-modules';
import { ConnectState, LockState, LockUnlockDirection, LockPassageMode, BluetoothState, TTLockEvent, GatewayEvent, TtRemoteKeyEvent, TtDoorSensorEvent, WirelessKeypadEvent } from "./types.js";
const TtlockNitroHybridObject = NitroModules.createHybridObject('TtlockNitro');

// Use NativeEventEmitter with the native module for event data
// The native module name is "Ttlock" as defined in the native code
const ttLockModule = NativeModules.Ttlock;
const ttLockEventEmitter = new NativeEventEmitter(ttLockModule);

// Subscription map to track subscriptions
const subscriptionMap = new Map();

// Helper function to convert callback to promise
function promisify(fn) {
  return new Promise((resolve, reject) => {
    fn(value => resolve(value), (errorCode, description) => reject({
      errorCode,
      description
    }));
  });
}

// Helper function for void promises
function promisifyVoid(fn) {
  return new Promise((resolve, reject) => {
    fn(() => resolve(), (errorCode, description) => reject({
      errorCode,
      description
    }));
  });
}

// Wireless Keypad
export function startScanWirelessKeypad(callback) {
  const eventName = WirelessKeypadEvent.ScanWirelessKeypad;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, data => {
      callback(data);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanWirelessKeypad();
}
export function stopScanWirelessKeypad() {
  TtlockNitroHybridObject.stopScanWirelessKeypad();
  const subscription = subscriptionMap.get(WirelessKeypadEvent.ScanWirelessKeypad);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(WirelessKeypadEvent.ScanWirelessKeypad);
}
export function initWirelessKeypad(keypadMac, lockMac) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initWirelessKeypad(keypadMac, lockMac, data => resolve([data.first, data.second]), reject);
  });
}

// Door Sensor
export function startScanDoorSensor(callback) {
  const eventName = TtDoorSensorEvent.ScanDoorSensor;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, data => {
      callback(data);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanDoorSensor();
}
export function stopScanDoorSensor() {
  TtlockNitroHybridObject.stopScanDoorSensor();
  const subscription = subscriptionMap.get(TtDoorSensorEvent.ScanDoorSensor);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TtDoorSensorEvent.ScanDoorSensor);
}
export function initDoorSensor(doorSensorMac, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initDoorSensor(doorSensorMac, lockData, data => resolve([data.first, data.second]), reject);
  });
}

// Remote Key
export function startScanRemoteKey(callback) {
  const eventName = TtRemoteKeyEvent.ScanRemoteKey;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, data => {
      callback(data);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanRemoteKey();
}
export function stopScanRemoteKey() {
  TtlockNitroHybridObject.stopScanRemoteKey();
  const subscription = subscriptionMap.get(TtRemoteKeyEvent.ScanRemoteKey);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TtRemoteKeyEvent.ScanRemoteKey);
}
export function initRemoteKey(remoteMac, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initRemoteKey(remoteMac, lockData, data => resolve([data.first, data.second]), reject);
  });
}
export function getRemoteKeySystemInfo(remoteMac) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getRemoteKeySystemInfo(remoteMac, resolve, reject);
  });
}

// Gateway
export function startScanGateway(callback) {
  const eventName = GatewayEvent.ScanGateway;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, data => {
      callback(data);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScanGateway();
}
export function stopScanGateway() {
  TtlockNitroHybridObject.stopScanGateway();
  const subscription = subscriptionMap.get(GatewayEvent.ScanGateway);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(GatewayEvent.ScanGateway);
}
export function connectGateway(mac) {
  return new Promise(resolve => {
    TtlockNitroHybridObject.connect(mac, state => {
      const states = [ConnectState.Timeout, ConnectState.Success, ConnectState.Fail];
      resolve(states[state] ?? ConnectState.Fail);
    });
  });
}
export function getNearbyWifi(progress, finish, fail) {
  return new Promise((resolve, reject) => {
    const eventName = GatewayEvent.ScanWifi;
    let subscription;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, data => {
        progress(data);
      });
    }
    TtlockNitroHybridObject.getNearbyWifi(state => {
      if (subscription) {
        subscription.remove();
      }
      if (state === 0) {
        if (finish) finish();
        resolve();
      } else {
        if (fail) fail(state, 'Scan WiFi failed');
        reject({
          errorCode: state,
          description: 'Scan WiFi failed'
        });
      }
    });
  });
}
export function initGateway(params) {
  return new Promise((resolve, reject) => {
    TtlockNitroHybridObject.initGateway(params, resolve, errorCode => reject({
      errorCode,
      description: ''
    }));
  });
}

// Lock Operations
export function startScan(callback) {
  const eventName = TTLockEvent.ScanLock;
  let subscription = subscriptionMap.get(eventName);
  if (subscription !== undefined) {
    subscription.remove();
  }
  if (callback) {
    subscription = ttLockEventEmitter.addListener(eventName, data => {
      callback(data);
    });
    subscriptionMap.set(eventName, subscription);
  }
  TtlockNitroHybridObject.startScan();
}
export function stopScan() {
  TtlockNitroHybridObject.stopScan();
  const subscription = subscriptionMap.get(TTLockEvent.ScanLock);
  if (subscription !== undefined) {
    subscription.remove();
  }
  subscriptionMap.delete(TTLockEvent.ScanLock);
}
export function initLock(params) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.initLock(params, resolve, reject);
  });
}
export function getLockVersionWithLockMac(lockMac) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockVersionWithLockMac(lockMac, resolve, reject);
  });
}
export function getAccessoryElectricQuantity(accessoryType, accessoryMac, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getAccessoryElectricQuantity(accessoryType, accessoryMac, lockData, data => resolve([data.first, data.second]), reject);
  });
}
export function resetLock(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.resetLock(lockData, resolve, reject);
  });
}
export function resetEkey(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.resetEkey(lockData, resolve, reject);
  });
}
export function controlLock(control, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.controlLock(control, lockData, data => resolve([data.first, data.second, data.third]), reject);
  });
}

// Passcode Operations
export function createCustomPasscode(passcode, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.createCustomPasscode(passcode, startDate, endDate, lockData, resolve, reject);
  });
}
export function recoverPasscode(passcode, passcodeType, cycleType, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.recoverPasscode(passcode, passcodeType, cycleType, startDate, endDate, lockData, resolve, reject);
  });
}
export function modifyPasscode(passcodeOrigin, passcodeNew, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyPasscode(passcodeOrigin, passcodeNew, startDate, endDate, lockData, resolve, reject);
  });
}
export function deletePasscode(passcode, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deletePasscode(passcode, lockData, resolve, reject);
  });
}
export function resetPasscode(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.resetPasscode(lockData, resolve, reject);
  });
}

// Lock Status
export function getLockSwitchState(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSwitchState(lockData, state => {
      const states = [LockState.Locked, LockState.Unlock, LockState.Unknown, LockState.CarOnLock];
      resolve(states[state] ?? LockState.Unknown);
    }, reject);
  });
}

// Card Operations
export function addCard(cycleList, startDate, endDate, lockData, progress) {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddCardProgress;
    let subscription;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, progress);
    }
    TtlockNitroHybridObject.addCard(cycleList, startDate, endDate, lockData, cardNumber => {
      if (subscription) {
        subscription.remove();
      }
      resolve(cardNumber);
    }, (errorCode, description) => {
      if (subscription) {
        subscription.remove();
      }
      reject(errorCode, description);
    });
  });
}
export function recoverCard(cardNumber, cycleList, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.recoverCard(cardNumber, cycleList, startDate, endDate, lockData, resolve, reject);
  });
}
export function modifyCardValidityPeriod(cardNumber, cycleList, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyCardValidityPeriod(cardNumber, cycleList, startDate, endDate, lockData, resolve, reject);
  });
}
export function deleteCard(cardNumber, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteCard(cardNumber, lockData, resolve, reject);
  });
}
export function clearAllCards(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllCards(lockData, resolve, reject);
  });
}

// Fingerprint Operations
export function addFingerprint(cycleList, startDate, endDate, lockData, progress) {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddFingerprintProgress;
    let subscription;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, data => {
        const dataArray = data;
        if (dataArray && dataArray.length >= 2) {
          progress(dataArray[0] ?? 0, dataArray[1] ?? 0);
        }
      });
    }
    TtlockNitroHybridObject.addFingerprint(cycleList, startDate, endDate, lockData, fingerprintNumber => {
      if (subscription) {
        subscription.remove();
      }
      resolve(fingerprintNumber);
    }, (errorCode, description) => {
      if (subscription) {
        subscription.remove();
      }
      reject(errorCode, description);
    });
  });
}
export function modifyFingerprintValidityPeriod(fingerprintNumber, cycleList, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyFingerprintValidityPeriod(fingerprintNumber, cycleList, startDate, endDate, lockData, resolve, reject);
  });
}
export function deleteFingerprint(fingerprintNumber, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteFingerprint(fingerprintNumber, lockData, resolve, reject);
  });
}
export function clearAllFingerprints(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllFingerprints(lockData, resolve, reject);
  });
}

// Admin Passcode
export function modifyAdminPasscode(adminPasscode, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.modifyAdminPasscode(adminPasscode, lockData, resolve, reject);
  });
}

// Lock Time
export function setLockTime(timestamp, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockTime(timestamp, lockData, resolve, reject);
  });
}
export function getLockTime(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockTime(lockData, timestamp => {
      resolve(Number(timestamp));
    }, reject);
  });
}

// Lock System Info
export function getLockSystem(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSystem(lockData, resolve, reject);
  });
}
export function getLockElectricQuantity(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockElectricQuantity(lockData, resolve, reject);
  });
}

// Operation Record
export function getLockOperationRecord(type, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockOperationRecord(type, lockData, resolve, reject);
  });
}

// Automatic Locking
export function getLockAutomaticLockingPeriodicTime(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockAutomaticLockingPeriodicTime(lockData, data => resolve([data.first, data.second, data.third]), reject);
  });
}
export function setLockAutomaticLockingPeriodicTime(seconds, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockAutomaticLockingPeriodicTime(seconds, lockData, resolve, reject);
  });
}

// Remote Unlock Switch
export function getLockRemoteUnlockSwitchState(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockRemoteUnlockSwitchState(lockData, resolve, reject);
  });
}
export function setLockRemoteUnlockSwitchState(isOn, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.setLockRemoteUnlockSwitchState(isOn, lockData, resolve, reject);
  });
}

// Lock Config
export function getLockConfig(config, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockConfig(config, lockData, data => resolve([data.first, data.second]), reject);
  });
}
export function setLockConfig(config, isOn, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockConfig(config, isOn, lockData, resolve, reject);
  });
}

// Sound Volume
export function setLockSoundVolume(soundVolume, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLockSoundVolume(soundVolume, lockData, resolve, reject);
  });
}
export function getLockSoundVolume(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getLockSoundVolume(lockData, value => {
      resolve(value);
    }, reject);
  });
}

// Unlock Direction
export function getUnlockDirection(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getUnlockDirection(lockData, direction => {
      const directions = [LockUnlockDirection.Unknown, LockUnlockDirection.Left, LockUnlockDirection.Right];
      resolve(directions[direction] ?? LockUnlockDirection.Unknown);
    }, reject);
  });
}
export function setUnlockDirection(direction, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setUnlockDirection(direction, lockData, resolve, reject);
  });
}
export function setUnlockDirectionAutomatic(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.setUnlockDirectionAutomatic(lockData, direction => {
      const directions = [LockUnlockDirection.Unknown, LockUnlockDirection.Left, LockUnlockDirection.Right];
      resolve(directions[direction] ?? LockUnlockDirection.Unknown);
    }, reject);
  });
}

// Passage Mode
export function addPassageMode(mode, days, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    const weekly = mode === LockPassageMode.Weekly ? days : null;
    const monthly = mode === LockPassageMode.Monthly ? days : null;
    TtlockNitroHybridObject.addPassageMode(mode, weekly, monthly, startDate, endDate, lockData, resolve, reject);
  });
}
export function clearAllPassageModes(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllPassageModes(lockData, resolve, reject);
  });
}

// Remote Key
export function addRemoteKey(remoteKeyMac, cycleDateList, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.addRemoteKey(remoteKeyMac, cycleDateList, startDate, endDate, lockData, resolve, reject);
  });
}
export function modifyRemoteKey(remoteKeyMac, cycleDateList, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyRemoteKey(remoteKeyMac, cycleDateList, startDate, endDate, lockData, resolve, reject);
  });
}
export function deleteRemoteKey(remoteKeyMac, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteRemoteKey(remoteKeyMac, lockData, resolve, reject);
  });
}
export function clearAllRemoteKey(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllRemoteKey(lockData, resolve, reject);
  });
}

// Door Sensor
export function addDoorSensor(doorSensorMac, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.addDoorSensor(doorSensorMac, lockData, resolve, reject);
  });
}
export function clearAllDoorSensor(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearAllDoorSensor(lockData, resolve, reject);
  });
}
export function setDoorSensorAlertTime(time, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setDoorSensorAlertTime(time, lockData, resolve, reject);
  });
}

// Wifi Operations
export function scanWifi(lockData, callback, fail) {
  return new Promise((resolve, reject) => {
    const eventName = TTLockEvent.ScanLockWifi;
    let subscription;
    if (callback) {
      subscription = ttLockEventEmitter.addListener(eventName, data => {
        const dataArray = data;
        callback(dataArray[0], dataArray[1]);
      });
    }
    TtlockNitroHybridObject.scanWifi(lockData, (errorCode, description) => {
      if (subscription) {
        subscription.remove();
      }
      if (fail) {
        fail(errorCode, description);
      }
      reject({
        errorCode,
        description
      });
    });
    // Note: scanWifi emits events via DeviceEventManagerModule
    // The promise resolves when scanning completes (handled by event)
    resolve();
  });
}
export function configWifi(wifiName, wifiPassword, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configWifi(wifiName, wifiPassword, lockData, resolve, reject);
  });
}
export function configServer(ip, port, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configServer(ip, port, lockData, resolve, reject);
  });
}
export function getWifiInfo(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getWifiInfo(lockData, data => resolve([data.first, data.second]), reject);
  });
}
export function configIp(info, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configIp(info, lockData, resolve, reject);
  });
}

// Wifi Power Saving
export function getWifiPowerSavingTime(lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.getWifiPowerSavingTime(lockData, resolve, reject);
  });
}
export function configWifiPowerSavingTime(weekDays, startDate, endDate, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.configWifiPowerSavingTime(weekDays, startDate, endDate, lockData, resolve, reject);
  });
}
export function clearWifiPowerSavingTime(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearWifiPowerSavingTime(lockData, resolve, reject);
  });
}

// Face Operations
export function addFace(cycleList, startDate, endDate, lockData, progress) {
  return promisify((resolve, reject) => {
    const eventName = TTLockEvent.AddFaceProgress;
    let subscription;
    if (progress) {
      subscription = ttLockEventEmitter.addListener(eventName, data => {
        const dataArray = data;
        if (dataArray && dataArray.length >= 2) {
          progress(dataArray[0] ?? 0, dataArray[1] ?? 0);
        }
      });
    }
    TtlockNitroHybridObject.addFace(cycleList, startDate, endDate, lockData, faceNumber => {
      if (subscription) {
        subscription.remove();
      }
      resolve(faceNumber);
    }, (errorCode, description) => {
      if (subscription) {
        subscription.remove();
      }
      reject(errorCode, description);
    });
  });
}
export function addFaceFeatureData(faceFeatureData, cycleList, startDate, endDate, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.addFaceFeatureData(faceFeatureData, cycleList, startDate, endDate, lockData, resolve, reject);
  });
}
export function modifyFaceValidityPeriod(cycleList, startDate, endDate, faceNumber, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.modifyFaceValidityPeriod(cycleList, startDate, endDate, faceNumber, lockData, resolve, reject);
  });
}
export function deleteFace(faceNumber, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.deleteFace(faceNumber, lockData, resolve, reject);
  });
}
export function clearFace(lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.clearFace(lockData, resolve, reject);
  });
}

// Lift Operations
export function activateLiftFloors(floors, lockData) {
  return promisify((resolve, reject) => {
    TtlockNitroHybridObject.activateLiftFloors(floors, lockData, data => resolve([data.first, data.second, data.third]), reject);
  });
}
export function setLiftControlEnableFloors(floors, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLiftControlEnableFloors(floors, lockData, resolve, reject);
  });
}
export function setLiftWorkMode(workMode, lockData) {
  return promisifyVoid((resolve, reject) => {
    TtlockNitroHybridObject.setLiftWorkMode(workMode, lockData, resolve, reject);
  });
}

// Utility
export function getBluetoothState() {
  return new Promise(resolve => {
    TtlockNitroHybridObject.getBluetoothState(state => {
      const states = [BluetoothState.Unknown, BluetoothState.Resetting, BluetoothState.Unsupported, BluetoothState.Unauthorized, BluetoothState.On, BluetoothState.Off];
      resolve(states[state] ?? BluetoothState.Unknown);
    });
  });
}
export function supportFunction(lockFunction, lockData) {
  return new Promise(resolve => {
    TtlockNitroHybridObject.supportFunction(lockFunction, lockData, resolve);
  });
}

// Event Listeners
export function addListener(eventName, listener) {
  TtlockNitroHybridObject.addListener(eventName, listener);
}
export function removeListener(eventName, listener) {
  TtlockNitroHybridObject.removeListener(eventName, listener);
}
//# sourceMappingURL=core.js.map