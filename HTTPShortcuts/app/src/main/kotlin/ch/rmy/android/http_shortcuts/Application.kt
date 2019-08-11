package ch.rmy.android.http_shortcuts

import android.content.Context
import androidx.multidex.MultiDex
import ch.rmy.android.http_shortcuts.data.RealmFactory
import ch.rmy.android.http_shortcuts.extensions.logException
import ch.rmy.android.http_shortcuts.utils.CrashReporting
import ch.rmy.android.http_shortcuts.utils.Settings
import com.facebook.stetho.Stetho
import io.reactivex.plugins.RxJavaPlugins

class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        CrashReporting.init(context)
        CrashReporting.enabled = Settings(context).isCrashReportingAllowed

        Stetho.initializeWithDefaults(context)

        RxJavaPlugins.setErrorHandler { error ->
            logException(error)
        }

        RealmFactory.init(applicationContext)
    }

    public override fun attachBaseContext(base: Context) {
        MultiDex.install(base)
        super.attachBaseContext(base)
    }

    val context: Context
        get() = this

}
