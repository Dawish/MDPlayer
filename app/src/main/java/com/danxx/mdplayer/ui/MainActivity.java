package com.danxx.mdplayer.ui;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.danxx.mdplayer.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment listFragment ,settingsFragment ,aboutFragment;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set Explode enter transition animation for current activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode().setDuration(800));
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        aboutFragment = AboutFragment.newInstance(null ,null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentLayout , listFragment ,listFragment.getClass().getSimpleName());
        transaction.add(R.id.contentLayout , settingsFragment ,settingsFragment.getClass().getSimpleName());
        transaction.hide(settingsFragment);
        transaction.add(R.id.contentLayout, aboutFragment, aboutFragment.getClass().getSimpleName());
        transaction.hide(aboutFragment);
        getSupportActionBar().setTitle("目录");
        transaction.commit();

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        transaction.hide(listFragment).hide(settingsFragment).hide(aboutFragment);
        if (id == R.id.nav_camera) {
            getSupportActionBar().setTitle("目录");
            transaction.show(listFragment).commit();
        } else if (id == R.id.nav_gallery) {
            getSupportActionBar().setTitle("设置");
            transaction.show(settingsFragment).commit();
        } else if (id == R.id.nav_slideshow) {
            getSupportActionBar().setTitle("关于");
            transaction.show(aboutFragment).commit();
        } else if (id == R.id.nav_manage) {
            /**************************************************/
            getSupportActionBar().setTitle("推荐");
            transaction.show(listFragment).commit();
        }
        /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 切换显示的fragment
     * @param fromFragment
     * @param toFragment
     */
    public void switchContent(Fragment fromFragment , Fragment toFragment ) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!toFragment.isAdded()) {    // 先判断是否被add过
            transaction.hide(fromFragment).add(R.id.contentLayout, toFragment ,toFragment.getClass().getSimpleName()).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(fromFragment).show(toFragment).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

}
