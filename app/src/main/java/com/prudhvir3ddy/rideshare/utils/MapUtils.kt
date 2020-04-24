package com.prudhvir3ddy.rideshare.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.model.LatLng
import com.prudhvir3ddy.rideshare.R
import kotlin.math.abs
import kotlin.math.atan

object MapUtils {

  fun getCarBitmap(context: Context): Bitmap {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_car)
    return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
  }
  fun getDestinationBitmap(): Bitmap {
    val height = 20
    val width = 20
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = Color.BLACK
    paint.style = Paint.Style.FILL
    paint.isAntiAlias = true
    canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
    return bitmap
  }

  fun getRotation(start: LatLng, end: LatLng): Float {
    val latDifference: Double = abs(start.latitude - end.latitude)
    val lngDifference: Double = abs(start.longitude - end.longitude)
    var rotation = -1F
    when {
      start.latitude < end.latitude && start.longitude < end.longitude -> {
        rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
      }
      start.latitude >= end.latitude && start.longitude < end.longitude -> {
        rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
      }
      start.latitude >= end.latitude && start.longitude >= end.longitude -> {
        rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
      }
      start.latitude < end.latitude && start.longitude >= end.longitude -> {
        rotation =
          (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
      }
    }
    return rotation
  }

}