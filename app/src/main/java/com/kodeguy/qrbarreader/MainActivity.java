package com.kodeguy.qrbarreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kodeguy.qrbarreader.GeneralFragments.AboutFragment;
import com.kodeguy.qrbarreader.GeneralFragments.HelpFragment;
import com.kodeguy.qrbarreader.GeneralFragments.HistoryFragment;
import com.kodeguy.qrbarreader.GeneralFragments.MyCaptureFragment;
import com.kodeguy.qrbarreader.GeneralFragments.SettingsFragment;

import java.io.File;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // delay to launch nav drawer item, to allow close animation to play
    static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;




    // Helper
    private Handler mHandler;

    private LinkedList<Integer> backStack = new LinkedList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mHandler = new Handler();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        if(prefs.getBoolean("firstVisit", true)) {
            WelcomeDialog welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), "WelcomeDialog");
        }
        else if(savedInstanceState == null) {
                selectItem(0, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_clear) {
            File sdcard = getFilesDir();
            final File file = new File(sdcard,"history.txt");

            if(!file.exists()) Toast.makeText(this, getResources().getString(R.string.cannot_clear), Toast.LENGTH_SHORT).show();
            else {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
                deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener () {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }

                });
                deleteDialog.setMessage(R.string.delete_history);
                deleteDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        selectItem(1,false);
                    }
                });

                deleteDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static class WelcomeDialog extends DialogFragment {

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater i = getActivity().getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(i.inflate(R.layout.welcome_dialog, null));
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(getActivity().getString(R.string.welcome));
            builder.setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)getActivity()).goToNavigationItem(R.id.nav_scan);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstVisit", false);
                    editor.commit();
                }
            });
            builder.setNegativeButton(getActivity().getString(R.string.viewhelp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)getActivity()).goToNavigationItem(R.id.nav_help);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstVisit", false);
                    editor.commit();
                }
            });

            return builder.create();
        }
    }

    public void switchToFragment(Fragment frag, boolean back){
        Fragment temp = getFragmentManager().findFragmentById(R.id.container);
        if(!back)
            if(temp instanceof MyCaptureFragment)
                backStack.push(0);
            else if(temp instanceof HistoryFragment)
                backStack.push(1);
            else if(temp instanceof SettingsFragment)
                backStack.push(2);
            else if(temp instanceof AboutFragment)
                backStack.push(3);
            else if(temp instanceof HelpFragment)
                backStack.push(4);


        getFragmentManager().beginTransaction().replace(R.id.container, frag).addToBackStack("").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (backStack.size() < 1)
            finish();
        else {
            int back = backStack.pop();
            selectItem(back, true);
        }
    }

    public void goBack(){
        getFragmentManager().beginTransaction().replace(R.id.container, new HistoryFragment()).addToBackStack("").commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(getNavigationDrawerID());
        selectNavigationItem(R.id.nav_scan);

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for(int i = 0 ; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    protected int getNavigationDrawerID() {
        Fragment temp = getFragmentManager().findFragmentById(R.id.container);

        if(temp instanceof MyCaptureFragment)
            return R.id.nav_scan;
        else if(temp instanceof HistoryFragment)
            return R.id.nav_history;
        else if(temp instanceof SettingsFragment)
            return R.id.nav_settings;
        else if(temp instanceof AboutFragment)
            return R.id.nav_about;

        else
            return R.id.nav_scan;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        return goToNavigationItem(itemId);
    }

    protected boolean goToNavigationItem(final int itemId) {

        // delay transition so the drawer can close
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        mDrawerLayout.closeDrawer(GravityCompat.START);

        selectNavigationItem(itemId);

        return true;
    }

    private void callDrawerItem(final int itemId) {
        selectItem(0, false);

        switch(itemId) {
            case R.id.nav_scan:
                selectItem(0, false);
                break;
            case R.id.nav_history:
                selectItem(1, false);
                break;
            case R.id.nav_about:
                selectItem(3, false);
                break;
            case R.id.nav_help:
                selectItem(4, false);
                break;
            case R.id.nav_settings:
                selectItem(2, false);
                break;
            case R.id.nav_more_apps:
                selectItem(5, false);
                break;
            case R.id.nav_share:
                selectItem(6, false);
                break;
            default: selectItem(0, false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void selectItem(int position, boolean back) {
        // update the main content by replacing fragments


        if(position == 0) {
            switchToFragment(new MyCaptureFragment(), back);
            this.setTitle(R.string.app_name);
        }
        else if(position == 1) {
            switchToFragment(new HistoryFragment(), back);
            this.setTitle(R.string.history);
        }
        else if(position == 2) {
            switchToFragment(new SettingsFragment(), back);
            this.setTitle(R.string.title_activity_settings);
        }
        else if(position == 3) {
            switchToFragment(new AboutFragment(), back);
            this.setTitle(R.string.action_about);
        }
        else if(position == 4) {
            switchToFragment(new HelpFragment(), false);
            this.setTitle(R.string.action_help);
        }
        else if(position == 5) {

            this.setTitle(R.string.action_more_apps);
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Kode+Guy "));
            startActivity(i);
        }
        else if(position == 6) {
            this.setTitle(R.string.action_share);
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String sAux = "\nBest QR/Bar code reader\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.kodeguy.qrbarreader \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        }
    }


}
