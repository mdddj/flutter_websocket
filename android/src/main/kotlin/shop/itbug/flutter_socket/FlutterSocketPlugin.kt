package shop.itbug.flutter_socket

import android.app.Activity
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.google.gson.Gson
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


class FlutterSocketPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel

    private val gson: Gson = Gson()
    private val util: Util = Util()

    //-------------------------------------
    private lateinit var client: JWebSocketClient
    private lateinit var binder: JWebSocketClientBinder
    private var service: JWebSocketService? = null
    private lateinit var serviceConnect: ServiceConnection

    //-------------------------------------
    private val channelName = "flutter_socket_plugin"
    private lateinit var eventChannel: EventChannel
    private lateinit var sinks: EventChannel.EventSink
    private val sinksThread: Handler = Handler(Looper.getMainLooper())


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_socket")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, channelName)
        eventChannel.setStreamHandler(eventStream)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "connect" -> {
                val url = call.argument<String>("url")
                if (url != null) {
                    Log.d(FlutterSocketPlugin::class.java.simpleName, "初始化连接:$url")
                    onStartConnectService(url)
                }

            }
            "closeConnect" -> {
                service?.closeConnect()
            }
            "openHeart" -> {
                if (service != null) {
                    if (isOpen()) {
                        service!!.openHeart()
                    } else {
                        Log.d(FlutterSocketPlugin::class.java.simpleName, "请先连接socket")
                    }
                } else {
                    Log.d(FlutterSocketPlugin::class.java.simpleName, "请先连接socket")
                }

            }
            "closeHeart" -> {
                if (service != null) {
                    if (isOpen()) {
                        Log.d("11111111111","断开心跳");
                        service!!.closeHeart()
                    }
                }
            }
            "send" -> {
                val message = call.argument<String>("message")
                if (message != null) {
                    service?.send(message)
                }
            }
            "isOpen" -> {
                result.success(isOpen())
            }
            "isOpenGPS" -> {
                val isOpenGps = util.isOpenGPSSetting(context)
                result.success(isOpenGps)
            }
            "openGPSSystemSetting" -> {
                // 打开gps设置
                util.openGPS(context, activity)
                result.success(true)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    // 是否已经连接
    private fun isOpen(): Boolean {
        var res = false
        if (service != null) {
            if (service!!.client != null) {
                res = service!!.client!!.isOpen
            }
        }
        return res
    }

    // 开启服务
    private fun onStartConnectService(url: String) {


        serviceConnect = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                // 插件和服务绑定
                if (p1 != null) {
                    binder = p1 as JWebSocketClientBinder
                    service = binder.getService()
                    connect(url)
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                // 销毁
            }

        }
        bindService()
    }

    // 连接
    private fun connect(socketUrl: String) {
        service?.initSocketClient(socketUrl, { url -> connectSuccess(url) }, { code, reason, remote -> connectClose(code, reason, remote) }, { message: String -> connectError(message) }) { message: String ->
            connectMessage(message)
        }
        client = service?.client!!
    }


    // socket 连接成功事件
    private fun connectSuccess(url: String) {
        val result = SinksResult("connectSuccess", url)
        sendToFlutter(gson.toJson(result))
    }

    // socket 连接被关闭事件
    private fun connectClose(code: Int, reason: String?, remote: Boolean) {
        var msg = "未知原因"
        if (reason != null) {
            msg = reason
        }
        val errorResult = SinksResultWithConnectError(code, msg, remote)
        val errorStr = gson.toJson(errorResult)
        val result = SinksResult("connectClose", errorStr)
        sendToFlutter(gson.toJson(result))
    }

    // socket 连接失败事件
    private fun connectError(message: String) {
        val result = SinksResult("connectError", message)
        sendToFlutter(gson.toJson(result))
    }

    // socket 接收到消息事件
    private fun connectMessage(message: String) {
        val result = SinksResult("connectMessage", message)
        sendToFlutter(gson.toJson(result))
    }

    // 初始化通道
    private val eventStream: EventChannel.StreamHandler = object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            if (events != null) {
                sinks = events
            }
        }

        override fun onCancel(arguments: Any?) {
            // 通道被销毁
        }

    }

    // 绑定服务
    private fun bindService() {
        val intent = Intent(context, JWebSocketService::class.java)
        context.bindService(intent, serviceConnect, Context.BIND_AUTO_CREATE)
    }

    // 发送数据返回flutter app
    private fun sendToFlutter(json: String) {
        sinksThread.post {
            sinks.success(json)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }


}
