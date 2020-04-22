package com.prudhvir3ddy.rideshare.data.network

import com.mindorks.ridesharing.simulator.WebSocket
import com.mindorks.ridesharing.simulator.WebSocketListener

class NetworkService {

  fun createWebSocket(webSocketListener: WebSocketListener): WebSocket {
    return WebSocket(webSocketListener)
  }

}