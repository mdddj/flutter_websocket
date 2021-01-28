// 工具类
import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_socket/flutter_socket.dart';
import 'package:flutter_socket/result_model.dart';

import 'connect_close_model.dart';

class FlutterWebSocketUtil {
  FlutterWebSocketUtil._();

  static final FlutterWebSocketUtil _instance = FlutterWebSocketUtil._();

  factory FlutterWebSocketUtil() => _instance;

  static FlutterWebSocketUtil get instance => FlutterWebSocketUtil();

  //-----------------------------------------------------

  EventChannel _eventChannel = EventChannel('flutter_socket_plugin');
  StreamSubscription _stream;

  //-----------------------------------------------------

  // 连接
  Future<void> connect(
      {@required String url,
      ConnectError onError,
      ConnectClose onClose,
      ConnectOpen onOpen,
      MessageHandle onMessage}) async {
    _stream = _eventChannel.receiveBroadcastStream().listen((event) {
      final json = event as String;
      if (json.isNotEmpty) {
        final result = socketResultModelFromJson(json);
        switch (result.messageType) {
          case 'connectError': // 连接失败
            if (onError != null) onError(result.data);
            break;
          case 'connectClose':
            final data = result.data;
            final error = socketConnectCloseModelFromJson(data);
            if (onClose != null) onClose(error);
            break;
          case 'connectSuccess':
            if (onOpen != null) onOpen(result.data);
            break;
          case 'connectMessage':
            if (onMessage != null) onMessage(result.data);
            break;
          default:
            break;
        }
      }
    });
    await FlutterSocket.connect(url: url);
  }

  /// 断开连接
  Future<void> close() async {
    await FlutterSocket.connectColse();
    _stream.cancel();
  }

  /// 开启心跳
  Future<void> openHeart()async{
    await FlutterSocket.openHeart();
  }

  /// 关闭心跳
  Future<void> closeHeart()async{
    await FlutterSocket.closeHeart();
  }

  /// 发送消息
  Future<void> send([String message])async{
    if(message!=null){
      await FlutterSocket.send(message);
    }
  }

}

/// 连接失败回调
typedef ConnectError = void Function(String message);

/// 连接被关闭回调
/// 服务器主动断开连接,或者意外断开回调
typedef ConnectClose = void Function(SocketConnectCloseModel closeDetail);

/// 连接成功回调,只会调用一次
typedef ConnectOpen = void Function(String successUrl);

/// 收到消息回调
typedef MessageHandle = void Function(String message);
