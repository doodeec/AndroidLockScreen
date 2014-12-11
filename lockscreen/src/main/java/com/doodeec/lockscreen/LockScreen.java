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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * LockScreen component
 * Overrides dialog fragment
 * uses {@link android.support.v7.widget.RecyclerView} widget
 *
 * @author Dusan Doodeec Bartos
 */
public class LockScreen extends DialogFragment {

    private static final String BUNDLE_CURRENT_VALUE = "val";
    private static final String BUNDLE_CURRENT_HINT = "hint";

    private boolean mCancelable = false;
    private CharSequence mHint = "Enter PIN";
    private CharSequence mRealValue;
    private StringBuilder mValue = new StringBuilder("");
    private TextView mValueTextView;

    // empty initial runnable
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    /**
     * Sets success unlocking callback
     *
     * @param runnable callback to execute after successful PIN entry
     *                 null to disable callback
     */
    public void setRunnable(Runnable runnable) {
        if (runnable != null) {
            mRunnable = runnable;
        } else {
            // null disables previously set callback
            // need this when updating lock screen settings
            mRunnable = new Runnable() {
                @Override
                public void run() {
                }
            };
        }
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
     * @param hintId hint resource id
     */
    public void setHint(int hintId) {
        mHint = getResources().getString(hintId);
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
     * Make this dialog fullscreen
     *
     * @param savedInstanceState saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        this.setCancelable(mCancelable);

        if (savedInstanceState != null) {
            mHint = savedInstanceState.getString(BUNDLE_CURRENT_HINT);
            mValue = new StringBuilder();
            mValue.append(savedInstanceState.getString(BUNDLE_CURRENT_VALUE));
        }

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock_screen, container);

        mValueTextView = (TextView) view.findViewById(R.id.pin_value);
        RecyclerView numbersGridView = (RecyclerView) view.findViewById(R.id.numbers_grid);

        if (mValueTextView == null || numbersGridView == null) {
            throw new AssertionError("Lock screen has invalid layout");
        }

        // set value and hint
        mValueTextView.setHint(mHint);
        mValueTextView.setText(mValue);

        // initialize numbers grid
        final LockGridAdapter adapter = new LockGridAdapter(getActivity());
        numbersGridView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        numbersGridView.setLayoutManager(new FixedGridLayoutManager(getActivity(), 3));
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

        return view;
    }

    /**
     * Save values to Bundle when changing orientation
     *
     * @param outState state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_CURRENT_VALUE, mValue.toString());
        outState.putString(BUNDLE_CURRENT_HINT, mHint.toString());

        super.onSaveInstanceState(outState);
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
        if (mValue.toString().equals(mRealValue)) {
            // fire callback when correctly entered
            mRunnable.run();

            getDialog().dismiss();
        } else {
            notifyWrongValue();
        }
    }

    /**
     * Notifies user about entering a wrong value
     */
    private void notifyWrongValue() {
        mValueTextView.setText("");
        Toast.makeText(getActivity(), "Wrong PIN! Try again.", Toast.LENGTH_SHORT).show();
    }
}
