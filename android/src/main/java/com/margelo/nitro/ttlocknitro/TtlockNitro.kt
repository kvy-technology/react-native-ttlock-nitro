package com.margelo.nitro.ttlocknitro
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class TtlockNitro : HybridTtlockNitroSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
