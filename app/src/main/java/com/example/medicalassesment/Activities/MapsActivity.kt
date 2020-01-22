package com.example.medicalassesment.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.medicalassesment.Helper.MyLocation
import com.example.medicalassesment.Helper.MyLocation.LocationResult
import com.example.medicalassesment.R
import com.example.medicalassesment.Utials.Utils
import com.google.android.gms.common.api.GoogleApiClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {
    private val MY_LOCATION_REQUEST_CODE = 200
    private var mMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
    lateinit var markerOptions: MarkerOptions

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true;
            MyLocation().getLocation(this, object : LocationResult() {
                override fun gotLocation(location: Location?) {
                    if (mMap != null) {
                        runOnUiThread {
                            var latLng = LatLng(location?.latitude!!, location.longitude)
                            Log.e("TAG", "LatLang " + Gson().toJson(latLng))
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                            markerOptions = MarkerOptions().position(latLng)
                            mMap?.addMarker(markerOptions)
                        }
                    }
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_LOCATION_REQUEST_CODE
            )
            // Show rationale and request permission.
        }

        mMap?.setOnMapClickListener {
            mMap?.clear()
            markerOptions = MarkerOptions().position(it)
            mMap?.addMarker(markerOptions)
            showDilog(it)
        }
        // Add a marker in Sydney and move the camera
        mMap?.setOnMyLocationButtonClickListener(this);
        mMap?.setOnMyLocationClickListener(this);

    }

    private fun showDilog(latLng: LatLng) {
        var dilog = BottomSheetDialog(this)
        dilog.setContentView(R.layout.map_bottom_sheet_dilog)
        var address = dilog.findViewById<TextView>(R.id.address)
        var use = dilog.findViewById<Button>(R.id.use)
        var clear = dilog.findViewById<Button>(R.id.clear)
        address?.text = Utils.getCompleteAddressString(this, latLng.latitude, latLng.longitude)
        use?.setOnClickListener {
            dilog.dismiss()
            var returnIntent = Intent()
            returnIntent.putExtra("Address", address?.text.toString())
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        }
        clear?.setOnClickListener { dilog.dismiss() }
        if (!dilog.isShowing)
            dilog.show()
    }

    override fun onMyLocationClick(p0: Location) {
        Log.e("TGA", "Location : ${p0.latitude}  ${p0.longitude}")
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                mMap?.isMyLocationEnabled = true;
                MyLocation().getLocation(this, object : LocationResult() {
                    override fun gotLocation(location: Location?) {
                        if (mMap != null) {
                            runOnUiThread {
                                var latLng = LatLng(location?.latitude!!, location.longitude)
                                Log.e("TAG", "LatLang " + Gson().toJson(latLng))
                                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                                markerOptions = MarkerOptions().position(latLng)
                                mMap?.addMarker(markerOptions)
                            }
                        }
                    }
                })
            } else {
                // Permission was denied. Display an error message.
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
