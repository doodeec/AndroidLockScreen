package com.doodeec.lockscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.Arrays;

/**
 * LockScreen component
 * Overrides dialog fragment
 * uses {@link android.support.v7.widget.RecyclerView} widget
 *
 * @author Dusan Doodeec Bartos
 */
public class LockScreen extends DialogFragment {

    private static final String BUNDLE_REAL_VALUE = "realVal";
    private static final String BUNDLE_CURRENT_VALUE = "val";
    private static final String BUNDLE_CURRENT_HINT = "hint";
    private static final String BUNDLE_FULLSCREEN = "fullscreen";
    private static final String BUNDLE_SETUP = "setup";

    /**
     * Dialog themes
     */
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    private int mTheme = THEME_LIGHT;
    private boolean mCancelable = false;
    private boolean mFullscreen = true;
    private boolean mSetup = false;
    private CharSequence mHint = "Enter PIN";
    private CharSequence mRealValue;
    private StringBuilder mValue = new StringBuilder("");
    private TextView mValueTextView;
    private View mLayoutView;
    private View mValueSeparator;

    // empty initial listener
    private static PINDialogListener mListener;

    /**
     * Sets listener
     *
     * @param listener callback to execute after successful PIN entry
     *                 null to disable callback
     */
    public void setListener(PINDialogListener listener) {
        mListener = listener;
    }

    /**
     * Sets whether lock screen is cancelable/not-cancelable
     *
     * @param cancelable true to be able to cancel lock screen
     *                   by default, lock screen is not cancellable
     */
    public void setCancelableDialog(boolean cancelable) {
        mCancelable = cancelable;
        this.setCancelable(cancelable);
    }

    /**
     * Sets hint to be displayed on the lock screen
     * Can vary for different cases (Enter PIN, Repeat PIN...)
     *
     * @param hint hint
     */
    public void setHint(String hint) {
        mHint = hint;
    }

    /**
     * Sets hint to be displayed on the lock screen
     * Can vary for different cases (Enter PIN, Repeat PIN...)
     *
     * @param hint hint string
     */
    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    /**
     * Sets real PIN value to compare with
     *
     * @param realPIN real PIN value
     */
    public void setRealValue(CharSequence realPIN) {
        mRealValue = realPIN;
    }

    /**
     * Sets fragment to be fullscreen, or dialog style
     * Dialog is fullscreen by default
     *
     * @param isFullscreen true to show as fullscreen
     */
    public void setFullscreen(boolean isFullscreen) {
        mFullscreen = isFullscreen;
    }

    /**
     * Sets fragment to be in setup mode
     * setup mode is used when new PIN is created
     *
     * @param isSetup true to be in setup mode
     */
    public void setSetup(boolean isSetup) {
        mSetup = isSetup;
    }

    /**
     * Sets dialog theme
     * possible variants are {@link LockScreen#THEME_DARK} and {@link LockScreen#THEME_LIGHT}
     *
     * @param theme theme to apply
     */
    private void setTheme(int theme) {
        mTheme = theme == THEME_DARK ? THEME_DARK : THEME_LIGHT;
    }

    /**
     * Updates lock screen settings all at once
     *
     * @param realPIN    PIN to compare user entry to
     * @param hint       hint
     * @param cancelable is lock dialog cancellable
     * @param fullScreen is lock dialog fullscreen
     */
    public void updateSettings(String realPIN, PINDialogListener listener, String hint,
                               Boolean cancelable, Boolean fullScreen, boolean setup) {
        setRealValue(realPIN);
        if (hint != null) {
            setHint(hint);
        }
        if (cancelable != null) {
            setCancelableDialog(cancelable);
        }
        if (fullScreen != null) {
            setFullscreen(fullScreen);
        }
        setSetup(setup);
        setListener(listener);
    }

    /**
     * Make this dialog fullscreen
     *
     * @param savedInstanceState saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRealValue = savedInstanceState.getString(BUNDLE_REAL_VALUE);
            mFullscreen = savedInstanceState.getBoolean(BUNDLE_FULLSCREEN);
            mSetup = savedInstanceState.getBoolean(BUNDLE_SETUP);
            mHint = savedInstanceState.getString(BUNDLE_CURRENT_HINT);
            mValue = new StringBuilder();
            mValue.append(savedInstanceState.getString(BUNDLE_CURRENT_VALUE));
        }

        if (mFullscreen) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        this.setCancelable(mCancelable);

        if (!mFullscreen) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutView = inflater.inflate(R.layout.lock_screen, container);

        mValueTextView = (TextView) mLayoutView.findViewById(R.id.pin_value);
        mValueSeparator = mLayoutView.findViewById(R.id.separator);
        RecyclerView numbersGridView = (RecyclerView) mLayoutView.findViewById(R.id.numbers_grid);

        if (mValueTextView == null || numbersGridView == null) {
            throw new AssertionError("Lock screen has invalid layout");
        }

        // set value and hint
        mValueTextView.setHint(mHint);
        mValueTextView.setText(mValue);

        // initialize numbers grid
        final LockGridAdapter adapter = new LockGridAdapter(getActivity());
        numbersGridView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        numbersGridView.setAdapter(adapter);
        numbersGridView.setHasFixedSize(true);
        numbersGridView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        switch (adapter.getItemType(position)) {
                            // char clicked, append it to the value
                            case LockGridValues.NUMBER_TYPE:
                                if (mValue.length() < 4) {
                                    mValue.append(adapter.getItem(position));
                                    refreshValueText();
                                }
                                break;

                            // delete the last character from PIN
                            case LockGridValues.BACK_TYPE:
                                if (mValue.length() > 0) {
                                    mValue.deleteCharAt(mValue.length() - 1);
                                }
                                refreshValueText();
                                break;

                            // submit entered PIN and clear the value
                            case LockGridValues.SUBMIT_TYPE:
                                submitPIN();
                                mValue = new StringBuilder("");
                                break;
                        }
                    }
                })
        );

        return mLayoutView;
    }

    /**
     * Save values to Bundle when changing orientation
     *
     * @param outState state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_REAL_VALUE, mRealValue.toString());
        outState.putString(BUNDLE_CURRENT_VALUE, mValue.toString());
        outState.putString(BUNDLE_CURRENT_HINT, mHint.toString());
        outState.putBoolean(BUNDLE_FULLSCREEN, mFullscreen);
        outState.putBoolean(BUNDLE_SETUP, mSetup);

        super.onSaveInstanceState(outState);
    }

    private void updateTheme() {
        //TODO
    }

    /**
     * Mask currently entered PIN with asterisk signs
     */
    private void refreshValueText() {
        char[] maskedCode = new char[mValue.length()];
        Arrays.fill(maskedCode, '*');
        mValueTextView.setText(String.valueOf(maskedCode));
    }

    /**
     * Submits PIN
     * Takes care of Error handling
     * If PIN is correctly entered, it runs registered callback
     */
    private void submitPIN() {
        if (mListener != null) {
            if (mSetup) {
                mListener.onPINSetup(mValue.toString());
                getDialog().dismiss();
            } else if (mValue.toString().equals(mRealValue)) {
                mListener.onPINEntered();
                getDialog().dismiss();
            } else {
                mListener.onWrongEntry();
                mValueTextView.setText("");
            }
        }
    }

    @Override
    public void onDestroy() {
        // reset runnable
        mListener = null;
        super.onDestroy();
    }

    public interface PINDialogListener {
        void onPINEntered();

        void onPINSetup(String pin);

        void onWrongEntry();
    }
}
