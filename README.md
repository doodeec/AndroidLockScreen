# PIN lock screen for Android

## Version 1.3.0

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

    dependencies {
        ...
        compile 'com.doodeec.utils:lockScreen:1.2.0'
    }

### Set the PIN
In your application, call `LockScreenController.setPIN(String)` first (this will set the real PIN
the component will compare user entry to).
You will have to use `IPINDialogListener` in the activity, where PIN dialog is called from (If
dialog is invoked from fragment, it is the activity, which fragmentManager is used for displaying
the dialog).

### Setup the PIN (by user)
Call `LockScreenController.setupPIN(Context, FragmentManager, Hint resource id,
Cancellable flag, Fullscreen flag)` to show the lock screen. If user enters correct PIN,
interface (IPINDialogListener) in the calling activity is triggered.

### Show the lock screen
Call `LockScreenController.askForPIN(Context, FragmentManager, Fullscreen flag)`
to show the lock screen. If user enters new PIN, interface (IPINDialogListener) in the calling
activity is triggered. New PIN is then available through `LockScreenController.getPIN()` call.

## Licence
Released under Apache V2.0 licence.

## Author
Dusan Bartos - [doodeec](http://doodeec.com)
