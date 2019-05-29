package com.project.stetoscoph;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialization
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigasi);
        exit = (LinearLayout) findViewById(R.id.container_exit);

        navigationView.setCheckedItem(R.id.menu_pairing);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            Fragment currentFragment = new PairProductFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, currentFragment)
                    .commit();
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Exit", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawerLayout.removeDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        String title = "";

        switch (menuItem.getItemId()) {
            case R.id.menu_pairing:
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                fragment = new PairProductFragment();
                title = "Pairing Product";
                break;
            case R.id.menu_graph:
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                title = "Graph";
                break;
            case R.id.menu_report:
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                title = "Report";
                break;
            case R.id.menu_lock:
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                title = "Lock App";
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, fragment)
                    .commit();
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
