package com.example.mapdungeon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 1001
    private lateinit var location: Location
    private val requestPermissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        location = Location(this, this)

        val button: Button = this.findViewById(R.id.button4) as Button
        button.setOnClickListener {
            Log.d("debug", "button clicked")
            location.getLocation()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= 23) {
                enableMyLocation()
            } else {
                showErrorDialog("位置情報権限エラー")
            }
        }
        mMap.isMyLocationEnabled = true
    }

    fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            requestPermissions(
                this,
                requestPermissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Log.d("debug", "権限の再要求")
                    requestPermissions(
                        this,
                        requestPermissions,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    Log.d("debug", "再要求不可、設定画面を開かせる")
                    showDialogToGetPermission()
                }
            }
        }
    }

    private fun showDialogToGetPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("権限要求")
            .setMessage(
                "このアプリの動作には位置情報、インターネット接続権限の許可が必要です。"
            )

        builder.setPositiveButton("設定を開く") { dialogInterface, i ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        builder.setNegativeButton("アプリを閉じる") { dialogInterface, i ->
            this.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("エラー")
            .setMessage(errorMessage)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            this.finish()
        }
    }
}