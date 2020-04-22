package com.prudhvir3ddy.rideshare.ui.maps

import com.google.android.gms.maps.model.LatLng

interface MapsView {
  fun showNearbyCabs(latLngList: List<LatLng>)
}