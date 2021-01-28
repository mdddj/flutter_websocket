import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_socket/connect_close_model.dart';
import 'package:flutter_socket/flutter_socket.dart';
import 'package:flutter_socket/flutter_socket_util.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _url = 'ws://192.168.199.63:8088/websocket/3/phone';

  // String _url = 'ws://192.168.199.63:8088/phone';

  final util = FlutterWebSocketUtil();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Container(
          height: MediaQueryData.fromWindow(window).size.height,
          width: MediaQueryData.fromWindow(window).size.width,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              renderBtn("初始化连接", () {
                util.connect(
                    url: _url,
                    onClose: (SocketConnectCloseModel detail) {
                      print('是否远程地址:${detail.remote}');
                      print('连接被断开code:${detail.code}');
                      print('连接被断开:${detail.message}');
                    },
                    onMessage: (String message) {
                      print('收到消息:$message');
                    },
                    onOpen: (String url) {
                      print('连接成功,地址是:$url');
                    },
                    onError: (String message) {
                      print('连接失败:$message');
                    });
              }),
              renderBtn("断开连接", (){
                util.close();
              }),
              renderBtn("开启心跳", (){
                util.openHeart();
              }),
              renderBtn("检测是否已经连接", (){
                FlutterSocket.isOpen();
              }),
              renderBtn("是否打开了GPS设置", ()async{
              final isOpen = await  FlutterSocket.gpsIsOpen();
              print('gps是否打开:$isOpen');
              }),
              renderBtn("打开gps设置页面", (){
                FlutterSocket.openGPSSystemPage();
              })
            ],
          ),
        ),
      ),
    );
  }

  // 渲染按钮
  Widget renderBtn(String text, Function onTap) {
    return MaterialButton(
        color: Colors.blue,
        onPressed: onTap,
        child: Text(
          '$text',
          style: TextStyle(color: Colors.white),
        ));
  }
}
