package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ViewPager2 viewPager2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottom_nagivation);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setupViewPager();

        Menu menu = navigationView.getMenu();
        MenuItem itemLogout = menu.findItem(R.id.nav_logout);
        itemLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                Intent intent = new Intent(DashboardActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        //xu ly khi click vao tab bottom
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) {
                    viewPager2.setCurrentItem(0);
                } else if (item.getItemId() == R.id.menu_expense) {
                    viewPager2.setCurrentItem(1);
                } else if (item.getItemId() == R.id.menu_budget) {
                    viewPager2.setCurrentItem(2);
                } else if (item.getItemId() == R.id.menu_setting) {
                    viewPager2.setCurrentItem(3);
                } else {
                    viewPager2.setCurrentItem(0);
                }
                return true;
            }
        });
    }
private void setupViewPager(){
    ViewPagerAdapter viewPager = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
    viewPager2.setAdapter(viewPager);
    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (position == 0){
                bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                } else if (position == 1) {
                bottomNavigationView.getMenu().findItem(R.id.menu_expense).setChecked(true);
            } else if (position == 2) {
                bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
            } else if (position == 3) {
                bottomNavigationView.getMenu().findItem(R.id.menu_setting).setChecked(true);
            } else {
                bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
            }

        }



        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    });
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home) {
            viewPager2.setCurrentItem(0);
        } else if (item.getItemId() == R.id.menu_expense) {
            viewPager2.setCurrentItem(1);
        } else if (item.getItemId() == R.id.menu_budget) {
            viewPager2.setCurrentItem(2);
        } else if (item.getItemId() == R.id.menu_setting) {
            viewPager2.setCurrentItem(3);
        } else {
            viewPager2.setCurrentItem(0);
        }
        //close Drawer menu
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
