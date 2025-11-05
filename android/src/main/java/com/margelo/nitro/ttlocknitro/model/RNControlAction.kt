package com.margelo.nitro.ttlocknitro.model

import com.ttlock.bl.sdk.constant.ControlAction

/**
 * Created by TTLock on 2021/1/5.
 */
object RNControlAction {
    const val unlock = 0
    const val lock = 1

    fun RN2Native(rnAction: Int): Int {
        return when (rnAction) {
            unlock -> ControlAction.UNLOCK
            lock -> ControlAction.LOCK
            else -> ControlAction.UNLOCK
        }
    }

    fun isValidAction(rnAction: Int): Boolean {
        return rnAction in 0..1
    }
}

