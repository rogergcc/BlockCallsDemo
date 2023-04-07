package com.rogergcc.blockcallsdemo


/**
 * Created on agosto.
 * year 2022 .
 */
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import com.rogergcc.blockcallsdemo.NegocioTelefono.Companion.getPhoneBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CallMonitorBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private var number: String? = null
        private const val TAG = "CallMonitorBroadcastRec"
    }
    //create a function to pass value from fragment to broadcast

    //4.2 Android
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val callingControl = 2 // pasar valor de fragment a broadcast



            //pass value from fragment to broadcast


            //2: bloquear llamadas entrantes y salientes, 1: bloquear llamadas salientes, 0: no bloquear llamadas
            // whitelist llamar solo a los numeros de la lista

            if (callingControl == 0) return

            Log.e(
                TAG,
                "Nº EXTRA_PHONE_NUMBER: ${intent.extras?.getString(Intent.EXTRA_PHONE_NUMBER)}"
            )
            Log.e(
                TAG,
                "Nº EXTRA_INCOMING_NUMBER: ${intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)}"
            )

            when (intent.action) {
                Intent.ACTION_NEW_OUTGOING_CALL -> {
                    number = intent.extras?.getString(Intent.EXTRA_PHONE_NUMBER)

                    Log.e(TAG, "ACTION_NEW_OUTGOING_CALL")
                    checkEndCall(context, number)
                }
                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                    Log.e(TAG, "ACTION_PHONE_STATE_CHANGED")
                    val extraState = intent.extras?.getString(TelephonyManager.EXTRA_STATE)
                    if (extraState == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                        Log.e(TAG, "EXTRA_STATE_OFFHOOK")
                        Log.e(TAG, "EXTRA_STATE_OFFHOOK number: $number")
                        CoroutineScope(Main).launch {
                            delay(5000)
                            checkEndCall(context, number)
                            number = null
                        }
                        //log 5 s
//                        checkEndCall(context, intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER))
//                        checkEndCall(context, number)
                        //number = null
                    } else if (extraState == TelephonyManager.EXTRA_STATE_RINGING
                        && callingControl == 2) {
                        checkEndCall(
                            context,
                            intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "error: ${e.message}")
        }
    }

    //    @SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    private fun checkEndCall(context: Context, phoneNumber: String?) {
        Log.e(TAG, "checkEndCall Check end call before also: $phoneNumber")
        if (phoneNumber == null) Log.i(TAG, "checkEndCall: phone is null")
        phoneNumber.also { phone ->
            //log phone

            Log.e(TAG, "checkEndCall Check end call after also: $phoneNumber")
            Log.e(TAG, "permitidos: ${getPhoneBook()}")
            val isWhiteList = getPhoneBook().contains(phone)
            if (isWhiteList) return

            Log.e(TAG, "no esta en ls lista: $phoneNumber")
            //Android 9

            when {
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P -> {
                    Log.e(TAG, "> android 9")
                    (context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).also { telephonyService ->
                        Log.e(TAG, "TELECOM_SERVICE")

                        telephonyService.endCall()
                    }
                }
                else -> {
                    Log.e(TAG, "< Android 9")

                    //         val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                    //         try {
                    //          val m: Method = tm.javaClass.getDeclaredMethod("getITelephony")
                    //          m.isAccessible = true
                    //          telephonyService = m.invoke(tm) as ITelephony
                    //          if (number != null) {
                    //           telephonyService.endCall()
                    //           Toast.makeText(context, "Ending the call from: $number", Toast.LENGTH_SHORT).show()
                    //          }
                    //         } catch (e: java.lang.Exception) {
                    //          e.printStackTrace()
                    //         }

                    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                        .also { telephonyManagerIt ->
                            Class.forName(telephonyManagerIt.javaClass.name).getDeclaredMethod(
                                "getITelephony"
                            ).also { iTelephonyIt ->
                                iTelephonyIt.isAccessible = true
                                (iTelephonyIt.invoke(telephonyManagerIt) as ITelephony)// internal Android API
                                    .endCall()
                            }
                        }

//                    disconnectCall()
                }
            }

        }
    }

}


class NegocioTelefono {
    companion object {

        fun getPhoneBook(): List<String> {
            val numerousPermitsList = mutableListOf("24124").apply {
                add("51917069215")
//                add("956025075") //ROSE
//                add("972267115")
            }
            return (numerousPermitsList)
        }
    }
}
