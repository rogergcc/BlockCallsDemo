package com.rogergcc.blockcallsdemo.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import java.lang.reflect.Method


/**
 * Created on febrero.
 * year 2023 .
 */
class OutgoingCallMonitor : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        // Recibe intencion cuando se realizan una Llamada Saliente
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            // Obtener Números y Almacenarlos como Variables
            var outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            // Bloquear Llama Saliente


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                (context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).also { telephonyService ->
                    Log.e(Companion.TAG, "TELECOM_SERVICE")
                    telephonyService.endCall()
                }
            }else{
                var tm =
                    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).also { telephonyManagerIt ->
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
    fun disconnectCall() {
        try {
            val serviceManagerName = "android.os.ServiceManager"
            val serviceManagerNativeName = "android.os.ServiceManagerNative"
            val telephonyName = "com.android.internal.telephony.ITelephony"
            val telephonyClass: Class<*>
            val telephonyStubClass: Class<*>
            val serviceManagerClass: Class<*>
            val serviceManagerNativeClass: Class<*>
            val telephonyEndCall: Method
            val telephonyObject: Any
            val serviceManagerObject: Any
            telephonyClass = Class.forName(telephonyName)
            telephonyStubClass = telephonyClass.classes[0]
            serviceManagerClass = Class.forName(serviceManagerName)
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName)
            val getService: Method =  // getDefaults[29];
                serviceManagerClass.getMethod("getService", String::class.java)
            val tempInterfaceMethod: Method = serviceManagerNativeClass.getMethod(
                "asInterface",
                IBinder::class.java
            )
            val tmpBinder = Binder()
            tmpBinder.attachInterface(null, "fake")
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder)
            val retbinder = getService.invoke(serviceManagerObject, "phone") as IBinder
            val serviceMethod: Method =
                telephonyStubClass.getMethod("asInterface", IBinder::class.java)
            telephonyObject = serviceMethod.invoke(null, retbinder)
            telephonyEndCall = telephonyClass.getMethod("endCall")
            telephonyEndCall.invoke(telephonyObject)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(Companion.TAG, "FATAL ERROR: could not connect to telephony subsystem")


        }
    }
    companion object {
        private const val TAG = "OutgoingCallMonitor"
    }
}