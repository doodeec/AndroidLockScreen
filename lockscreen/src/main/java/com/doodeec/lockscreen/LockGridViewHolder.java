package com.doodeec.lockscreen;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Lock Grid viewHolder
 * Based on {@link android.support.v7.widget.RecyclerView}
 *
 * @author Dusan Doodeec Bartos
 * @see android.support.v7.widget.RecyclerView.ViewHolder
 */
public class LockGridViewHolder extends RecyclerView.ViewHolder {

    TextView mItemValue;

    public LockGridViewHolder(View v, Drawable background) {
        super(v);
        mItemValue = (TextView) v.findViewById(R.id.grid_value);

        if (mItemValue == null) {
            throw new AssertionError("Grid item has invalid layout");
        }

        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            itemView.setBackgroundDrawable(background);
        } else {
            itemView.setBackground(background);
        }
    }

    public void setValue(String value) {
        mItemValue.setText(value);
    }

    public void setTextSize(int size) {
        mItemValue.setTextSize(size);
    }
}
