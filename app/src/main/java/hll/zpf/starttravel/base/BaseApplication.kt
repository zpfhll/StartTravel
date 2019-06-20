package hll.zpf.starttravel.base

import android.app.Application

class BaseApplication:Application() {
    companion object {
        var application:BaseApplication? = null
    }
}