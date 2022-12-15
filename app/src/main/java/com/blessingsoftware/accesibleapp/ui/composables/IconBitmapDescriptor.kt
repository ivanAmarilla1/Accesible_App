package com.blessingsoftware.accesibleapp.ui.composables

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)

    val resizedBitmap = resizeBitmap(bm, 100)

    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}



fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
    try {
        if (source.height >= source.width) {
            if (source.height <= maxLength) { // if image height already smaller than the required height
                return source
            }

            val aspectRatio = source.width.toDouble() / source.height.toDouble()
            val targetWidth = (maxLength * aspectRatio).toInt()
            val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            return result
        } else {
            if (source.width <= maxLength) { // if image width already smaller than the required width
                return source
            }

            val aspectRatio = source.height.toDouble() / source.width.toDouble()
            val targetHeight = (maxLength * aspectRatio).toInt()

            val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            return result
        }
    } catch (e: Exception) {
        return source
    }
}