package `in`.kit.college_management_system

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this


        // setting Error activity
        CaocConfig.Builder.create().errorActivity(ErrorActivity::class.java).apply()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }
    }
}
