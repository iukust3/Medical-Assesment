package com.example.medicalassesment.uIItems;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.medicalassesment.R;
import com.example.medicalassesment.Utials.ViewAnimation;

public class ExpandCollapseView extends LinearLayout {
    private LinearLayout mContentView;
    private View content;
    private LinearLayout expandLayout;
    private ImageView arrow;
    private View[] children;
    private String title;
    private TextView qustionTittle;

    public ExpandCollapseView(Context context) {
        super(context);
    }

    public ExpandCollapseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniate(context, attrs);
    }

    public ExpandCollapseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniate(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ExpandCollapseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        iniate(context, attrs);
    }

    private void iniate(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandCollapseView);
        if (a.getString(R.styleable.ExpandCollapseView_tittle) != null) {
            title = a.getString(R.styleable.ExpandCollapseView_tittle);
        }
        a.recycle();
        if (fromRecyclerView)
            initialize(context);
    }

    private void initialize(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ExpandCollapseView accordionLayout = (ExpandCollapseView) inflater.inflate(R.layout.layoutexpend_colleps, this, true);
        mContentView = (LinearLayout) accordionLayout.findViewById(R.id.content_layout);
        if (content != null) {
            mContentView.removeAllViews();
            mContentView.addView(content);
        }
        expandLayout = (LinearLayout) accordionLayout.findViewById(R.id.lyt_expand_collapse);
        ViewAnimation.collapsefast(expandLayout);
        arrow = (ImageView) accordionLayout.findViewById(R.id.bt_show);
        qustionTittle = accordionLayout.findViewById(R.id.questionTitle);

        arrow.animate().setDuration(20).rotation(0f);
        accordionLayout.findViewById(R.id.answer_lable).setVisibility(GONE);
      /*  a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandCollapseView);
        if (a.getString(R.styleable.ExpandCollapseView_tittle) != null) {
            TextView subTittle = findViewById(R.id.questionTitle);
            subTittle.setText(a.getString(R.styleable.ExpandCollapseView_tittle));
        }*/
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection();
            }
        });
        if (title != null) {
            qustionTittle.setText(title);

        }
    }

    private void initializeViews(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout accordionLayout = (LinearLayout) inflater.inflate(R.layout.layoutexpend_colleps, null);
        mContentView = (LinearLayout) accordionLayout.findViewById(R.id.content_layout);

        expandLayout = (LinearLayout) accordionLayout.findViewById(R.id.lyt_expand_collapse);
      //  ViewAnimation.collapsefast(expandLayout);
        arrow = (ImageView) accordionLayout.findViewById(R.id.bt_show);
        qustionTittle = accordionLayout.findViewById(R.id.questionTitle);

     //   arrow.animate().setDuration(20).rotation(0f);
        accordionLayout.findViewById(R.id.answer_lable).setVisibility(GONE);
      /*  a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandCollapseView);
        if (a.getString(R.styleable.ExpandCollapseView_tittle) != null) {
            TextView subTittle = findViewById(R.id.questionTitle);
            subTittle.setText(a.getString(R.styleable.ExpandCollapseView_tittle));
        }*/
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection();
            }
        });
        mContentView.removeAllViews();

        if (title != null) {
            qustionTittle.setText(title);

        }
        int i;
        children = new View[getChildCount()];
        for (i = 0; i < getChildCount(); i++) {
            children[i] = getChildAt(i);
        }
        removeAllViews();
        for (i = 0; i < children.length; i++) {
            mContentView.addView(children[i]);
        }

/*

        paragraphBottomMargin = ((LinearLayout.LayoutParams) paragraph.getLayoutParams()).bottomMargin;
        paragraphTopMargin = ((LinearLayout.LayoutParams) paragraph.getLayoutParams()).topMargin;
*/

        addView(accordionLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void addContenteView(View view) {
        this.content = view;
    }

    public void setTittle(String tittle) {
        if (qustionTittle != null)
            qustionTittle.setText(tittle);
    }

    public static boolean fromRecyclerView = false;

    @Override
    protected void onFinishInflate() {
        if(!fromRecyclerView)
        initializeViews(getContext());
        super.onFinishInflate();
    }


    private void toggleSection() {
        boolean show = toggleArrow();
        if (show) {
            ViewAnimation.expand(expandLayout);
        } else {
            ViewAnimation.collapse(expandLayout);
        }
    }

    private boolean toggleArrow() {
        if (arrow.getRotation() == 0f) {
            arrow.animate().setDuration(200).rotation(180f);
            return true;
        } else {
            arrow.animate().setDuration(200).rotation(0f);
            return false;
        }
    }
}
