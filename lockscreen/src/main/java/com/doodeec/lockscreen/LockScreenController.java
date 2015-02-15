package com.doodeec.lockscreen;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * @author Dusan Doodeec Bartos
 */
public class LockScreenController {

    /**
     * Is application locked
     */
    private static boolean mLocked = false;

    /**
     * Delay for app locking
     * Defined in seconds
     * Default 0
     */
    private static int mLockDelay = 0;

    /**
     * PIN for unlocking the app
     */
    private static String mPIN = "";

    private static final Handler lockHandler = new Handler();
    private static final String FRAGMENT_TAG = "lock_fragment";

    /**
     * Locks application
     */
    private static final Runnable lockTask = new Runnable() {
        @Override
        public void run() {
            mLocked = true;
        }
    };

    /**
     * @return true if app is locked
     */
    public static boolean isAppLocked() {
        return mLocked;
    }

    /**
     * Locks app
     */
    public static void lock() {
        mLocked = true;
    }

    /**
     * Unlocks app
     */
    public static void unLock() {
        mLocked = false;
    }

    /**
     * Locks app with defined delay
     * Delay is 0 seconds by default
     */
    public static void lockWithDelay() {
        lockHandler.removeCallbacks(lockTask);
        lockHandler.postDelayed(lockTask, mLockDelay * 1000);
    }

    /**
     * Sets pin to compare entry with
     *
     * @param newPIN pin
     */
    public static void setPIN(String newPIN) {
        mPIN = newPIN;
    }

    /**
     * Gets pin
     *
     * @return pin
     */
    public static String getPIN() {
        return mPIN;
    }

    /**
     * Sets delay for locking
     *
     * @param delay delay in seconds
     */
    public static void setPINDelay(int delay) {
        mLockDelay = delay;
    }

    public static void askForPIN(final ActionBarActivity context, final Runnable runnable, Integer hintId, Boolean cancelable, Boolean fullScreen) {
        askForPINInternal(context, new LockScreen.PINDialogListener() {
            @Override
            public void onPINEntered() {
                runnable.run();
            }

            @Override
            public void onPINSetup(String pin) {

            }

            @Override
            public void onWrongEntry() {
                Toast.makeText(context, "Wrong PIN! Try again.", Toast.LENGTH_SHORT).show();
            }
        }, hintId, cancelable, fullScreen, false);
    }

    public static void setupPIN(ActionBarActivity context, final Runnable runnable, Boolean fullScreen) {
        askForPINInternal(context, new LockScreen.PINDialogListener() {
            @Override
            public void onPINEntered() {
            }

            @Override
            public void onPINSetup(String pin) {
                setPIN(pin);
                runnable.run();
            }

            @Override
            public void onWrongEntry() {

            }
        }, R.string.setup_pin_hint, true, fullScreen, true);
    }

    /**
     * Prompt PIN dialog to unlock app
     *
     * @param context    context
     * @param listener   listener
     * @param hintId     text to display when PIN field is empty
     * @param cancelable true if dialog can be cancelled
     */
    private static void askForPINInternal(final ActionBarActivity context,
                                          LockScreen.PINDialogListener listener,
                                          Integer hintId, Boolean cancelable, Boolean fullScreen,
                                          boolean setup) {
        FragmentManager fm = context.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        String hint = hintId != null ? context.getString(hintId) : null;

        if (fragment == null) {
            LockScreen lockScreen = new LockScreen();

            lockScreen.show(fm, FRAGMENT_TAG);
            lockScreen.updateSettings(mPIN, listener, hint, cancelable, fullScreen, setup);
        } else if (fragment instanceof LockScreen) {
            ((LockScreen) fragment).updateSettings(mPIN, listener, hint, cancelable, fullScreen, setup);
        }
    }
}
