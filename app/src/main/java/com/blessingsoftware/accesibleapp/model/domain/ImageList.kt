package com.blessingsoftware.accesibleapp.model.domain

import android.graphics.Bitmap
import android.net.Uri

data class ImageList(
    var listOfSelectedImages:List<Uri> = emptyList(),
    val listOfSelectedBitmap:List<Bitmap> = emptyList()
)