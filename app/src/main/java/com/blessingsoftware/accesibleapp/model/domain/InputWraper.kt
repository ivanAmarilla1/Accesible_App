package com.blessingsoftware.accesibleapp.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//no se usa
@Parcelize
data class InputWrapper(
    val value: String = "",
    val errorId: Int? = null
) : Parcelable