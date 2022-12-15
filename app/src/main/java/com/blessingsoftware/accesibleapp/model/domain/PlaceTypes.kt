package com.blessingsoftware.accesibleapp.model.domain

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.blessingsoftware.accesibleapp.R

enum class PlaceTypes {

    COMERCIO, RESTAURANTE, HOTEL, PUNTO_INTERES, LUGAR_PUBLICO, LUGAR_PRIVADO, ENTIDAD_ESTATAL, ZONA_ENTRETENIMIENTO, ESTACIONAMIENTO, OTROS;

    fun description () : String {
        return when(this) {
            COMERCIO -> "Comercio"
            RESTAURANTE -> "Restaurante"
            HOTEL -> "Hotel"
            PUNTO_INTERES -> "Punto de Interés"
            LUGAR_PUBLICO -> "Lugar Público"
            LUGAR_PRIVADO -> "Lugar Privado"
            ENTIDAD_ESTATAL -> "Entidad Estatal"
            ZONA_ENTRETENIMIENTO -> "Zona de Entretenimiento"
            ESTACIONAMIENTO -> "Estacionamiento"
            OTROS -> "Otros"
        }
    }

    fun placePainterResource (): Int {
        return when(this) {
            COMERCIO -> R.drawable.comercio
            RESTAURANTE -> R.drawable.restaurante
            HOTEL -> R.drawable.hotel
            PUNTO_INTERES -> R.drawable.punto_interes
            LUGAR_PUBLICO -> R.drawable.publico
            LUGAR_PRIVADO -> R.drawable.privado
            ENTIDAD_ESTATAL -> R.drawable.estatal
            ZONA_ENTRETENIMIENTO -> R.drawable.entretenimiento
            ESTACIONAMIENTO -> R.drawable.estacionamiento
            OTROS -> R.drawable.otros
        }
    }

    fun placeIcon (): Int {
        return when(this) {
            COMERCIO -> R.drawable.comerce_icon
            RESTAURANTE -> R.drawable.restaurant_icon
            HOTEL -> R.drawable.hotel_icon
            PUNTO_INTERES -> R.drawable.interest_point_icon
            LUGAR_PUBLICO -> R.drawable.public_place_icon
            LUGAR_PRIVADO -> R.drawable.private_place_icon
            ENTIDAD_ESTATAL -> R.drawable.goverment_icon
            ZONA_ENTRETENIMIENTO -> R.drawable.entertaiment_icon_2
            ESTACIONAMIENTO -> R.drawable.parking_icon
            OTROS -> R.drawable.others_icon
        }
    }

}