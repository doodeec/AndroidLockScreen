package com.doodeec.lockscreen;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

/**
 * @author Dusan Doodeec Bartos
 */
@SuppressWarnings("unused")
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
    private static final String FRAGMENT_NAME = "lock_fragment";

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
     * Sets delay for locking
     *
     * @param delay delay in seconds
     */
    public static void setPINDelay(int delay) {
        mLockDelay = delay;
    }

    /**
     * @see #askForPINInternal(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean, Boolean)
     */
    public static void askForPIN(ActionBarActivity context, Runnable runnable) {
        askForPINInternal(context, runnable, null, null, null);
    }

    /**
     * @see #askForPINInternal(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean, Boolean)
     */
    public static void askForPIN(ActionBarActivity context, Runnable runnable, Boolean cancelable) {
        askForPINInternal(context, runnable, null, cancelable, null);
    }

    /**
     * @see #askForPINInternal(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean, Boolean)
     */
    public static void askForPIN(ActionBarActivity context, Runnable runnable, Integer hintId) {
        askForPINInternal(context, runnable, hintId, null, null);
    }

    /**
     * @see #askForPINInternal(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean, Boolean)
     */
    public static void askForPIN(ActionBarActivity context, Runnable runnable, Integer hintId, Boolean cancelable) {
        askForPINInternal(context, runnable, hintId, cancelable, null);
    }

    /**
     * @see #askForPINInternal(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean, Boolean)
     */
    public static void askForPIN(ActionBarActivity context, Runnable runnable, Integer hintId, Boolean cancelable, Boolean fullScreen) {
        askForPINInternal(context, runnable, hintId, cancelable, fullScreen);
    }

    /**
     * Prompt PIN dialog to unlock app
     *
     * @param context    context
     * @param runnable   runnable to execute after successful PIN entered
     * @param hintId     text to display when PIN field is empty
     * @param cancelable true if dialog can be cancelled
     */
    private static void askForPINInternal(ActionBarActivity context, Runnable runnable, Integer hintId, Boolean cancelable, Boolean fullScreen) {
        FragmentManager fm = context.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_NAME);

        if (fragment == null) {
            LockScreen lockScreen = new LockScreen();

            lockScreen.updateSettings(mPIN, runnable, hintId, cancelable, fullScreen);
            lockScreen.show(fm, FRAGMENT_NAME);
        } else if (fragment instanceof LockScreen) {
            ((LockScreen) fragment).updateSettings(mPIN, runnable, hintId, cancelable, fullScreen);
        }
    }
}
