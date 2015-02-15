package com.doodeec.lockscreen;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Lock Grid adapter
 * Adapter for displaying grid of numbers (plus functional buttons) to enter PIN
 * Based on {@link android.support.v7.widget.RecyclerView}
 *
 * @author Dusan Doodeec Bartos
 * @see android.support.v7.widget.RecyclerView.Adapter
 */
public class LockGridAdapter extends RecyclerView.Adapter<LockGridViewHolder> {

    private Activity mContext;
    private String[][] mValues;

    public LockGridAdapter(Activity context) {
        mContext = context;
        mValues = LockGridValues.gridValues;
    }

    @Override
    public LockGridViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new LockGridViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.lock_grid_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(LockGridViewHolder lockGridViewHolder, int i) {
        String code = mValues[i][1];

        lockGridViewHolder.setValue(mValues[i][0]);

        // submit and back have smaller font-size
        if (code.equals(LockGridValues.SUBMIT_TYPE) || code.equals(LockGridValues.BACK_TYPE)) {
            lockGridViewHolder.setTextSize(26);
        } else {
            lockGridViewHolder.setTextSize(40);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public String getItemType(int position) {
        return mValues[position][1];
    }

    public String getItem(int position) {
        return mValues[position][0];
    }
}
