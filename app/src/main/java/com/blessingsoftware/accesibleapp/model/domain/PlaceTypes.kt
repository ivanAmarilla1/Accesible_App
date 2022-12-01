package com.blessingsoftware.accesibleapp.model.domain

enum class PlaceTypes {

    ESTACIONAMIENTO, COMERCIO, LUGAR_PUBLICO, LUGAR_PRIVADO, ENTIDAD_ESTATAL, RESTAURANTE, HOTEL, PUNTO_INTERES, ZONA_ENTRETENIMIENTO, OTROS;

    fun description () : String {
        return when(this) {
            ESTACIONAMIENTO -> "Estacionamiento"
            COMERCIO -> "Comercio"
            LUGAR_PUBLICO -> "Lugar Público"
            LUGAR_PRIVADO -> "Lugar Privado"
            ENTIDAD_ESTATAL -> "Entidad Estatal"
            RESTAURANTE -> "Restaurante"
            HOTEL -> "Hotel"
            PUNTO_INTERES -> "Punto de Interés"
            ZONA_ENTRETENIMIENTO -> "Zona de Entretenimiento"
            OTROS -> "Otros"
        }
    }

}