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

    // Widget
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout exit;

    // variabel session
    SessionSharedPreference session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialization widget
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigasi);
        exit = (LinearLayout) findViewById(R.id.container_exit);

        // membuat navigasi yang terpilih pertama kali adalah pair product
        navigationView.setCheckedItem(R.id.menu_pairing);
        // menaruh method untuk menghandle saat navigasi di klik.
        navigationView.setNavigationItemSelectedListener(this);

        // pembuatan objek sesson agar dapat menggunakan method dan fungsi yang ada pada kelas SessionSharedPreference
        session = new SessionSharedPreference(getApplicationContext());

        // Menegek apakah user telah login atau belum. Jika nilainya true maka akan menutup halaman saat ini.
        if (session.checkLogin())
            finish();

        // mengatur agar halaman fragment yang pertama kali muncul adalah pair product
        if (savedInstanceState == null) {
            Fragment currentFragment = new PairProductFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, currentFragment)
                    .commit();
        }

        // Menghandle ketika tulisan log out di tekan, maka akan menjalankan proses ini
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muncul pop up text
                Toast.makeText(HomeActivity.this, "Log Out", Toast.LENGTH_SHORT).show();
                // Memanggil method berikut untuk menghapus sesi login di file shared preference
                session.deleteUserLoginSession();
                // Menutup halaman saat ini
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


    // Untuk menghandle saat salah satu menu di navigasi ditekan
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // ini untuk menyimpan objek fragment
        Fragment fragment = null;
        // ini untuk menyimpan nama halaman
        String title = "";

        switch (menuItem.getItemId()) {
            // ketika yang diklik cocok dengan case dibawah ini maka proses didalamnya akan berjalan
            case R.id.menu_pairing:
                // membuat objek fragment yang berisi PairProduct
                fragment = new PairProductFragment();
                // mengisi variabel title dengan teks berikut
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

        // Disini untuk mengatur halaman fragment yang akan ditampilkan, sesuai pilihan user
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame, fragment)
                    .commit();
        }

        // Disini untuk mengatur judul dari toolbar sesuai isi variable title
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
