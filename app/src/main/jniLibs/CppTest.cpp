//
// Created by litred on 17.01.2021.
//

#include "CppTest.h"
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_ru_androidtools_ndktest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}