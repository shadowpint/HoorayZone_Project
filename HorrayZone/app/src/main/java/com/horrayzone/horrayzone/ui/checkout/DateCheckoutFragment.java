package com.horrayzone.horrayzone.ui.checkout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.ui.CheckoutActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateCheckoutFragment extends CheckoutFragment implements OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {
    private static final String TAG = "PaymentCheckoutFragment";
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final String STATE_CURRENT_CARD_TYPE = "state_current_card_type";
    MaterialCalendarView widget;
    private Button mProceedToReviewButton;
    private int mCurrentday;
    private CalendarDay mSelectedDate;
    private CheckoutActivity mActivity;
    private View mCurrentFocusView;
    private int mCurrentMonth;

    // Credit card icons wrapped in a Level List Drawable.
    private int mCurrentYear;

    public static DateCheckoutFragment newInstance() {
        return new DateCheckoutFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (CheckoutActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout_date, container, false);
        widget = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        widget.setOnDateLongClickListener(this);
        widget.setOnMonthChangedListener(this);
        mProceedToReviewButton = (Button) rootView.findViewById(R.id.proceed_to_review_button);

        mProceedToReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("pay_price", mActivity.getSelectedPrice().getName());
                proceedToReview();
            }
        });


        if (savedInstanceState != null) {
//            mCurrentCardType = CreditCard.CardType.fromValue(
//                    savedInstanceState.getInt(STATE_CURRENT_CARD_TYPE));
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putInt(STATE_CURRENT_CARD_TYPE, mCurrentCardType.getValue());
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        mCurrentMonth = date.getMonth();
        mCurrentYear = date.getYear();
        mCurrentday = date.getDay();
        mSelectedDate = date;
        mProceedToReviewButton.setEnabled(true);
    }

    @Override
    public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
        final String text = String.format("%s is available", FORMATTER.format(date.getDate()));
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions

    }

    private void proceedToReview() {

        mActivity.proceedToReview(mSelectedDate);
    }


}
