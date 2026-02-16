#include <jni.h>
#include <android/log.h>
#include "block_rule_engine.h"
#include "security_validator.h"
#include "time_manager.h"
#include "pin_manager.h"
#include "grayscale_controller.h"
#include <memory>

#define TAG "SafeTimeGuard-JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

using namespace SafeTimeGuard;

// Global instances
static std::unique_ptr<BlockRuleEngine> blockRuleEngine;
static std::unique_ptr<SecurityValidator> securityValidator;
static std::unique_ptr<TimeManager> timeManager;
static std::unique_ptr<PinManager> pinManager;
static std::unique_ptr<GrayscaleController> grayscaleController;

extern "C" {

// Initialization
JNIEXPORT void JNICALL
Java_com_example_safetimeguard_NativeLib_initializeNative(JNIEnv *env, jobject obj) {
    try {
        blockRuleEngine = std::make_unique<BlockRuleEngine>();
        securityValidator = std::make_unique<SecurityValidator>();
        timeManager = std::make_unique<TimeManager>();
        pinManager = std::make_unique<PinManager>();
        grayscaleController = std::make_unique<GrayscaleController>();
        LOGD("Native library initialized successfully");
    } catch (const std::exception& e) {
        LOGE("Initialization failed: %s", e.what());
    }
}

// Block Rule Engine Methods
JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_shouldBlockApp(JNIEnv *env, jobject obj, 
                                                         jstring packageName, jlong currentTime) {
    const char *pkgName = env->GetStringUTFChars(packageName, nullptr);
    jboolean result = blockRuleEngine->shouldBlockApp(std::string(pkgName), (time_t)currentTime);
    env->ReleaseStringUTFChars(packageName, pkgName);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_example_safetimeguard_NativeLib_getBlockType(JNIEnv *env, jobject obj, 
                                                       jstring packageName, jlong currentTime) {
    const char *pkgName = env->GetStringUTFChars(packageName, nullptr);
    BlockType type = blockRuleEngine->getBlockType(std::string(pkgName), (time_t)currentTime);
    env->ReleaseStringUTFChars(packageName, pkgName);
    return (jint)type;
}

// Security Methods
JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_verifyPin(JNIEnv *env, jobject obj, jstring pin) {
    const char *pinStr = env->GetStringUTFChars(pin, nullptr);
    jboolean result = pinManager->verifyPin(std::string(pinStr));
    env->ReleaseStringUTFChars(pin, pinStr);
    return result;
}

JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_setPin(JNIEnv *env, jobject obj, jstring pin) {
    const char *pinStr = env->GetStringUTFChars(pin, nullptr);
    jboolean result = pinManager->setPin(std::string(pinStr));
    env->ReleaseStringUTFChars(pin, pinStr);
    return result;
}

JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_isPinSet(JNIEnv *env, jobject obj) {
    return pinManager->isPinSet();
}

JNIEXPORT jint JNICALL
Java_com_example_safetimeguard_NativeLib_getFailedPinAttempts(JNIEnv *env, jobject obj) {
    return pinManager->getFailedAttempts();
}

JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_isPinLocked(JNIEnv *env, jobject obj) {
    return pinManager->isLockedOut();
}

JNIEXPORT jlong JNICALL
Java_com_example_safetimeguard_NativeLib_getPinLockoutEndTime(JNIEnv *env, jobject obj) {
    return (jlong)pinManager->getLockoutEndTime();
}

// Time Manager Methods
JNIEXPORT jlong JNICALL
Java_com_example_safetimeguard_NativeLib_getCurrentTime(JNIEnv *env, jobject obj) {
    return (jlong)timeManager->getCurrentTime();
}

JNIEXPORT jint JNICALL
Java_com_example_safetimeguard_NativeLib_getDayOfWeek(JNIEnv *env, jobject obj, jlong timestamp) {
    return timeManager->getDayOfWeek((time_t)timestamp);
}

JNIEXPORT jstring JNICALL
Java_com_example_safetimeguard_NativeLib_formatTime(JNIEnv *env, jobject obj, jlong timestamp) {
    std::string formatted = timeManager->formatTime((time_t)timestamp);
    return env->NewStringUTF(formatted.c_str());
}

// Grayscale Methods
JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_shouldApplyGrayscale(JNIEnv *env, jobject obj, 
                                                              jstring packageName, jlong currentTime) {
    const char *pkgName = env->GetStringUTFChars(packageName, nullptr);
    jboolean result = grayscaleController->shouldApplyGrayscale(std::string(pkgName), (time_t)currentTime);
    env->ReleaseStringUTFChars(packageName, pkgName);
    return result;
}

JNIEXPORT jfloat JNICALL
Java_com_example_safetimeguard_NativeLib_getGrayscaleIntensity(JNIEnv *env, jobject obj, 
                                                               jstring packageName, jlong currentTime) {
    const char *pkgName = env->GetStringUTFChars(packageName, nullptr);
    jfloat result = grayscaleController->getGrayscaleIntensity(std::string(pkgName), (time_t)currentTime);
    env->ReleaseStringUTFChars(packageName, pkgName);
    return result;
}

// Validation Methods
JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_isRootDetected(JNIEnv *env, jobject obj) {
    return securityValidator->isRootDetected();
}

JNIEXPORT jboolean JNICALL
Java_com_example_safetimeguard_NativeLib_isEmulatorDetected(JNIEnv *env, jobject obj) {
    return securityValidator->isEmulatorDetected();
}

JNIEXPORT jstring JNICALL
Java_com_example_safetimeguard_NativeLib_validatePinStrength(JNIEnv *env, jobject obj, jstring pin) {
    const char *pinStr = env->GetStringUTFChars(pin, nullptr);
    std::string validation = securityValidator->validatePinStrength(std::string(pinStr));
    env->ReleaseStringUTFChars(pin, pinStr);
    return env->NewStringUTF(validation.c_str());
}

}  // extern "C"
