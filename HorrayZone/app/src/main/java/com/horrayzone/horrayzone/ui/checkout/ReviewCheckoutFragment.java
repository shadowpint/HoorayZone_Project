package com.horrayzone.horrayzone.ui.checkout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.CreditCard;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.ui.CheckoutActivity;
import com.horrayzone.horrayzone.ui.ShoppingCartActivity;
import com.horrayzone.horrayzone.ui.widget.CustomTypefaceSpan;
import com.horrayzone.horrayzone.util.FontUtils;
import com.horrayzone.horrayzone.util.UiUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewCheckoutFragment extends CheckoutFragment {
    private static final String TAG = "ReviewCheckoutFragment";
    private float totalPrice;
    private String cartId;
    private String productId;
    private TextView mEventNameView,mEventTimeView;

    public static ReviewCheckoutFragment newInstance() {
        return new ReviewCheckoutFragment();
    }

    public static final String ARG_ADDRESS_ID = "arg_address_id";
    public static final String ARG_SELECTED_ADDRESS = "arg_selected_address";
    public static final String ARG_CARD_HOLDER = "arg_name_on_card";
    public static final String ARG_CARD_NUMBER = "arg_card_number";
    public static final String ARG_CARD_CVC = "arg_card_cvc";
    public static final String ARG_CARD_EXP_MONTH = "arg_card_exp_month";
    public static final String ARG_CARD_EXP_YEAR = "arg_card_exp_year";

    private MetricAffectingSpan mMediumTypefaceSpan;

    private CheckoutActivity mActivity;

//    private View mPaymentInfoSection;
//    private TextView mCardTypeView;
//    private TextView mCardHolderView;
//    private TextView mCardExpDateView;

    private View mPriceInfoSection;
    private TextView mPriceNameView;
    private TextView mPriceDescriptionView;

    private TextView mPriceView;

    private View mEventDetailsSection;
    private LinearLayout mEventDetailsContainer;

    private View mOrderSummarySection;
    private TextView mSummaryLabelsView;
    private TextView mSummaryValuesView;
    private TextView mOrderTotalView;

    private Button mGetPaymentTokenButton;

    interface ReviewCartProductsQuery extends ShoppingCartActivity.CartProductsQuery {
        int LOADER_ID = 0x1;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_checkout_review, container, false);

//        mPaymentInfoSection = rootView.findViewById(R.id.payment_info_section);
//        mCardTypeView = (TextView) mPaymentInfoSection.findViewById(R.id.card_type_view);
//        mCardHolderView = (TextView) mPaymentInfoSection.findViewById(R.id.card_holder_view);
//        mCardExpDateView = (TextView) mPaymentInfoSection.findViewById(R.id.card_exp_date_view);

        mPriceInfoSection = rootView.findViewById(R.id.price_info_container);
        mPriceNameView = (TextView) mPriceInfoSection.findViewById(R.id.price_name);
        mPriceDescriptionView = (TextView) mPriceInfoSection.findViewById(R.id.price_description);
        mPriceView = (TextView) mPriceInfoSection.findViewById(R.id.event_price);

        mEventDetailsSection = rootView.findViewById(R.id.event_details_section);
        mEventDetailsContainer = (LinearLayout)mEventDetailsSection.findViewById(R.id.event_details_container);
        mEventNameView = (TextView) mEventDetailsSection.findViewById(R.id.event_name);
        mEventTimeView = (TextView) mEventDetailsSection.findViewById(R.id.event_time);




        mOrderSummarySection = rootView.findViewById(R.id.order_summary_section);
        mSummaryLabelsView = (TextView) mOrderSummarySection.findViewById(R.id.summary_labels);
        mSummaryValuesView = (TextView) mOrderSummarySection.findViewById(R.id.summary_values);
        mOrderTotalView = (TextView) mOrderSummarySection.findViewById(R.id.summary_order_total);

        mGetPaymentTokenButton = (Button) rootView.findViewById(R.id.place_order_button);
        mGetPaymentTokenButton.setOnClickListener(v -> mActivity.getToken());
        mGetPaymentTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setAmount(totalPrice);
                mActivity.setCartId(cartId);
                mActivity.setProductId(productId);
                mActivity.getToken();
                //mActivity.proceedToPayment(mAdapter.getItem(mListView.getCheckedItemPosition()));
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");


        if (mMediumTypefaceSpan == null) {
            mMediumTypefaceSpan = new CustomTypefaceSpan(
                    FontUtils.getTypeface(getActivity(), "Roboto-Medium.ttf"));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint: " + isVisibleToUser);
        if (isVisibleToUser) {
            setPriceInfo(mActivity.getSelectedPrice());
            setEventInfo(mActivity.getSelectedEvent());
            //setPaymentInformation(mActivity.getCreditCard());
        }
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    private void setPriceInfo(Price price) {
        if (price != null) {
            mPriceNameView.setText(price.getName());

           mPriceDescriptionView.setText(String.format("%s",price.getDescription()));


//            SpannableString phone = new SpannableString(getString(R.string.text_phone, address.getPrice()));
//            phone.setSpan(mMediumTypefaceSpan, 0, phone.toString().indexOf(':') + 1, 0);
            mPriceView.setText("INR "+price.getPrice());
        }
    }
    private void setEventInfo(Event event) {
        if (event != null) {
            mEventNameView.setText(event.getName());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            try {
                Date d = formatter.parse(event.getDate().replaceAll("Z$", "+0000"));
                DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat time = new SimpleDateFormat("hh:mm:ss a");
                System.out.println("Date: " + date.format(d));
                System.out.println("Time: " + time.format(d));
                mEventTimeView.setText(date.format(d)+", "+time.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
    }
    private void setPaymentInformation(CreditCard card) {
        if (card != null) {
//            mCardTypeView.setText(String.format("%s **** %s", card.getType().getName(), card.getLastFourNumbers()));
//            mCardHolderView.setText(card.getCardHolder());
//            mCardExpDateView.setText(String.format("Expires %s/20%s", card.getExpMonth(), card.getExpYear()));
        }
    }




}
