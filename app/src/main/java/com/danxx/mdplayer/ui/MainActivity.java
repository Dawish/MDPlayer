package com.danxx.mdplayer.ui;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment listFragment ,settingsFragment  ,meizhiFragment, onlineVideoFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Fill in layout id
     *
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * Initialize the view in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Resources resource=(Resources)getBaseContext().getResources();
        ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);

        listFragment = FileListFragment.newInstance(null, null);
        settingsFragment = SettingsFragment.newInstance(null ,null);
//        aboutFragment = AboutFragment.newInstance(null ,null);
        meizhiFragment = MeizhiClassifyFragment.newInstance(null, null);

        onlineVideoFragment = OnlineVideoFragment.newInstance(MainActivity.this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentLayout , listFragment ,listFragment.getClass().getSimpleName());
        transaction.add(R.id.contentLayout , settingsFragment ,settingsFragment.getClass().getSimpleName());
        transaction.hide(settingsFragment);
        transaction.add(R.id.contentLayout , meizhiFragment ,meizhiFragment.getClass().getSimpleName());
        transaction.hide(meizhiFragment);

        transaction.add(R.id.contentLayout, onlineVideoFragment, onlineVideoFragment.getClass().getSimpleName());
        transaction.hide(onlineVideoFragment);

        getSupportActionBar().setTitle("目录");
        transaction.commit();

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    /**
     * Initialize the Activity data
     */
    @Override
    protected void initData() {

    }

    /**
     * Initialize the toolbar in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    /**
     * Initialize the View of the listener
     */
    @Override
    protected void initListeners() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (onlineVideoFragment != null) {
            ((OnlineVideoFragment) onlineVideoFragment).onBackPressed();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(listFragment).hide(settingsFragment).hide(onlineVideoFragment).hide(meizhiFragment);
        if (id == R.id.nav_camera) {
            getSupportActionBar().setTitle("目录");
            transaction.show(listFragment).commit();
        } else if (id == R.id.nav_gallery) {
            getSupportActionBar().setTitle("设置");
            transaction.show(settingsFragment).commit();
        } else if (id == R.id.nav_slideshow) {
            getSupportActionBar().setTitle("在线");
            transaction.show(onlineVideoFragment).commit();
        } else if (id == R.id.nav_manage) {
            getSupportActionBar().setTitle("妹纸");
            transaction.show(meizhiFragment).commit();
        }
        /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        if (onlineVideoFragment != null){
            ((OnlineVideoFragment)onlineVideoFragment).pauseToPlay();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event);
    }
}
