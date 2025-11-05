package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.entity.TTLockConfigType

/**
 * Created by TTLock on 2020/9/10.
 */
enum class TTLockConfigConverter {
    audio,
    passcodeVisible,
    freeze,
    tamperAlert,
    resetButton,
    privacyLock,
    passageModeAutoUnlock,
    wifiPowerSavingMode,
    doubleAuth,
    PublicMode,
    LowBatteryAutoUnlock;

    companion object {
        fun RN2Native(index: Int): TTLockConfigType? {
            val values = entries
            if (index < values.size) {
                return RN2Native(values[index])
            }
            return null
        }

        fun RN2Native(ttLockConfigConverter: TTLockConfigConverter): TTLockConfigType? {
            return when (ttLockConfigConverter) {
                audio -> TTLockConfigType.LOCK_SOUND
                passcodeVisible -> TTLockConfigType.PASSCODE_VISIBLE
                freeze -> TTLockConfigType.LOCK_FREEZE
                privacyLock -> TTLockConfigType.PRIVACY_LOCK
                resetButton -> TTLockConfigType.RESET_BUTTON
                tamperAlert -> TTLockConfigType.TAMPER_ALERT
                passageModeAutoUnlock -> TTLockConfigType.PASSAGE_MODE_AUTO_UNLOCK_SETTING
                wifiPowerSavingMode -> TTLockConfigType.WIFI_LOCK_POWER_SAVING_MODE
                doubleAuth -> TTLockConfigType.DOUBLE_CHECK
                else -> null
            }
        }

        fun native2RN(ttLockConfigType: TTLockConfigType): Int {
            return when (ttLockConfigType) {
                TTLockConfigType.LOCK_SOUND -> audio.ordinal
                TTLockConfigType.PASSCODE_VISIBLE -> passcodeVisible.ordinal
                TTLockConfigType.LOCK_FREEZE -> freeze.ordinal
                TTLockConfigType.PRIVACY_LOCK -> privacyLock.ordinal
                TTLockConfigType.RESET_BUTTON -> resetButton.ordinal
                TTLockConfigType.TAMPER_ALERT -> tamperAlert.ordinal
                TTLockConfigType.PASSAGE_MODE_AUTO_UNLOCK_SETTING -> passageModeAutoUnlock.ordinal
                TTLockConfigType.WIFI_LOCK_POWER_SAVING_MODE -> wifiPowerSavingMode.ordinal
                TTLockConfigType.DOUBLE_CHECK -> doubleAuth.ordinal
                else -> -1 // 未知
            }
        }
    }
}

