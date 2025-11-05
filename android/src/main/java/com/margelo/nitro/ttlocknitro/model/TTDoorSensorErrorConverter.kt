package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.wirelessdoorsensor.model.DoorSensorError

/**
 * Created by TTLock on 2020/9/10.
 */
object TTDoorSensorErrorConverter {
    const val bluetoothPowerOff = 0
    const val connectTimeout = 1
    const val fail = 2
    const val wrongCRC = 3

    fun native2RN(error: DoorSensorError): Int {
        return when (error) {
            DoorSensorError.FAILED -> fail
            DoorSensorError.CONNECT_FAIL -> connectTimeout
            else -> fail
        }
    }
}

