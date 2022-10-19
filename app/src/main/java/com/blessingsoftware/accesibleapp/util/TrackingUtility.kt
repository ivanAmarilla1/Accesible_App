package com.blessingsoftware.accesibleapp.util

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {

    //Corrobora si ya se ha concedido los permisos de ubicacion
    fun hasLocationPermissions(context: Context) =
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )


    //ACCESS_BACKGROUND_LOCATION para acceder a la ubicacion en segundo plano, primero se debe pedir FINE_LOCATION o COARSE_LOCATION y solo funciona en API 29 para arriba
    /*fun hasBackgroundLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            true
        }*/

}