import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterSocket {
  static const MethodChannel _channel = const MethodChannel('flutter_socket');

  /// 连接
  static Future<void> connect({@required String url}) async {
    assert(url != null && url.isNotEmpty, 'socket 连接地址不能为空');
    await _channel.invokeMethod('connect', {'url': url});
  }

  /// 断开连接
  static Future<void> connectColse() async {
    await _channel.invokeMethod("closeConnect");
  }

  /// 开启心跳
  static Future<void> openHeart() async {
    await _channel.invokeMethod("openHeart");
  }

  /// 关闭心跳
  static Future<void> closeHeart() async {
    await _channel.invokeMethod("closeHeart");
  }

  /// 发送消息
  static Future<void> send([String message]) async {
    if (message != null) {
      await _channel.invokeMethod("send", {"message": message});
    }
  }

  /// 是否已经连接
  static Future<bool> isOpen() async {
    final result = await _channel.invokeMethod("isOpen");
    return result.toString() == "true";
  }

  /// 是否已经开启gps设置
  static Future<bool> gpsIsOpen() async {
    final result = await _channel.invokeMethod("isOpenGPS");
    return result.toString()=="true";
  }
  
  /// 跳转到GPS系统设置页面
  static Future<void> openGPSSystemPage()async{
    await _channel.invokeMethod("openGPSSystemSetting");
  }
}
