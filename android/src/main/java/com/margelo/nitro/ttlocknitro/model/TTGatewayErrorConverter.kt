package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.gateway.model.GatewayError

/**
 * Created by TTLock on 2020/9/10.
 */
object TTGatewayErrorConverter {
    const val fail = 0
    const val wrongSSID = 1
    const val wrongWifiPassword = 2
    const val wrongCRC = 3
    const val wrongAeskey = 4
    const val notConnect = 5
    const val disconnect = 6
    const val failConfigRouter = 7
    const val failConfigServer = 8
    const val failConfigAccount = 9
    const val noSIM = 10
    const val invalidCommand = 11
    const val failConfigIP = 12
    const val failInvaildIP = 13

    fun native2RN(error: GatewayError): Int {
        return when (error) {
            GatewayError.FAILED -> fail
            GatewayError.BAD_WIFI_NAME -> wrongSSID
            GatewayError.BAD_WIFI_PASSWORD -> wrongWifiPassword
            GatewayError.INVALID_COMMAND -> invalidCommand
            GatewayError.TIME_OUT -> notConnect
            GatewayError.NO_SIM_CARD -> noSIM
            GatewayError.FAILED_TO_CONFIGURE_ROUTER -> failConfigRouter
            GatewayError.FAILED_TO_CONFIGURE_SERVER -> failConfigServer
            GatewayError.FAILED_TO_CONFIGURE_ACCOUNT -> failConfigAccount
            else -> fail
        }
    }
}

