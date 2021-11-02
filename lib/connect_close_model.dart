// To parse this JSON data, do
//
//     final socketConnectCloseModel = socketConnectCloseModelFromJson(jsonString);

import 'dart:convert';

SocketConnectCloseModel socketConnectCloseModelFromJson(String str) => SocketConnectCloseModel.fromJson(json.decode(str));

String socketConnectCloseModelToJson(SocketConnectCloseModel data) => json.encode(data.toJson());

/// socket 连接失败详情
class SocketConnectCloseModel {
  SocketConnectCloseModel({
    this.code,
    this.message,
    this.remote,
  });

  /// 错误码
  int? code;

  /// 错误详情
  String? message;

  /// 是否远程连接地址
  bool? remote;

  factory SocketConnectCloseModel.fromJson(Map<String, dynamic> json) => SocketConnectCloseModel(
    code: json["code"],
    message: json["message"],
    remote: json["remote"].toString() == "true",
  );

  Map<String, dynamic> toJson() => {
    "code": code,
    "message": message,
    "remote": remote,
  };
}
