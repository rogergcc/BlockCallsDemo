package com.rogergcc.blockcallsdemo


/**
 * Created on agosto.
 * year 2022 .
 */
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.internal.telephony.ITelephony
import java.lang.reflect.Method


class CallMonitorBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private var number: String? = null
    }

    //4.2 Android
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val callingControl = 2
            if (callingControl == 0) return

            when (intent.action) {
                Intent.ACTION_NEW_OUTGOING_CALL -> {
                    number = intent.extras?.getString(Intent.EXTRA_PHONE_NUMBER)

                    Log.e("CallMonitor", "ACTION_NEW_OUTGOING_CALL")
                }
                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                    Log.e("CallMonitor", "ACTION_PHONE_STATE_CHANGED")
                    val extraState = intent.extras?.getString(TelephonyManager.EXTRA_STATE)
                    if (extraState == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                        Log.e("CallMonitor", "EXTRA_STATE_OFFHOOK")
                        Log.e("CallMonitor", "EXTRA_STATE_OFFHOOK number: $number")
                        Thread.sleep(5000)
                        //log 5 s
                        checkEndCall(context, number)
                        number = null
                    } else if (extraState == TelephonyManager.EXTRA_STATE_RINGING
                        && callingControl == 2
                    ) {
                        Log.e("CallMonitor", "callingControl 2 EXTRA_STATE_RINGING")
                        checkEndCall(
                            context, intent.extras?.getString(
                                TelephonyManager.EXTRA_INCOMING_NUMBER
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CallMonitor", "eror: ${e.message}")
        }
    }

    private fun checkEndCall(context: Context, phoneNumber: String?) {

        val telephonyService: ITelephony


        phoneNumber?.also { phone ->

            //log phone
            Log.e("CallMonitor ", "checkEndCall Check end call: $phoneNumber")
            Log.e("CallMonitor ", "permitidos: ${NegocioTelefono.getPhoneBook().toString()}")

            if (!NegocioTelefono.getPhoneBook().contains(phone)) {
                Log.e("CallMonitor ", "no esta en ls lista: $phoneNumber")
                //Android 9
                when {
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P -> {
                        Log.e("CallMonitor ", "> android 9")
                        (context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).also {
                            Log.e("CallMonitor ", "TELECOM_SERVICE")
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ANSWER_PHONE_CALLS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            it.endCall()
                        }
                    }
                    else -> {
                        Log.e("CallMonitor ", "< Android 9")

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

                        (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager)
                            .also { telephonyManagerIt ->
                                Class.forName(telephonyManagerIt.javaClass.name).getDeclaredMethod(
                                    "getITelephony"
                                ).also { iTelephonyIt ->
                                    iTelephonyIt.isAccessible = true
                                    (iTelephonyIt.invoke(telephonyManagerIt) as ITelephony)// internal Android API
                                        .endCall()
                                }
                            }
                    }
                }
            }
        }
    }
}

class NegocioTelefono {

    companion object {
        fun getPhoneBook(): List<String> {
            val numerosPermitosLista = mutableListOf("94124").apply {
                add("51916706921")
            }
            return (numerosPermitosLista)
        }
    }
}
