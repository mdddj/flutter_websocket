#import "FlutterSocketPlugin.h"
#if __has_include(<flutter_socket/flutter_socket-Swift.h>)
#import <flutter_socket/flutter_socket-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_socket-Swift.h"
#endif

@implementation FlutterSocketPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSocketPlugin registerWithRegistrar:registrar];
}
@end
