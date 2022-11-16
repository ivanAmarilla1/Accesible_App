package com.blessingsoftware.accesibleapp.model.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

//Obtiene la ubicacion actual y lo devuelve en formato live data del objeto LocationDetails que yo creé
class LocationLiveData(var context: Context) : LiveData<LocationDetails>() {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    //Cuando se ejecuta un pedido de location
    override fun onActive() {
        super.onActive()
        //Codigo generado automaticamente para checkear si se tienen acceso a los permisos de ubicacion, sino no ejecuta nada
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //obtiene la ultima ubicacion conocida
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location ->location.also {
                setLocationData(it)
            }
        }

    }

    //Actualizacion de la ubicacion para tener la ubicacion en tiempo real
    internal fun startLocationUpdates() {
        //otro tochazo autogenerado para verificar permisos
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //Pedido de actualizacion de ubicacion
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    //pasa el valor de la ubicacion a la clase LocationDetails para poder utilizarla en la aplicacion
    private fun setLocationData(location: Location?) {
        location?.let { location ->
            value = LocationDetails(location.latitude.toString(), location.longitude.toString())
        }

    }

    //Cuando no se esta ejecutando un pedido
    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    //callback para el update de la ubicacion
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    //Objeto que especifica el request de location, que se realizara cada 1 minuto
    companion object {
        val ONE_MINUTE : Long = 60000
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = ONE_MINUTE
            fastestInterval = ONE_MINUTE/4
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }
}