package com.blessingsoftware.accesibleapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.model.session.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.main.MainScreen
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.reviewsuggestions.ReviewSuggestionViewModel
import com.blessingsoftware.accesibleapp.util.TrackingUtility
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog

@AndroidEntryPoint
class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {
    private val loginViewModel by viewModels<AuthViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()
    //private val suggestionViewModel by viewModels<MakeSuggestionViewModel>()
    //private val reviewSuggestionViewModel by viewModels<ReviewSuggestionViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccesibleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //Composable que contiene la el navigator y la pantalla principal de la aplicación
                    MainScreen(
                        loginViewModel = loginViewModel,
                        homeViewModel = homeViewModel,
                        rememberNavController()
                    )
                }
            }
            //isLocationOn(suggestionViewModel)
            prepLocationUpdates()
        }
    }

    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //suggestionViewModel.startLocationUpdates()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        if (TrackingUtility.hasLocationPermissions(this)) {
            //suggestionViewModel.permissionGrand(true)
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Debe aceptar los permisos de ubicacion para que la aplicacion funcione correctamente",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            when (requestCode) {
                0 -> Toast.makeText(
                    this,
                    "La aplicación necesita permisos de ubicacion para funcionar correctamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            0 -> {
                //suggestionViewModel.permissionGrand(true)
                //suggestionViewModel.startLocationUpdates()
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun isLocationOn(suggestionViewModel: MakeSuggestionViewModel): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        suggestionViewModel.setGPSStatus(mGPS)
        Log.d("gps ", mGPS.toString())
        return mGPS
    }

}


