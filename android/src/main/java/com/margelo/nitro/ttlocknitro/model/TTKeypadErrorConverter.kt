package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.keypad.model.KeypadError

/**
 * Created by TTLock on 2020/9/10.
 */
object TTKeypadErrorConverter {
    const val fail = 0
    const val wrongCRC = 1
    const val connectTimeout = 2
    const val wrongFactorydDate = 3

    fun native2RN(error: KeypadError): Int {
        return when (error) {
            KeypadError.FAILED -> fail
            KeypadError.KEYBOARD_CONNECT_FAIL -> connectTimeout
            else -> fail
        }
    }
}

