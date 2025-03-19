package com.gometro.logger

typealias ErrorReporterContract = CrashlyticsLogger

interface CrashlyticsLogger {

    fun reportHandledException(throwable: Throwable?)

    /**
     * when this is called, all logs leading up to this
     * exception are reported to crashlytics
     */
    fun commitLogHistory(exception: Exception)

    /**
     * up to 64kB of logs per session. Oldest ones start getting deleted
     */
    fun addLog(crashlyticsLog: CrashlyticsLog)

    fun addAndCommitLog(exception: Exception, crashlyticsLog: CrashlyticsLog)

    fun addAndCommitLog(exception: Exception, crashlyticsLogList: List<CrashlyticsLog>)

    /**
     * can add a max of 64 keys, each key value pair not exceeding 1kB
     */
    fun addCustomKey(keyName: String, keyValue: String)
}

data class CrashlyticsLog(
    val tag: String,
    val message: String
)

/**
 * Performs given action, and reports to crashlytics if any exception is encountered and rethrows
 * the exception for calling side to handle it
 */
@Throws(Throwable::class)
suspend fun <T>CrashlyticsLogger.runAndReportIfException(action: suspend () -> T): T {
    return try {
        action()
    } catch (t: Throwable) {
        this.reportHandledException(t)
        throw t
    }
}

/**
 * Performs given action, and reports to crashlytics if any exception is encountered and returns null
 * in case of exception
 */
suspend fun <T>CrashlyticsLogger.runSafelyAndReportIfException(action: suspend () -> T): T? {
    return try {
        this.runAndReportIfException(action)
    } catch (t: Throwable) {
        null
    }
}