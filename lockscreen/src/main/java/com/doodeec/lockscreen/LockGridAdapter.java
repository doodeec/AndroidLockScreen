package com.doodeec.lockscreen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
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

    private Context mContext;
    private String[][] mValues;
    private int mActiveColor;
    private float dp;

    public LockGridAdapter(Context context, int activeColor) {
        mContext = context;
        mValues = LockGridValues.gridValues;
        mActiveColor = activeColor;

        dp = mContext.getResources().getDisplayMetrics().density;
    }

    @Override
    public LockGridViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new LockGridViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.lock_grid_item, viewGroup, false),
                getBackgroundColorScheme(mContext.getResources().getColor(android.R.color.transparent),
                        mActiveColor));
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

    /**
     * Sets stateList background drawable for viewHolder (numbers adapter) views
     *
     * @param defaultColor default color
     * @param pressedColor pressed color
     * @return drawable
     */
    private Drawable getBackgroundColorScheme(int defaultColor, int pressedColor) {
        int defaultBorder = Color.argb(30, 0, 0, 0);
        int pressedBorder = Color.argb(80, 0, 0, 0);

        // default state
        GradientDrawable defaultGd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{defaultColor, defaultColor});
        defaultGd.setStroke(1, defaultBorder);
        defaultGd.setCornerRadius(3f * dp);

        // pressed state
        GradientDrawable pressedGd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{pressedColor, pressedColor});
        pressedGd.setStroke(1, pressedBorder);
        pressedGd.setCornerRadius(3f * dp);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedGd);
        drawable.addState(new int[]{}, defaultGd);
        return drawable;
    }
}
