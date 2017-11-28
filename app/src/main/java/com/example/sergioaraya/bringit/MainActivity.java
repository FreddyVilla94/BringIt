package com.example.sergioaraya.bringit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Classes.User;
import com.example.sergioaraya.bringit.Dialogs.NewShoppingListDialog;
import com.example.sergioaraya.bringit.Fragments.ProductsFragment;
import com.example.sergioaraya.bringit.Fragments.SettingsFragment;
import com.example.sergioaraya.bringit.Fragments.ShoppingListsFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProductsFragment.OnFragmentInteractionListener, ShoppingListsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View header;
    private Toolbar toolbar;
    private LinearLayout contentMain;
    private Fragment fragment;

    private TextView accountUsername, accountEmail;
    private CircleImageView accountImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.main_activity_your_shopping_lists));
        toolbar.setSubtitle(getResources().getString(R.string.main_activity_greeting));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        accountImage = (CircleImageView) header.findViewById(R.id.account_image);
        accountUsername = (TextView) header.findViewById(R.id.account_username);
        accountEmail = (TextView) header.findViewById(R.id.account_email);

        loadAccountInfoNavigationDrawer();
        contentMain = (LinearLayout) findViewById(R.id.content_main);

        fragment = new ShoppingListsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.add_shopping_list:
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_CALENDAR},
                            constants.getMyPermissionsRequestWriteCalendar());
                }

                else {
                    singleton.setControlUpdateShoppingList(0);
                    NewShoppingListDialog newShoppingListDialog = new NewShoppingListDialog(MainActivity.this);
                    newShoppingListDialog.show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onFinish() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        singleton.setUser(new User());
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
            singleton.setUser(new User());

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Load user data to set profile on navigation drawer
     */
    public void loadAccountInfoNavigationDrawer(){

        accountUsername.setText(singleton.getUser().getName());
        accountEmail.setText(singleton.getUser().getEmail());

        if (! singleton.getUser().getImage().equals("") && ! singleton.getUser().getImage().equals("#")) {

            Glide.with(this.getApplicationContext()).load(singleton.getUser().getImage()).into(accountImage);
        }

        else {

            accountImage.setImageResource(R.drawable.account_image);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean FragmentTransaction = false;
        fragment = null;

        if (id == R.id.nav_shopping_lists) {
            fragment = new ShoppingListsFragment();
            FragmentTransaction = true;
        } else if (id == R.id.nav_products) {
            fragment = new ProductsFragment();
            FragmentTransaction = true;

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            FragmentTransaction = true;
        } else if (id == R.id.nav_logout) {
            this.onFinish();
        }

        if (FragmentTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
            item.setChecked(true);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
