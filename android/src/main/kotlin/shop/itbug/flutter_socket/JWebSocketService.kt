package shop.itbug.flutter_socket

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


/// socket 长连接服务
class JWebSocketService : Service() {


    var client: JWebSocketClient? = null

    //灰色保活
    class GrayInnerService : Service() {
        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            startForeground(1001, Notification())
            stopForeground(true)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }
    }


    override fun onCreate() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("myService", "版本更新服务通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, FlutterSocketPlugin::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "myService")
                .setContentTitle("hi")
                .setContentText("hello")
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build()
        startForeground(1001, notification)
        super.onCreate()
    }


    override fun onBind(p0: Intent?): IBinder {
        return JWebSocketClientBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(JWebSocketService::class.java.simpleName, "服务已启动:onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(JWebSocketService::class.java.simpleName, "服务进程被杀死")
        super.onDestroy()
    }

    /**
     * 初始化websocket连接
     */
    fun initSocketClient(url: String,
                         success: (url: String) -> Unit,
                         close: (code: Int, reason: String?, remote: Boolean) -> Unit,
                         error: (message: String) -> Unit,
                         doMessage: (message: String) -> Unit) {
        val uri = URI.create(url)
        client = object : JWebSocketClient(uri) {
            override fun onMessage(message: String?) {
                if (message != null) {
                    doMessage(message)
                }
            }

            override fun onOpen(handshakedata: ServerHandshake?) {
                success(url)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                close(code, reason, remote)
            }

            override fun onError(ex: Exception?) {
                if (ex != null) {
                    error(ex.toString())
                } else {
                    error("连接失败:未知原因")
                }
            }

        }
        connect()
    }

    /**
     * 连接websocket
     */
    private fun connect() {
        object : Thread() {
            override fun run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client?.connectBlocking()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 开启心跳
     */
    fun openHeart() {
        mHandler.postDelayed(heartBeatRunnable, sendTime) // 开启心跳
    }

    private val sendTime = (30 * 1000).toLong()
    private val mHandler: Handler = Handler()
    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            if (client != null) {
                if (client!!.isClosed) {
                    reconnectWs()
                }
            }
            Log.d(FlutterSocketPlugin::class.java.simpleName, "正在检测连接是否断开,如果断开将重新连接")
            mHandler.postDelayed(this, sendTime)
        }
    }

    /**
     * 发送消息
     */

    fun send(message: String) {
        if (client != null && client!!.isOpen) {
            client!!.send(message)
        }
    }


    /**
     * 开启重连
     */
    private fun reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable)
        object : Thread() {
            override fun run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连")
                    client?.reconnectBlocking()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 断开连接
     */
    fun closeConnect() {
        try {
            client?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
        mHandler.removeCallbacks(heartBeatRunnable)
    }

}