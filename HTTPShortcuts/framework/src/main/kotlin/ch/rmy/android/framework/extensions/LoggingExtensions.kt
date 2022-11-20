package ch.rmy.android.framework.extensions

import android.util.Log
import ch.rmy.android.framework.BuildConfig

object GlobalLogger {

    private var logging: Logging? = null

    fun registerLogging(logging: Logging) {
        this.logging = logging
    }

    internal fun logException(origin: String, e: Throwable) {
        logging?.logException(origin, e)
    }

    internal fun logInfo(origin: String, message: String) {
        logging?.logInfo(origin, message)
    }
}

interface Logging {
    fun logException(origin: String, e: Throwable)

    fun logInfo(origin: String, message: String)
}

fun Any.logException(e: Throwable) {
    GlobalLogger.logException(this.javaClass.name.ifEmpty { "anonymous" }, e)
}

fun Any.logInfo(message: String) {
    GlobalLogger.logInfo(this.javaClass.name.ifEmpty { "anonymous" }, message)
}

inline fun <T> Any.tryOrLog(block: () -> T): T? =
    try {
        block()
    } catch (e: Throwable) {
        logException(e)
        null
    }

inline fun <T> Any.tryOrIgnore(block: () -> T): T? =
    try {
        block()
    } catch (e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(this.javaClass.name, "An ignorable error occurred", e)
        }
        null
    }
