package com.rogergcc.blockcallsdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rogergcc.blockcallsdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var smsAndStoragePermissionHandler: RequestPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            handleRequestPermission()
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }

        smsAndStoragePermissionHandler = RequestPermissionHandler(this@MainActivity,
            permissions = setOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE),
            listener = object : RequestPermissionHandler.Listener {
                override fun onComplete(grantedPermissions: Set<String>, deniedPermissions: Set<String>) {
                    Toast.makeText(this@MainActivity, "complete", Toast.LENGTH_SHORT).show()
                    binding.textGranted.text = "Granted: " + grantedPermissions.toString()
                    binding.textDenied.text = "Denied: " + deniedPermissions.toString()
                }

                override fun onShowPermissionRationale(permissions: Set<String>): Boolean {
                    AlertDialog.Builder(this@MainActivity).setMessage("To able to Send Photo, we need SMS and" + " Storage permission")
                        .setPositiveButton("OK") { _, _ ->
                            smsAndStoragePermissionHandler.retryRequestDeniedPermission()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            smsAndStoragePermissionHandler.cancel()
                            dialog.dismiss()
                        }
                        .show()
                    return true // don't want to show any rationale, just return false here
                }

                override fun onShowSettingRationale(permissions: Set<String>): Boolean {
                    AlertDialog.Builder(this@MainActivity).setMessage("Go Settings -> Permission. " + "Make SMS on and Storage on")
                        .setPositiveButton("Settings") { _, _ ->
                            smsAndStoragePermissionHandler.requestPermissionInSetting()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            smsAndStoragePermissionHandler.cancel()
                            dialog.cancel()
                        }
                        .show()
                    return true
                }
            })

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
//                    Manifest.permission.CALL_PHONE
//                ) == PackageManager.PERMISSION_DENIED
//            ) {
//                val permissions =
//                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
//                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE)
//            }
//        }

    }
    private fun handleRequestPermission() {
        smsAndStoragePermissionHandler.requestPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsAndStoragePermissionHandler.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        smsAndStoragePermissionHandler.onActivityResult(requestCode)
    }



//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>?,
//        grantResults: IntArray
//    ) {
//        if (permissions != null) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//        when (requestCode) {
//            PERMISSION_REQUEST_READ_PHONE_STATE -> {
//                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(
//                        this,
//                        "Permission granted: $PERMISSION_REQUEST_READ_PHONE_STATE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Permission NOT granted: $PERMISSION_REQUEST_READ_PHONE_STATE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                return
//            }
//        }
//    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        private val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    }
}