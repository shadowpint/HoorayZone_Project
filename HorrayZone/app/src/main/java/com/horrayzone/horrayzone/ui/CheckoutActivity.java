package com.horrayzone.horrayzone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.CreditCard;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.ui.checkout.DateCheckoutFragment;
import com.horrayzone.horrayzone.ui.checkout.ReviewCheckoutFragment;
import com.horrayzone.horrayzone.ui.checkout.TicketCheckoutFragment;
import com.horrayzone.horrayzone.ui.widget.Stepper;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends BaseActivity {
    private static final String TAG = "CheckoutActivity";

    private static final String STATE_SELECTED_ADDRESS = "state_selected_address";
    private static final String STATE_SELECTED_AMOUNT = "state_selected_amount";
    private static final String STATE_CREDIT_CARD = "state_credit_card";
    private static final String STATE_FRAGMENT_SHIPPING = "state_checkout_shipping";
    private static final String STATE_FRAGMENT_DATE = "state_checkout_date";
    private static final String STATE_FRAGMENT_REVIEW = "state_checkout_review";
    private static final String STATE_SELECTED_PRICE = "state_selected_price";
    private static final String STATE_SELECTED_EVENT = "state_selected_event";

    private ViewPager mViewPager;
    private CheckoutPagerAdapter mStepperAdapter;

    private TicketCheckoutFragment mShippingFragment;
    private DateCheckoutFragment mDateFragment;
    private ReviewCheckoutFragment mReviewFragment;

    private Price mSelectedPrice;
    private String mAmount;
    private CreditCard mCreditCard;
    private String mCartId;
    private String mProductId;
    private Event mSelectedEvent;
    private String title, content, imageUrl;
    private CalendarDay mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set this activity's window as "Secure".
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        mSelectedEvent = intent.getExtras().getParcelable("event");
        title = mSelectedEvent.getName();
        content = mSelectedEvent.getDescription();
        imageUrl = mSelectedEvent.getLeadImageUrl();
        Log.e("event_checkout", title);
        if (savedInstanceState != null) {
            mShippingFragment = (TicketCheckoutFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, STATE_FRAGMENT_SHIPPING);

            mDateFragment = (DateCheckoutFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, STATE_FRAGMENT_DATE);

            mReviewFragment = (ReviewCheckoutFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, STATE_FRAGMENT_REVIEW);
        } else {
            mShippingFragment = TicketCheckoutFragment.newInstance();
            mDateFragment = DateCheckoutFragment.newInstance();
            mReviewFragment = ReviewCheckoutFragment.newInstance();
        }

        /*stepper.addStep(new Step.Builder(this).setTitle(getString(R.string.step_shipping)).build());
        stepper.addStep(new Step.Builder(this).setTitle(getString(R.string.step_payment)).build());
        stepper.addStep(new Step.Builder(this).setTitle(getString(R.string.step_review)).build());*/

        mStepperAdapter = new CheckoutPagerAdapter(getSupportFragmentManager());
        mStepperAdapter.addFragment(mShippingFragment);
        mStepperAdapter.addFragment(mDateFragment);
        mStepperAdapter.addFragment(mReviewFragment);

        mViewPager = (ViewPager) findViewById(R.id.steps_pager);
        mViewPager.setAdapter(mStepperAdapter);
        mViewPager.setOffscreenPageLimit(2);

        Stepper stepper = (Stepper) findViewById(R.id.stepper);
        stepper.setViewPager(mViewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
        outState.putParcelable(STATE_SELECTED_EVENT, mSelectedEvent);
        outState.putParcelable(STATE_SELECTED_PRICE, mSelectedPrice);
        outState.putSerializable(STATE_SELECTED_AMOUNT, mAmount);
        outState.putParcelable(STATE_CREDIT_CARD, mCreditCard);

        getSupportFragmentManager().putFragment(outState, STATE_FRAGMENT_SHIPPING, mShippingFragment);
        getSupportFragmentManager().putFragment(outState, STATE_FRAGMENT_DATE, mDateFragment);
        getSupportFragmentManager().putFragment(outState, STATE_FRAGMENT_REVIEW, mReviewFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedEvent = savedInstanceState.getParcelable(STATE_SELECTED_EVENT);
        mSelectedPrice = savedInstanceState.getParcelable(STATE_SELECTED_PRICE);
        mCreditCard = savedInstanceState.getParcelable(STATE_CREDIT_CARD);

        if (mSelectedPrice != null) {
            Log.d(TAG, "onRestoreInstanceState:\n" + mSelectedPrice.toString());
        }
        if (mCreditCard != null) {
            Log.d(TAG, "onRestoreInstanceState:\n" + mCreditCard.toString());
        }
    }

    public Price getSelectedPrice() {
        return mSelectedPrice;
    }

    public Event getSelectedEvent() {
        return mSelectedEvent;
    }

    public CalendarDay getSelectedDate() {
        return mSelectedDate;
    }
    public String getAmount() {
       return mAmount;
    }
    public CreditCard getCreditCard() {
        return mCreditCard;
    }

    public void prevStep() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    public void nextStep() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void proceedToDate(Price price) {
        Log.d(TAG, "proceedToDate: " + price.toString());

        mSelectedPrice = price;
        nextStep();
    }

    public void proceedToReview(CalendarDay date) {
        Log.d(TAG, "proceedToReview: " + date.toString());

        mSelectedDate = date;
        nextStep();

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() > 0) {
            prevStep();
            return;
        }

        super.onBackPressed();
    }

    public void getToken() {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra(OrderConfirmationActivity.EXTRA_PRICE, mSelectedPrice);
        intent.putExtra(OrderConfirmationActivity.EXTRA_AMOUNT, mSelectedPrice.getPrice());
        intent.putExtra(OrderConfirmationActivity.EXTRA_EVENT, mSelectedEvent);
        intent.putExtra(OrderConfirmationActivity.EXTRA_CREDIT_CARD, mCreditCard);
        intent.putExtra(OrderConfirmationActivity.EXTRA_PRODUCT_ID, mProductId);
        startActivity(intent);
        finish();
    }

    public void setAmount(float amount) {
        mAmount = String.valueOf(amount);
    }
    public void setCartId(String cartId) {
        mCartId = String.valueOf(cartId);
    }
    public void setProductId(String productId) {
        mProductId = String.valueOf(productId);
    }
    private class CheckoutPagerAdapter extends Stepper.StepperPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        private int[] mStepTitleStringRes = {
                R.string.step_select_ticket,
                R.string.step_select_date,
                R.string.step_select_payment
        };

        public CheckoutPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getClass().getSimpleName();
        }

        @Override
        public CharSequence getStepTitle(int position) {
            return getString(mStepTitleStringRes[position]);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }
    }
}
