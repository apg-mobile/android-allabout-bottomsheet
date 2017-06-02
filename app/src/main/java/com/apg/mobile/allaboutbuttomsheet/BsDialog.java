package com.apg.mobile.allaboutbuttomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.apg.library.corehelper.Apg;

/**
 * Created by X-tivity on 5/31/2017 AD.
 */

public class BsDialog extends BottomSheetDialogFragment {

    private RelativeLayout bottomSheet;
    private LinearLayout mainContent;
    private RelativeLayout header;
    private FrameLayout footer;
    private Button btnSubmit;
    private EditText edtComment;
    private NestedScrollView scroll;
    private View expandContent;

    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                int tempState;

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        if (btnSubmit != null) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnSubmit.getLayoutParams();
                            layoutParams.gravity = Gravity.BOTTOM;
                            btnSubmit.setLayoutParams(layoutParams);
                            btnSubmit.setTranslationY(0);

                            if (expandContent.getVisibility() != View.VISIBLE) {

                                Animation fadeIn = new AlphaAnimation(0, 1);
                                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                                fadeIn.setDuration(300);
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        expandContent.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                expandContent.startAnimation(fadeIn);
                            }
                        }
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        if (btnSubmit != null) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnSubmit.getLayoutParams();
                            layoutParams.gravity = Gravity.TOP;
                            btnSubmit.setLayoutParams(layoutParams);
                            btnSubmit.setTranslationY(0);
                            expandContent.setVisibility(View.GONE);
                        }

                        Apg.input().hideKeyboard(getActivity(), getDialog());

                    } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                        if (tempState == BottomSheetBehavior.STATE_EXPANDED) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnSubmit.getLayoutParams();
                            layoutParams.gravity = Gravity.TOP;
                            btnSubmit.setLayoutParams(layoutParams);
                            btnSubmit.setTranslationY(getMaxTranslateY());
                        }
                    }

                    tempState = newState;
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, final float slideOffset) {

//                    int peekHeight = BottomSheetBehavior.from(bottomSheet).getPeekHeight();
//                    Log.e("TRANSLATE-Y", getMaxTranslateY() + "");
//                    Log.e("OFFSET", slideOffset + "");
//                    Log.e("CURRENT HEIGHT", ((bottomSheet.getHeight() * slideOffset) + (peekHeight * (1 - slideOffset))) + "");

                    Log.e("SCALE TOP", getMaxTranslateY() * slideOffset + "");
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnSubmit.getLayoutParams();
                    if (layoutParams.gravity == Gravity.BOTTOM) {
                        btnSubmit.setTranslationY(getMaxTranslateY() * Math.abs(slideOffset - 1));
                    } else {
                        btnSubmit.setTranslationY(getMaxTranslateY() * slideOffset);
                    }
                }
            };


    private int getMaxTranslateY() {
        Log.e("FOOTER", footer.getHeight() + "");
        Log.e("BTN", btnSubmit.getHeight() + "");
        return footer.getHeight() - btnSubmit.getHeight();
    }

    public static BsDialog newInstance() {

        Bundle args = new Bundle();

        BsDialog fragment = new BsDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View contentView = View.inflate(getContext(), R.layout.dialog_bottom, null);

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        bottomSheet = (RelativeLayout) contentView.findViewById(R.id.bottomSheet);
        mainContent = ((LinearLayout) contentView.findViewById(R.id.mainContent));
        expandContent = contentView.findViewById(R.id.expandContent);
        header = (RelativeLayout) contentView.findViewById(R.id.header);
        footer = (FrameLayout) contentView.findViewById(R.id.footer);
        btnSubmit = ((Button) contentView.findViewById(R.id.btnSubmit));
        edtComment = ((EditText) contentView.findViewById(R.id.edtComment));
        scroll = ((NestedScrollView) contentView.findViewById(R.id.scroll));


        mBottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());


        if (mBottomSheetBehavior != null) {

            bottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    bottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int peekHeight = 0;

                    peekHeight += header.getHeight();
                    peekHeight += mainContent.getHeight();
                    peekHeight += btnSubmit.getHeight();
                    mBottomSheetBehavior.setPeekHeight(peekHeight);
                }
            });

            // should detect keyboard popup and scroll to bottom...

//            bottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    int heightDiff = bottomSheet.getRootView().getHeight() - bottomSheet.getHeight();
//                    if (heightDiff > dpToPx(getContext(), 128)) { // if more than 200 dp, it's probably a keyboard...
//                        // ... do something here
//                        scroll.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                scroll.fullScroll(View.FOCUS_DOWN);
//                            }
//                        });
//                    }
//                }
//            });

            // setHideable(Boolean) is handled by setCancelable(Boolean) in BottomSheetDialog.Class
            // mBottomSheetBehavior.setHideable(false);
            mBottomSheetBehavior.setSkipCollapsed(false);
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior != null) {

                    int state = mBottomSheetBehavior.getState();

                    if (state == BottomSheetBehavior.STATE_DRAGGING
                            || state == BottomSheetBehavior.STATE_SETTLING) {
                        return;
                    }

                    if (state != BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }
        });

        edtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior != null) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        return dialog;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("KEYBOARD", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // override dialog default setting
        getDialog().setCancelable(false);
    }
}
