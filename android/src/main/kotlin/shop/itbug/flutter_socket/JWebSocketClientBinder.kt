package shop.itbug.flutter_socket

import android.os.Binder

class JWebSocketClientBinder : Binder() {

    fun getService(): JWebSocketService {
        return  JWebSocketService()
    }
}