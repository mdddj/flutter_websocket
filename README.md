# flutter_socket

flutter连接websocket插件,基于安卓`Java-WebSocket:1.4.0`插件

## 开始使用


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