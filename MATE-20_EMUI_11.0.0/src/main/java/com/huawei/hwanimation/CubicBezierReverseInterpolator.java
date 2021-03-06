package com.huawei.hwanimation;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

public class CubicBezierReverseInterpolator extends CubicBezierInterpolator {
    public CubicBezierReverseInterpolator(float cx1, float cy1, float cx2, float cy2) {
        super(cx1, cy1, cx2, cy2);
    }

    public CubicBezierReverseInterpolator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CubicBezierReverseInterpolator(Resources res, Resources.Theme theme, AttributeSet attrs) {
        super(res, theme, attrs);
    }

    @Override // com.huawei.hwanimation.CubicBezierInterpolator, android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return 1.0f - getCubicBezierY(SEARCH_STEP * ((float) binarySearch(1.0f - input)));
    }
}
