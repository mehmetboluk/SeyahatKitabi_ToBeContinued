package com.example.seyahatkitabi.view

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.seyahatkitabi.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.seyahatkitabi.databinding.ActivityMapsBinding
import com.example.seyahatkitabi.model.Place
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registeredLauncher()
        sharedPreferences = this.getSharedPreferences("com.example.seyahatkitabi", MODE_PRIVATE)

        trackBoolean = false
        selectedLatitude = 0.0
        selectedLongitude = 0.0

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
        mMap.setOnMapLongClickListener(this)
         //casting
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                if(trackBoolean == false){
                    val userLocation = LatLng(p0.latitude,p0.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15F))
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }

            }
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //permission denied
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Permission is needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

        }else{
            //permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation != null){
                val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
            }
            mMap.isMyLocationEnabled = true
        }


        /*
        // Add a marker in kadikoy and move the camera
        val kadikoy = LatLng(40.98607766016042, 29.0367448964486)
        mMap.addMarker(MarkerOptions().position(kadikoy).title("Sukru Saracoglu Stadi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kadikoy, 15F))

         */
    }

    private fun registeredLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                //permission granted
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }
                    mMap.isMyLocationEnabled = true
                }
            }else{
                //permission denied
                Toast.makeText(this@MapsActivity, "Permission is needed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng?) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0!!))
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
    }

    fun save(view : View){

        val place = Place(binding.etPlaceName.text.toString(), selectedLatitude!!,selectedLongitude!!)
    }
    fun delete(view: View){

    }
}