package com.example.famreen.application.logging

import android.text.TextUtils
import android.util.Log
import androidx.core.os.trace
import com.example.famreen.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.IllegalStateException
/** log crashlytics priority(1-10)*/
sealed class Logger {
    companion object{
        fun d(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.d(className,getLoc(className) + msg)
            identifier?.let {
                Log.d(identifier,getLoc(className) + msg)
            }
        }
        fun e(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.e(className,getLoc(className) + msg)
            identifier?.let {
                Log.e(identifier,getLoc(className) + msg)
            }
        }
        fun i(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.i(className,getLoc(className) + msg)
            identifier?.let {
                Log.i(identifier,getLoc(className) + msg)
            }
        }
        fun log(priority: Int, message: String, ex: Throwable?) {
            //TODO if(isCollectionEnabled)
            if (priority == Log.ERROR || priority == Log.DEBUG) {
                val builder = StringBuilder()
                    .append("Priority: ")
                    .append(priority)
                    .append(", ")
                    .append("Message: ")
                    .append(message)

                FirebaseCrashlytics.getInstance().log(builder.toString())
                if (ex != null) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            } else return
        }

        private fun checkBuild(){
            /*if(BuildConfig.DEBUG && BuildConfig.BUILD_TYPE == "debug"){
                throw IllegalStateException("Build version not debug")
            }*/
        }
        private fun getLoc(className: String) : String{
            val traces = Thread.currentThread().stackTrace
            var found = false
            for(i in traces){
                try {
                    if(found){
                        if(!i.className.startsWith(className)){
                            val _class = Class.forName(i.className)
                            return "[" + className + ":" + i.methodName + ":" + i.lineNumber + "]: "
                        }
                    }else if(i.className.startsWith(className)){
                        found = true
                        continue
                    }
                }catch (ex: ClassNotFoundException){
                    d("logger","class not found for parse","logger")
                }
            }
            return "[]: "
        }
        private fun getClassName(_class: Class<*>?) : String{
            if(_class == null) return ""
            if(!TextUtils.isEmpty(_class.simpleName)){
                return _class.simpleName
            }
            return getClassName(_class.enclosingClass)
        }
    }
}