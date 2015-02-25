package com.doodeec.lockscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
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

    private Drawable bitmapOfColor(GradientDrawable source, int color) {

        Bitmap sourceCopy = Bitmap.createBitmap(Math.round(80 * dp), Math.round(60 * dp), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(sourceCopy);
        source.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        source.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
        source.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.DARKEN));
        source.draw(canvas);

        return new BitmapDrawable(mContext.getResources(), sourceCopy);
    }

    public Drawable getBackgroundColorScheme(int defaultColor, int pressedColor) {
        GradientDrawable gradientDrawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.lock_grid_item_default);

        if (gradientDrawable != null) {
            // set icon states
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, bitmapOfColor(gradientDrawable, pressedColor));
            drawable.addState(new int[]{}, bitmapOfColor(gradientDrawable, defaultColor));
            return drawable;
        } else {
            return null;
        }
    }
}
