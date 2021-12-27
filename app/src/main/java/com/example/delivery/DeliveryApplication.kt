package com.example.delivery

import android.app.Application
import android.content.Context
import com.example.delivery.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DeliveryApplication: Application() {

    /* 4. 기본 설정 및 data :
    *  어플리케이션이 실행되면 가장 먼저 실행된다.
    *  context에 대한 설정, module에 관한 설정 이때 전부 선언된다.
    */
    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@DeliveryApplication)
            modules(appModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }

    companion object {

        var appContext: Context? = null
            private set

    }

}
