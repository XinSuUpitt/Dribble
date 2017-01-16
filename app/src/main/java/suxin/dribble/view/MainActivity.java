package suxin.dribble.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.AppRater;
import suxin.dribble.view.followingusers.FollowingUsersFragment;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.view.bucket_list.BucketListFragment;
import suxin.dribble.view.shot_list.ShotListFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer)
    NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;
    public int timeType = 0;
    private MainActivity mainActivity;

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public int getTimeType() {
        return timeType;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,          /* DrawerLayout object */
                R.string.open_drawer,         /* "open drawer" description */
                R.string.close_drawer         /* "close drawer" description */
        );
        drawerLayout.setDrawerListener(drawerToggle);

        setupDrawer(drawerLayout);

        AppRater.app_launched(this);

        if (savedInstanceState == null) {
            ShotListFragment shotListFragment = ShotListFragment.newInstance(
                    ShotListFragment.LIST_TYPE_POPULAR);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, shotListFragment)
                    .commit();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawer(final DrawerLayout drawerLayout) {
        // dynamically set header, the header is not specified in main_activity.xml layout
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_logged_in);

        ((TextView) headerView.findViewById(R.id.nav_header_user_name)).setText(
                Dribbble.getCurrentUser().name);

        headerView.findViewById(R.id.nav_header_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dribbble.logout(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView userPicture = (ImageView) headerView.findViewById(R.id.nav_header_user_picture);
        ImageUtil.loadUserPicture(this, userPicture, Dribbble.getCurrentUser().avatar_url);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.drawer_item_favorite:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR);
                        setTitle(R.string.drawer_menu_favorite);
                        break;
                    case R.id.drawer_item_attachments:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_ATTACHMENTS);
                        setTitle(R.string.drawer_menu_shotswithattachments);
                        break;
                    case R.id.drawer_item_debuts:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_DEBUTS);
                        setTitle(R.string.drawer_menu_debuts);
                        break;
                    case R.id.drawer_item_teamshots:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_TEAMSHOTS);
                        setTitle(R.string.drawer_menu_teamshots);
                        break;
                    case R.id.drawer_item_playoffs:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_PLAYOFFS);
                        setTitle(R.string.drawer_menu_playoffs);
                        break;
                    case R.id.drawer_item_rebounds:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_REBOUNDS);
                        setTitle(R.string.drawer_menu_rebounds);
                        break;
                    case R.id.drawer_item_following:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_FOLLOWING);
                        setTitle(R.string.drawer_menu_following);
                        break;
                    case R.id.drawer_item_friends:
                        fragment = FollowingUsersFragment.newInstance(Dribbble.getCurrentUser().id);
                        setTitle(R.string.drawer_menu_friends);
                        break;
                    case R.id.drawer_item_likes:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_LIKED);
                        setTitle(R.string.title_likes);
                        break;
                    case R.id.drawer_item_animatedGIFs:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_ANIMATEDGIFS);
                        setTitle(R.string.drawer_menu_animatedGif);
                        break;
                    case R.id.drawer_item_buckets:
                        fragment = BucketListFragment.newInstance(null, false, null, false);
                        setTitle(R.string.title_buckets);
                        break;
                    case R.id.drawer_item_aboutme:
                        fragment = About_Me_main.newInstance(200);
                        setTitle(R.string.drawer_menu_aboutme);
                        break;

                }

                drawerLayout.closeDrawers();

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    return true;
                }

                return false;
            }
        });
    }
}
