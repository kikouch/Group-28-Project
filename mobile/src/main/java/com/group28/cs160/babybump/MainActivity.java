package com.group28.cs160.babybump;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity {

    public static final String EVENT_OBJECT="event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragmentManager = getSupportFragmentManager();

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useFixedMode();
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.calendar_icon:
                        replaceFragment(new CalendarFragment());
                        break;
                    case R.id.weight_icon:
                        replaceFragment(new WeightFragment());
                        break;
                    case R.id.home_icon:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.heartrate_icon:
                        replaceFragment(new HeartRateFragment());
                        break;
                    case R.id.nearby_icon:
                        replaceFragment(new NearbyLocationsFragment());
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
        mBottomBar.mapColorForTab(0, "#7B1FA2");
        mBottomBar.mapColorForTab(1, "#7B1FA2");
        mBottomBar.mapColorForTab(2, "#FF5252");
        mBottomBar.mapColorForTab(3, "#7B1FA2");
        mBottomBar.mapColorForTab(4, "#7B1FA2");

        mBottomBar.selectTabAtPosition(2, false);

        // Default to home fragment.
        //replaceFragment(new HomeFragment());

        // Check if this activity was launched from watch.
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("EVENT_OBJECT")) {
                String event = bundle.getString(EVENT_OBJECT);
                // Move to calendar.
                mBottomBar.selectTabAtPosition(0, false);
                replaceFragment(new DetailedEventFragment());
            }
        }

    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    private BottomBar mBottomBar;
    private FragmentManager mFragmentManager;
}
