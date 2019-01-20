@file:Suppress("DEPRECATION")

package com.example.flashlight

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast


class MainActivity() : AppCompatActivity(), Parcelable {

    val  CAMERA_SERVICE = 200
    private var flashLightStatus: Boolean = false
    private var btAction: ImageButton? = null
    private var tvStatus: TextView? = null

    constructor(parcel: Parcel) : this() {
        flashLightStatus = parcel.readByte() != 0.toByte()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btAction = findViewById(R.id.btAction)
        tvStatus = findViewById(R.id.tvStatus)
        tvStatus!!.text = "ON"

        btAction!!.setOnClickListener {
            val permissions = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (permissions != PackageManager.PERMISSION_GRANTED)
                    setupPermissions()
                else {
                    openFlashLight()
                }
            } else {
                openFlashLight()
            }
        }
    }


    private fun setupPermissions() {
        requestPermissions(this , arrayOf(Manifest.permission.CAMERA) , this.CAMERA_SERVICE)
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val PERMISSION = null
        when (requestCode) {
            PERMISSION -> {
                if (grantResults.isEmpty() || !grantResults[0].equals(PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                } else {
                    openFlashLight()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openFlashLight() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        if (!flashLightStatus) {
            try {
                cameraManager.setTorchMode(cameraId, true)
                btAction!!.setImageDrawable(getDrawable(R.drawable.on_icon))
                tvStatus!!.text = "ON"
                flashLightStatus = true

            } catch (e: CameraAccessException) {
            }
        } else {
            try {
                cameraManager.setTorchMode(cameraId, false)
                btAction!!.setImageDrawable(getDrawable(R.drawable.off_icon))
                tvStatus!!.text = "OFF"
                flashLightStatus = false
            } catch (e: CameraAccessException) {
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (flashLightStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

}
