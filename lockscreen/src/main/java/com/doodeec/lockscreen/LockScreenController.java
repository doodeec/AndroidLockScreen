package com.doodeec.lockscreen;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Controller for LockScreen widget
 * All interaction should be managed through this class
 *
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

    /**
     * Opens PIN screen to ask for PIN
     * PIN has to be set before, or it will be the default one ("")
     *
     * @param context         context
     * @param fragmentManager fragment manager to use when showing Lock screen
     * @param hintId          string resource id to show as a hint
     * @param cancelable      true to make PIN dialog cancellable
     * @param fullScreen      true to make PIN dialog fullscreen
     */
    public static void askForPIN(final Context context, FragmentManager fragmentManager,
                                 Integer hintId, Boolean cancelable, Boolean fullScreen) {
        askForPINInternal(context, fragmentManager, hintId, cancelable, fullScreen, false);
    }

    /**
     * Sets new PIN
     *
     * @see #askForPIN(android.content.Context, android.support.v4.app.FragmentManager, Integer, Boolean, Boolean)
     */
    public static void setupPIN(Context context, FragmentManager fragmentManager, Boolean fullScreen) {
        askForPINInternal(context, fragmentManager, R.string.setup_pin_hint, true, fullScreen, true);
    }

    /**
     * Prompt PIN dialog to unlock app
     *
     * @param context    context
     * @param fm         fragment manager
     * @param hintId     text to display when PIN field is empty
     * @param cancelable true if dialog can be cancelled
     */
    private static void askForPINInternal(final Context context,
                                          FragmentManager fm,
                                          Integer hintId, Boolean cancelable, Boolean fullScreen,
                                          boolean setup) {
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        String hint = hintId != null ? context.getString(hintId) : null;

        if (fragment == null) {
            LockScreen lockScreen = new LockScreen();

            lockScreen.show(fm, FRAGMENT_TAG);
            lockScreen.updateSettings(mPIN, hint, cancelable, fullScreen, setup);
        } else if (fragment instanceof LockScreen) {
            ((LockScreen) fragment).updateSettings(mPIN, hint, cancelable, fullScreen, setup);
        }
    }
}
