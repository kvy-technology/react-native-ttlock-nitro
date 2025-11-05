#include <jni.h>
#include "ttlocknitroOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::ttlocknitro::initialize(vm);
}
