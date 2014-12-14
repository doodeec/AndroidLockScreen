# PIN lock screen for Android

## Description
If you ever needed a lock screen widget - screen/dialog for entering a PIN, you pretty much ended
with implementing it on your own, because there is no such thing in system natively.

This library should make it easier to use PIN locking in your app.

## Support
Supports API10+ (Lollipop included), so pretty much almost every device with Android. Library
is based on a RecyclerView component, it uses v7 support library (make sure you have support
library included in your SDK)

## Usage
### Include library
Simply inculde `lockscreen` in your gradle file

### Set the lock screen
In your application, call `LockScreenController.setPIN(String)` first (this will set the real PIN
the component will compare user entry to).

### Show the lock screen
Call `LockScreenController.askForPIN(Context, Callback runnable)` to show the lock screen. If user
enters correct PIN, callback runnable is triggered.

## Licence
Released under Apache V2.0 licence.

## Author
Dusan Bartos - [doodeec](http://doodeec.com)
