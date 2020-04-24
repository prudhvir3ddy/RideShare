package com.prudhvir3ddy.rideshare.ui.maps

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.mindorks.ridesharing.simulator.WebSocket
import com.mindorks.ridesharing.simulator.WebSocketListener
import com.prudhvir3ddy.rideshare.data.network.NetworkService
import com.prudhvir3ddy.rideshare.utils.Constants
import org.json.JSONObject

class MapsPresenter(
  private val networkService: NetworkService
) : WebSocketListener {

  companion object {
    private const val TAG = "MapsPresenter"
  }

  private var view: MapsView? = null
  private lateinit var webSocket: WebSocket

  fun onAttach(view: MapsView?) {
    this.view = view
    webSocket = networkService.createWebSocket(this)
    webSocket.connect()
  }

  fun onDetach() {
    webSocket.disconnect()
    view = null
  }

  override fun onConnect() {
    Log.d(TAG, "onConnect")
  }

  override fun onMessage(data: String) {
    Log.d(TAG, "onMessage data : $data")
    val jsonObject = JSONObject(data)
    when (jsonObject.getString(Constants.TYPE)) {
      Constants.NEAR_BY_CABS -> {
        handleOnMessageNearbyCabs(jsonObject)
      }
      Constants.CAB_BOOKED -> {
        view?.informCabBooked()
      }
      Constants.PICK_UP_PATH, Constants.TRIP_PATH -> {
        val jsonArray = jsonObject.getJSONArray("path")
        val pickUpPath = arrayListOf<LatLng>()
        for (i in 0 until jsonArray.length()) {
          val lat = (jsonArray.get(i) as JSONObject).getDouble("lat")
          val lng = (jsonArray.get(i) as JSONObject).getDouble("lng")
          val latLng = LatLng(lat, lng)
          pickUpPath.add(latLng)
        }
        view?.showPath(pickUpPath)
      }
      Constants.LOCATION -> {
        val latCurrent = jsonObject.getDouble("lat")
        val lngCurrent = jsonObject.getDouble("lng")
        view?.updateCabLocation(LatLng(latCurrent, lngCurrent))
      }
      Constants.CAB_ARRIVED -> {
        view?.informCabArrived()
      }
      Constants.CAB_IS_ARRIVING -> {
        view?.informCabIsArriving()
      }
      Constants.TRIP_START -> {
        view?.informTripStart()
      }
      Constants.TRIP_END -> {
        view?.informTripEnd()
      }
    }
  }

  override fun onDisconnect() {
    Log.d(TAG, "onDisconnect")
  }

  override fun onError(error: String) {
    Log.d(TAG, "onError : $error")
    val jsonObject = JSONObject(error)
    when (jsonObject.getString(Constants.TYPE)) {
      Constants.ROUTES_NOT_AVAILABLE -> {
        view?.showRoutesNotAvailableError()
      }
      Constants.DIRECTION_API_FAILED -> {
        view?.showDirectionApiFailedError(
          "Direction API Failed : " + jsonObject.getString(
            Constants.ERROR
          )
        )
      }
    }
  }

  private fun handleOnMessageNearbyCabs(jsonObject: JSONObject) {
    val nearbyCabLocations = arrayListOf<LatLng>()
    val jsonArray = jsonObject.getJSONArray(Constants.LOCATIONS)
    for (i in 0 until jsonArray.length()) {
      val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
      val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
      val latLng = LatLng(lat, lng)
      nearbyCabLocations.add(latLng)
    }
    view?.showNearbyCabs(nearbyCabLocations)
  }

  fun requestNearbyCabs(latLng: LatLng) {
    val jsonObject = JSONObject()
    jsonObject.put(Constants.TYPE, Constants.NEAR_BY_CABS)
    jsonObject.put(Constants.LAT, latLng.latitude)
    jsonObject.put(Constants.LNG, latLng.longitude)
    webSocket.sendMessage(jsonObject.toString())
  }

  fun requestCab(pickUpLatLng: LatLng, dropLatLng: LatLng) {
    val jsonObject = JSONObject()
    jsonObject.put(Constants.TYPE, Constants.REQUEST_CAB)
    jsonObject.put(Constants.PICK_UP_LAT, pickUpLatLng.latitude)
    jsonObject.put(Constants.PICK_UP_LNG, pickUpLatLng.longitude)
    jsonObject.put(Constants.DROP_LAT, dropLatLng.latitude)
    jsonObject.put(Constants.DROP_LNG, dropLatLng.longitude)
    webSocket.sendMessage(jsonObject.toString())
  }
}