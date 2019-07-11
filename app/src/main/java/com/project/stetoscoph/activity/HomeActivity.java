package com.project.stetoscoph.activity;

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

import com.project.stetoscoph.fragment.PairProductFragment;
import com.project.stetoscoph.R;
import com.project.stetoscoph.fragment.ReportFragment;
import com.project.stetoscoph.SessionSharedPreference;
import com.project.stetoscoph.fragment.GraphFragment;

public class
HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout exit;

    SessionSharedPreference session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialization widget and object
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigasi);
        exit = (LinearLayout) findViewById(R.id.container_exit);

        navigationView.setCheckedItem(R.id.menu_pairing);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionSharedPreference(getApplicationContext());

        if (session.checkLogin())
            finish();

        if (savedInstanceState == null) {
            Fragment currentFragment = new PairProductFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, currentFragment)
                    .commit();
        }

        // ketika pilihan log out di klik
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Log Out", Toast.LENGTH_SHORT).show();
                session.deleteUserLoginSession();
                finish();
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


    // ketika item menu navigasi di klik maka akan pindah ke halaman tersebut
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        String title = "";

        switch (menuItem.getItemId()) {
            // ketika menu pairing di klik akan pindah ke menu tersebut, dengan menampung nya pada variabel fragment
            case R.id.menu_pairing:
                fragment = new PairProductFragment();
                title = "Pairing Product";
                break;
            case R.id.menu_graph:
                fragment = new GraphFragment();
                title = "Graph";
                break;
            case R.id.menu_report:
                fragment = new ReportFragment();
                title = "Report";
                break;
        }

        // disini untuk mengatur fragment mana yang akan ditampilkan
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
