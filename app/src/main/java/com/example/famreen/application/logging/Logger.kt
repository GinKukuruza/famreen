package com.example.famreen.application.logging

import android.text.TextUtils
import android.util.Log
import androidx.core.os.trace
import com.example.famreen.BuildConfig
import java.lang.IllegalStateException

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