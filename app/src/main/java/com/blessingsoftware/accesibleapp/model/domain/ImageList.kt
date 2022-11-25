package com.blessingsoftware.accesibleapp.model.domain

import android.net.Uri

data class ImageList(
    val listOfSelectedImages:List<Uri> = emptyList()
)