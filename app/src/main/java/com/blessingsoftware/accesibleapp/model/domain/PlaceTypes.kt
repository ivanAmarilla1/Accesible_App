package com.blessingsoftware.accesibleapp.model.domain

enum class PlaceTypes {

    COMERCIO, LUGAR_PUBLICO, LUGAR_PRIVADO, ENTIDAD_ESTATAL, RESTAURANTE, HOTEL, PUNTO_INTERES, ZONA_ENTRETENIMIENTO, ESTACIONAMIENTO, OTROS;

    fun description () : String {
        return when(this) {
            COMERCIO -> "Comercio"
            LUGAR_PUBLICO -> "Lugar Público"
            LUGAR_PRIVADO -> "Lugar Privado"
            ENTIDAD_ESTATAL -> "Entidad Estatal"
            RESTAURANTE -> "Restaurante"
            HOTEL -> "Hotel"
            PUNTO_INTERES -> "Punto de Interés"
            ZONA_ENTRETENIMIENTO -> "Zona de Entretenimiento"
            ESTACIONAMIENTO -> "Estacionamiento"
            OTROS -> "Otros"
        }
    }

}