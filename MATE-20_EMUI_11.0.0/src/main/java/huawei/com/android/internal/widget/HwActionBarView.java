package huawei.com.android.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ActionMenuPresenter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.widget.ActionBarView;

public class HwActionBarView extends ActionBarView {
    public HwActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public int measureChildView(View child, int availableWidth, int childSpecHeight, int spacing) {
        return HwActionBarView.super.measureChildView(child, availableWidth, childSpecHeight, spacing);
    }

    /* access modifiers changed from: protected */
    public LinearLayout initTitleLayout() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void initTitleIcons() {
    }

    /* access modifiers changed from: protected */
    public void initTitleIcons(LinearLayout titleLayout) {
    }

    public void setTitle(CharSequence title) {
    }

    public void setCustomTitle(View view) {
    }

    public void setStartIconVisible(boolean isIcon1Visible) {
    }

    public void setEndIconVisible(boolean isIcon2Visible) {
    }

    public void setStartContentDescription(CharSequence contentDescription) {
    }

    public void setEndContentDescription(CharSequence contentDescription) {
    }

    public void triggerIconsVisible(boolean isIcon1Visible, boolean isIcon2Visible) {
    }

    public void setStartIconImage(Drawable icon1) {
    }

    public void setEndIconImage(Drawable icon2) {
    }

    public void setStartIconListener(View.OnClickListener listener1) {
    }

    public void setEndIconListener(View.OnClickListener listener2) {
    }

    /* access modifiers changed from: protected */
    public ActionMenuPresenter initActionMenuPresenter(Context context) {
        return null;
    }

    /* access modifiers changed from: protected */
    public void deleteExpandedHomeIfNeed() {
    }

    public static class HwHomeView extends ActionBarView.HomeView {
        public HwHomeView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void setShowUp(boolean isUp) {
        }

        public void setShowIcon(boolean isIconShowed) {
            HwActionBarView.super.setShowIcon(false);
        }

        public int getStartOffset() {
            return 0;
        }

        /* access modifiers changed from: protected */
        public void layoutUpView(View view, int upLeft, int upTop, int upRight, int upBottom, int leftMargin, int upOffset) {
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    private static class ButtonState {
        private float mAlpha1;
        private float mAlpha2;
        private boolean mIsEnabled1;
        private boolean mIsEnabled2;
        private boolean mIsUsed1;
        private boolean mIsUsed2;

        private ButtonState() {
        }
    }

    public void saveState(ButtonState state) {
    }

    public void restoreState(ButtonState saved) {
    }

    /* access modifiers changed from: protected */
    public void initTitleAppearance() {
    }

    public static void initTitleAppearance(Context context, TextView title, TextView subTitle) {
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        HwActionBarView.super.onFinishInflate();
    }

    public void invalidateAllViews() {
    }

    public void setSplitViewLocation(int start, int end) {
    }

    /* access modifiers changed from: protected */
    public void updateSplitLocation() {
    }

    public static void invalidateTitleLayout(int gravity, int margin, TextView title, TextView subTitle) {
    }

    public void setSmartColor(ColorStateList iconColor, ColorStateList titleColor) {
    }
}
