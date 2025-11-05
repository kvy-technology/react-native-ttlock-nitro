package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.entity.LockError

/**
 * Created by TTLock on 2020/9/10.
 */
object TTLockErrorConverter {
    const val hadReseted = 0
    const val crcError = 1
    const val noPermisstion = 2
    const val wrongAdminCode = 3
    const val lackOfStorageSpace = 4
    const val inSettingMode = 5
    const val noAdmin = 6
    const val notInSettingMode = 7
    const val wrongDynamicCode = 8
    const val isNoPower = 9
    const val resetPasscode = 10
    const val updatePasscodeIndex = 11
    const val invalidLockFlagPos = 12
    const val ekeyExpired = 13
    const val passcodeLengthInvalid = 14
    const val samePasscodes = 15
    const val ekeyInactive = 16
    const val aesKey = 17
    const val fail = 18
    const val passcodeExist = 19
    const val passcodeNotExist = 20
    const val lackOfStorageSpaceWhenAddingPasscodes = 21
    const val invalidParaLength = 22
    const val cardNotExist = 23
    const val fingerprintDuplication = 24
    const val fingerprintNotExist = 25
    const val invalidCommand = 26
    const val inFreezeMode = 27
    const val invalidClientPara = 28
    const val lockIsLocked = 29
    const val recordNotExist = 30
    const val wrongSSID = 31
    const val wrongWifiPassword = 32
    const val bluetoothPoweredOff = 33
    const val connectionTimeout = 34
    const val disconnection = 35
    const val lockIsBusy = 36
    const val wrongLockData = 37
    const val invalidParameter = 38

    fun native2RN(error: LockError): Int {
        return when (error) {
            LockError.KEY_INVALID -> hadReseted
            LockError.LOCK_CRC_CHECK_ERROR -> crcError
            LockError.LOCK_NO_PERMISSION -> noPermisstion
            LockError.LOCK_ADMIN_CHECK_ERROR -> wrongAdminCode
            LockError.LOCK_IS_IN_SETTING_MODE -> inSettingMode
            LockError.LOCK_NOT_EXIST_ADMIN -> noAdmin
            LockError.LOCK_IS_IN_NO_SETTING_MODE -> notInSettingMode
            LockError.LOCK_DYNAMIC_PWD_ERROR -> wrongDynamicCode
            LockError.LOCK_NO_POWER -> isNoPower
            LockError.LOCK_INIT_KEYBOARD_FAILED -> resetPasscode
            LockError.LOCK_KEY_FLAG_INVALID -> invalidLockFlagPos
            LockError.LOCK_USER_TIME_EXPIRED -> ekeyExpired
            LockError.LOCK_PASSWORD_LENGTH_INVALID -> passcodeLengthInvalid
            LockError.LOCK_SUPER_PASSWORD_IS_SAME_WITH_DELETE_PASSWORD -> samePasscodes
            LockError.LOCK_USER_TIME_INEFFECTIVE -> ekeyInactive
            LockError.LOCK_PASSWORD_EXIST -> passcodeExist
            LockError.LOCK_PASSWORD_NOT_EXIST -> passcodeNotExist
            LockError.LOCK_NO_FREE_MEMORY -> lackOfStorageSpace
            LockError.LOCK_REVERSE -> lockIsLocked
            LockError.INVALID_VENDOR -> invalidClientPara
            LockError.INVALID_COMMAND -> invalidCommand
            LockError.LOCK_FROZEN -> inFreezeMode
            LockError.BAD_WIFI_NAME -> wrongSSID
            LockError.BAD_WIFI_PASSWORD -> wrongWifiPassword
            LockError.IC_CARD_NOT_EXIST -> cardNotExist
            LockError.FINGER_PRINT_NOT_EXIST -> fingerprintNotExist
            LockError.INVALID_PARAM -> invalidParameter
            LockError.RECORD_NOT_EXIST -> recordNotExist
            LockError.Failed -> fail
            LockError.LOCK_CONNECT_FAIL -> connectionTimeout
            LockError.LOCK_IS_BUSY -> lockIsBusy
            LockError.DATA_FORMAT_ERROR -> invalidParameter
            else -> fail
        }
    }
}

