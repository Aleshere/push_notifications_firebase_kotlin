package com.raywenderlich.android.drinkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      // Your code
      button_retrieve_token.setOnClickListener {
          // Get token
          if ( checkGooglePlayServices() ) {
              // [START retrieve_current_token]
              FirebaseInstanceId.getInstance().instanceId
                      .addOnCompleteListener(OnCompleteListener { task ->
                          if (!task.isSuccessful) {
                              Log.w(TAG, getString(R.string.token_error), task.exception)
                              return@OnCompleteListener
                          }

                          // Get new Instance ID token
                          val token = task.result?.token

                          // Log and toast
                          val msg = getString(R.string.token_prefix, token)
                          Log.d(TAG, msg)
                          Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                      })
              // [END retrieve_current_token]
          } else {
              //You won't be able to send notifications to this device
              Log.w(TAG, "Device doesn't have google play services")
          }
      }

      val bundle = intent.extras
      if (bundle != null) {
          text_view_notification.text = bundle.getString("text")
      }

    }


    override fun onStart() {
      super.onStart()

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, IntentFilter("MyData"))
    }

    override fun onStop() {
      super.onStop()
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(messageReceiver)

    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            text_view_notification.text = intent.extras?.getString("message")
        }
    }

    companion object {
      private const val TAG = "MainActivity"
    }

}

