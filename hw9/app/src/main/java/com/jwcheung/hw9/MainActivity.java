package com.jwcheung.hw9;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainPageAdapter mMainPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        LinearLayout tabSearchLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabSearch = (TextView) tabSearchLayout.findViewById(R.id.customTab);
        tabSearch.setText(R.string.search_tab_text);
        tabSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabSearch);

        LinearLayout tabFavoritesLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabFavorites = (TextView) tabFavoritesLayout.findViewById(R.id.customTab);
        tabFavorites.setText(R.string.favorites_tab_text);
        tabFavorites.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.heart_fill_white, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabFavorites);
    }

    private void setupViewPager(ViewPager viewPager) {
        mMainPageAdapter.addFragment(new Tab1FragmentSearch(), getString(R.string.search_tab_text));
        mMainPageAdapter.addFragment(new Tab2FragmentFavorites(), getString(R.string.favorites_tab_text));
        viewPager.setAdapter(mMainPageAdapter);
    }
}
