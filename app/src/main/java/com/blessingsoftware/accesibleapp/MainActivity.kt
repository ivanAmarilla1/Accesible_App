package com.blessingsoftware.accesibleapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.blessingsoftware.accesibleapp.model.domain.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.blessingsoftware.accesibleapp.model.domain.Constants.SHOW_CURRENT_LOCATION
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.main.MainScreen
import com.blessingsoftware.accesibleapp.util.TrackingUtility
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog

@AndroidEntryPoint
class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {


    private val loginViewModel by viewModels<AuthViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Si el usuario esta logueado, carga los lugares en el mapa
            loginViewModel.currentUser?.let {
                homeViewModel.getPlaces()
            }
            AccesibleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        loginViewModel = loginViewModel,
                        homeViewModel = homeViewModel,
                        rememberNavController()
                    )
                }
            }
            prepLocationUpdates(homeViewModel)
        }
    }

    private fun prepLocationUpdates(viewModel: HomeViewModel) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startLocationUpdates()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        if (TrackingUtility.hasLocationPermissions(this)) {
            homeViewModel.permissionGrand(true)
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
                homeViewModel.permissionGrand(true)
                homeViewModel.startLocationUpdates()
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

}


