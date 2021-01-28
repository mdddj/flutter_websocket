# flutter_socket

flutter连接websocket插件,基于安卓`Java-WebSocket:1.4.0`插件

[![](https://badgen.net/pub/v/flutter_socket)](https://pub.dev/packages/flutter_socket)

## 开始使用

```dart
dependencies:
  flutter_socket: ^0.0.5
```


使用工具类
```dart
final util = FlutterWebSocketUtil();
```

### 1.连接


必填参数:`url` 服务端的socket连接地址

可选参数1:`onClose` 连接被断开回调

可选参数2:`onMessage` 收到消息回调

可选参数3:`onOpen` 连接成功回调,调用一次

可选参数4:`onError` 连接失败回调


**连接后会启动一个后台服务(service),通知栏可能会显示一个正在运行的通知**
```dart


  
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

  
```

### 2.断开连接

```dart
util.close();
```

### 3.开启心跳检测

30秒检测一次
```dart
util.openHeart();
```

### 4.检测是否连接
``` dart
 FlutterSocket.isOpen();
```

### 5.发送消息
```dart
util.send("hello world");
```

## 工具类
```dart
import 'package:erp_system/config/ip.dart';
import 'package:erp_system/utils/extend_util.dart';
import 'package:erp_system/utils/user_util.dart';
import 'package:flutter_socket/connect_close_model.dart';
import 'package:flutter_socket/flutter_socket_util.dart';

class WebSocketUtilsV3 {
  WebSocketUtilsV3._();

  static final WebSocketUtilsV3 _instance = WebSocketUtilsV3._();

  factory WebSocketUtilsV3() => _instance;

  final util = FlutterWebSocketUtil();

  // 初始化socket连接
  Future<void> init() async {
    final user = await getCatchUser();
    if (user != null) {
      final _connectUrl = 'ws://$kHost:$kPort/websocket';
      util.connect(url: _connectUrl, onOpen: onOpen, onClose: onClose, onMessage: onMessage, onError: onError);
    }
  }

  // 连接打开
  void onOpen(String url) {
    log('连接成功啦;$url');
    util.openHeart();
  }

  // 连接关闭
  void onClose(SocketConnectCloseModel detail) {
    log('连接被断开啦:${detail.code}');
  }

  // 收到消息
  void onMessage(String message) {
    log('收到消息啦:$message');
  }

  // 连接失败
  void onError(String message) {
    log('连接错误啦:$message');
  }
}

```

## 运行示例
![](https://static.saintic.com/picbed/huang/2021/01/28/1611804968070.png)