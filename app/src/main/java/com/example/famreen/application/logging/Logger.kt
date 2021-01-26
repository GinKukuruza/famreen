package com.example.famreen.application.logging

import android.text.TextUtils
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

sealed class Logger {
    companion object{
        /**
         * Уровень логгирования - DEBUG, используется во время отладки
         * **/
        fun d(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.d(className,getLoc(className) + msg)
            identifier?.let {
                Log.d(identifier,getLoc(className) + msg)
            }
        }
        /**
         * Уровень логгирования - ERROR, используется во время отладки
         * **/
        fun e(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.e(className,getLoc(className) + msg)
            identifier?.let {
                Log.e(identifier,getLoc(className) + msg)
            }
        }
        /**
         * Уровень логгирования - INFO, используется во время отладки
         * **/
        fun i(className: String, msg: String, identifier: String?){
            checkBuild()
            Log.i(className,getLoc(className) + msg)
            identifier?.let {
                Log.i(identifier,getLoc(className) + msg)
            }
        }
        /**
         * Уровень логгирования - WTF, используется для целей безопасности
         * логирует важную информацию в stack trace и, так же, логгирует ее на сервер
         * **/
        fun wtf(msg: String) : Boolean{
            Log.wtf("detection",msg)
            log(Log.WARN,msg,null)
            return true
        }
        /**
         * Уровень логгирования - WTF, используется для целей безопасности
         * используется как заглушка для методов безопасности
         * **/
        fun wtf() : Boolean{
            return false
        }
        /**
         * Уровень логгирования задается в параметре, используется для целей безопасности
         * логгирование на сервер
         * задается приоритет из Log.? , сообщение и исключение
         * **/
        fun log(priority: Int, message: String, ex: Throwable?) {
            //TODO if(isCollectionEnabled)
            if (priority == Log.ERROR || priority == Log.DEBUG || priority == Log.WARN) {
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