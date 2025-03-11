// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("network-shared");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("network-shared")
//      }
//    }


#include <jni.h>
#include <string>

/*
 * Returns string key public key pin 1 for chalo
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getPKP1c(JNIEnv *env, jobject thiz) {
    std::string s = "sha256/";
    std::string e = "PAt/jdy1p7fzYZ92zuY=";
    std::string m = "Acp8RGwwD+emZSwqSXleV4eL";
    return env->NewStringUTF(s.append(m).append(e).c_str());
}

/*
 * Returns string key public key pin 2 for Sectigo RSA Domain Validation
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getPKP2c(JNIEnv *env, jobject jobj) {
    std::string s = "sha256/";
    std::string e = "FR8a60d3auda+sKfg4Ng=";
    std::string m = "4a6cPehI7OG6cuDZka5NDZ7";
    return env->NewStringUTF(s.append(m).append(e).c_str());
}

/*
 * Returns string key public key pin 3 for  Certification Authority
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getPKP3c(JNIEnv *env, jobject jobj) {
    std::string s = "sha256/";
    std::string e = "k3Bw5zBn4lTdO/nEW/Td4=";
    std::string m = "x4QzPSC810K5/cMjb05Qm4";
    return env->NewStringUTF(s.append(m).append(e).c_str());
}


/*
 * Returns string key public key pin 1 for zophop
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getPKP1z(JNIEnv *env, jobject jobj) {
    std::string s = "sha256/";
    std::string e = "IuZZ96TT3xvJzdRq7yaxGxw=";
    std::string m = "itSXMEHvzgmQ5QBIl4K1";
    return env->NewStringUTF(s.append(m).append(e).c_str());
}

/*
 * Returns string key public key pin 1 for chalo
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getc(JNIEnv *env, jobject thiz) {
    std::string s = "*.gometro.com";
    return env->NewStringUTF(s.c_str());
}

/*
 * Returns string key public key pin 1 for chalo
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getz(JNIEnv *env, jobject thiz) {
    std::string s = "*.gometro.com";
    return env->NewStringUTF(s.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gometro_network_utils_SecureJNI_getVaultKey(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("4a3c55c1535245c392b1c981c064ac4a");
}
