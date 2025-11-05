package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.remote.model.RemoteError

/**
 * Created by TTLock on 2020/9/10.
 */
object TTRemoteKeyErrorConverter {
    const val fail = 0
    const val wrongCRC = 1
    const val connectTimeout = 2

    fun native2RN(error: RemoteError): Int {
        return when (error) {
            RemoteError.FAILED -> fail
            RemoteError.CONNECT_FAIL -> connectTimeout
            else -> fail
        }
    }
}

