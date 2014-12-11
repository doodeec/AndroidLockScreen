package com.doodeec.lockscreen;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

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
    private static final String FRAGMENT_NAME = "lock_fragment";
    private static final Runnable lockTask = new Runnable() {
        @Override
        public void run() {
            mLocked = true;
        }
    };

    public static boolean isAppLocked() {
        return mLocked;
    }

    public static void lock() {
        mLocked = true;
    }

    public static void unLock() {
        mLocked = false;
    }

    public static void lockWithDelay() {
        lockHandler.removeCallbacks(lockTask);
        lockHandler.postDelayed(lockTask, mLockDelay * 1000);
    }

    public static void setPIN(String newPIN) {
        mPIN = newPIN;
    }

    public static void setPasscodeDelay(int delay) {
        mLockDelay = delay;
    }

    /**
     * @see #askForPasscode(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean)
     */
    public static void askForPasscode(ActionBarActivity context, Runnable runnable) {
        askForPasscode(context, runnable, null, null);
    }

    /**
     * @see #askForPasscode(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean)
     */
    public static void askForPasscode(ActionBarActivity context, Runnable runnable, boolean cancelable) {
        askForPasscode(context, runnable, null, cancelable);
    }

    /**
     * @see #askForPasscode(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean)
     */
    public static void askForPasscode(ActionBarActivity context, Runnable runnable, int hintId) {
        askForPasscode(context, runnable, hintId, null);
    }

    /**
     * @see #askForPasscode(android.support.v7.app.ActionBarActivity, Runnable, Integer, Boolean)
     */
    public static void askForPasscode(ActionBarActivity context, Runnable runnable, int hintId, boolean cancelable) {
        askForPasscode(context, runnable, hintId, cancelable);
    }

    /**
     * Prompt PIN dialog to unlock app
     *
     * @param context    context
     * @param runnable   runnable to execute after successful PIN entered
     * @param hintId     text to display when PIN field is empty
     * @param cancelable true if dialog can be cancelled
     */
    private static void askForPasscode(ActionBarActivity context, Runnable runnable, Integer hintId, Boolean cancelable) {
        FragmentManager fm = context.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_NAME);

        if (fragment == null) {
            LockScreen lockScreen = new LockScreen();

            updateLockScreenSettings(lockScreen, runnable, hintId, cancelable);
            lockScreen.show(fm, FRAGMENT_NAME);
        } else if (fragment instanceof LockScreen) {
            updateLockScreenSettings((LockScreen) fragment, runnable, hintId, cancelable);
        }
    }

    private static void updateLockScreenSettings(LockScreen lockScreen, Runnable runnable, Integer hintId, Boolean cancelable) {
        lockScreen.setRealValue(mPIN);
        if (hintId != null) {
            lockScreen.setHint(hintId);
        }
        if (cancelable != null) {
            lockScreen.setCancelableDialog(cancelable);
        }
        lockScreen.setRunnable(runnable);
    }
}
