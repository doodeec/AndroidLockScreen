package com.doodeec.lockscreen;

import android.app.Activity;
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
@SuppressWarnings("unused")
public class LockScreen extends DialogFragment implements RecyclerItemClickListener.OnItemClickListener {

    private static final String BUNDLE_REAL_VALUE = "realVal";
    private static final String BUNDLE_CURRENT_VALUE = "val";
    private static final String BUNDLE_CURRENT_HINT = "hint";
    private static final String BUNDLE_CANCELABLE = "cancelable";
    private static final String BUNDLE_FULLSCREEN = "fullscreen";
    private static final String BUNDLE_SETUP = "setup";

    private boolean mCancelable = false;
    private boolean mFullscreen = true;
    private boolean mSetup = false;
    private CharSequence mHint = "Enter PIN";
    private CharSequence mRealValue;
    private StringBuilder mValue = new StringBuilder("");
    private TextView mValueTextView;

    private RecyclerView mNumbersGridView;
    private LockGridAdapter mAdapter;
    private RecyclerItemClickListener mItemClickListener;

    // empty initial listener
    private static IPINDialogListener mListener;
    private static int sActiveColor = -1;

    public static void setActiveColor(int color) {
        sActiveColor = color;
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
     * Updates lock screen settings all at once
     *
     * @param realPIN    PIN to compare user entry to
     * @param hint       hint
     * @param cancelable is lock dialog cancellable
     * @param fullScreen is lock dialog fullscreen
     */
    public void updateSettings(String realPIN, String hint,
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
            mCancelable = savedInstanceState.getBoolean(BUNDLE_CANCELABLE);
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
        View layoutView = inflater.inflate(R.layout.lock_screen, container);

        mValueTextView = (TextView) layoutView.findViewById(R.id.pin_value);
        mNumbersGridView = (RecyclerView) layoutView.findViewById(R.id.numbers_grid);

        if (mValueTextView == null || mNumbersGridView == null) {
            throw new AssertionError("Lock screen has invalid layout");
        }

        // set value and hint
        mValueTextView.setHint(mHint);
        refreshValueText();

        // initialize numbers grid
        mItemClickListener = new RecyclerItemClickListener(getActivity(), this);
        mAdapter = new LockGridAdapter(getActivity(), sActiveColor);
        mNumbersGridView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mNumbersGridView.setAdapter(mAdapter);
        mNumbersGridView.setHasFixedSize(true);
        mNumbersGridView.addOnItemTouchListener(mItemClickListener);

        return layoutView;
    }

    /**
     * Save values to Bundle when changing orientation
     *
     * @param outState state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_REAL_VALUE, mRealValue != null ? mRealValue.toString() : "");
        outState.putString(BUNDLE_CURRENT_VALUE, mValue.toString());
        outState.putString(BUNDLE_CURRENT_HINT, mHint.toString());
        outState.putBoolean(BUNDLE_FULLSCREEN, mFullscreen);
        outState.putBoolean(BUNDLE_CANCELABLE, mCancelable);
        outState.putBoolean(BUNDLE_SETUP, mSetup);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (getDialog() != null) {
            // do whatever
            switch (mAdapter.getItemType(position)) {
                // char clicked, append it to the value
                case LockGridValues.NUMBER_TYPE:
                    if (mValue.length() < 4) {
                        mValue.append(mAdapter.getItem(position));
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
                mNumbersGridView.removeOnItemTouchListener(mItemClickListener);
                dismiss();
            } else if (mValue.toString().equals(mRealValue)) {
                mListener.onPINEntered();
                mNumbersGridView.removeOnItemTouchListener(mItemClickListener);
                dismiss();
            } else {
                mListener.onWrongEntry();
                mValueTextView.setText("");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IPINDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IPINDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface to be used as a callback object
     */
    public interface IPINDialogListener {
        /**
         * Fired when entered PIN is correct
         */
        void onPINEntered();

        /**
         * Used for setting up a new PIN
         *
         * @param pin entered PIN
         */
        void onPINSetup(String pin);

        /**
         * Fired when entered PIN is not correct
         */
        void onWrongEntry();
    }
}
