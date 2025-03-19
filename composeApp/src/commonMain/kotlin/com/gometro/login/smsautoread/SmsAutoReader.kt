package com.gometro.login.smsautoread

import kotlinx.coroutines.flow.Flow

interface SmsAutoReader {

    /**
     * Starts SmsAutoReader, which waits for a matching SMS message until timeout (5 minutes)
     */
    fun startSession()

    fun receiveSmsStatus(): Flow<SMSStatus>

}

sealed class SMSStatus {
    data class Success(val msg: String) : SMSStatus()
    data object Timeout : SMSStatus()
    data object Unknown : SMSStatus()
}