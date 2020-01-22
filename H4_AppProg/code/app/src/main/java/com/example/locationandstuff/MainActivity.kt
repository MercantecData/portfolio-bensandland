package com.example.locationandstuff

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : AppCompatActivity() {
    // Permission id for location
    val PERMISSION_ID = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get saved preferences
        val sharedPrefs =
            getSharedPreferences("com.example.locationandstuff", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val theme_id = sharedPrefs.getInt("theme", 0)

        when (theme_id)
        {
            1 -> setTheme(R.style.darkTheme)
            else -> {
                setTheme(R.style.lightTheme)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get dark/light theme switch button
        val toggleBtn = findViewById<Switch>(R.id.toggleTheme)

        // Get state
        toggleBtn.isChecked = sharedPrefs.getBoolean("toggleTheme", false)

        // Create event listener for toggle button
        toggleBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Set state
                editor.putBoolean("toggleTheme", true)
                editor.putInt("theme", 1)
                editor.apply()

                // Set to dark mode
                changeTheme(1)

            } else {
                // Set state
                editor.putBoolean("toggleTheme", false)
                editor.putInt("theme", 0)
                editor.apply()

                // Set to light mode
                changeTheme()
            }
        }

        btnGetLocation.setOnClickListener {
            fetchLocation()
            val text = sharedPrefs.getString("homeAddress", "")
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }

        val homeAddr = findViewById<EditText>(R.id.txtBoxHomeAddr)
        val workAddr = findViewById<EditText>(R.id.txtBoxWorkAddr)
        val btnSaveAddr = findViewById<Button>(R.id.btnSaveAddr)


        btnSaveAddr.setOnClickListener {
            val homeAddr = homeAddr.text
            val workAddr = workAddr.text
            with(sharedPrefs.edit()) {
                putString("homeAddress", homeAddr!!.toString())
                putString("workAddress", workAddr!!.toString())
                apply()
            }
        }
    }

    fun changeTheme(i: Int = -1) {
        val intent = intent
        intent.putExtra("theme", i)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent)
    }

    private fun checkLocationPermissions(): Boolean {
        // Check if app has permission to ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION
        // Returns true/false depending on permission results
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    private fun requestLocationPermissions() {
        // Requests permission to use ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION within the app.
        // This is NOT the same as having location enabled within 'Settings'
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Is called when user grants/denies location permissions within the app
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fetchLocation()
            }
        }
    }

    private fun fetchLocationState(): Boolean {
        // This function checks if location is turned on (from within 'Settings')'
        // - if user grants permission (from our prompt) but this is turned off it is of no use.
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun fetchNewLocationData() {
        // Requests new location data from device.
        // In some instances the location data from the device can be null
        // - if that's the case this function is called to retrieve new data at runtime.
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        // When fetchNewLocationData receives new data it'll call this callBack method
        // This function inserts the newly fetched data into the TextView objects.
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            findViewById<TextView>(R.id.txtLatitude).text = "Latitude: " + mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.txtLongitude).text = "Longitude: " + mLastLocation.longitude.toString()
        }
    }

    private fun fetchLocation() {
        // Main 'location' function. Gets location data by using last entry. If none is found,
        // fetchNewLocationData is called (which will fetch new data)

        if (checkLocationPermissions()) {
            if (fetchLocationState()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                   var location: Location? = task.result
                    if (location == null) {
                        fetchNewLocationData()
                    } else {
                        findViewById<TextView>(R.id.txtLatitude).text = "Latitude: " + location.latitude.toString()
                        findViewById<TextView>(R.id.txtLongitude).text = "Longitude: " + location.longitude.toString()
                    }
                }
            } else {
                // Location isn't enabled. Prompt user for access by creating a 'Toast'
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Location permissions isn't enabled by user
            requestLocationPermissions()
        }
    }
}
