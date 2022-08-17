package com.android.internal.telephony


/**
 * Created on agosto.
 * year 2022 .
 */
interface ITelephony {
    fun endCall(): Boolean
    fun answerRingingCall()
    fun silenceRinger()
}