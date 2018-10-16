package com.example.maturi.cardscannew;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ContainerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.frame)
    FrameLayout frame;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    @BindView(R.id.textView)
    TextView textView;


    private static final int SCREEN_ORIENTATION_LANDSCAPE = 1;
    private static final int SCREEN_ORIENTATION_POTRAIT = 0;
    // Permission  Array ..
    private String[] premission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION = 123;
    private static final String TAG ="ContainerACtivity";
    private static final int REQUEST_IMAGE_CAPTURE =124;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);
        ButterKnife.bind(this);

        checkPermission();





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

        }

        toolbar.setTitle(R.string.nav_title);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);

            }
        });



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.scan:
                        textView.setVisibility(View.INVISIBLE);
                        setScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE);
                        drawer.closeDrawer(Gravity.START);


                       fragment = new CameraActivity();

                       break;



                    case R.id.gallery:
                        textView.setVisibility(View.INVISIBLE);
                      //  setScreenOrientation(SCREEN_ORIENTATION_POTRAIT);
                        toolbar.setTitle("Gallery Scan");
                        drawer.closeDrawer(Gravity.START);

                        fragment = new GalleryImageScanActivity();
                        break;

                        case R.id.Google:
                            textView.setVisibility(View.INVISIBLE);
                          //  setScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE);
                            drawer.closeDrawer(Gravity.START);
                            fragment = new GooglVissionApi();
                            break;

                    case R.id.waze:
                        textView.setVisibility(View.INVISIBLE);
                        //  setScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE);
                        drawer.closeDrawer(Gravity.START);
                        fragment = new Waze();
                        break;
                    case R.id.logout:
                         finish();

                        break;


                }

                if (fragment != null){

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame,fragment);
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return false;
            }
        });


    }




    /**
     * Setting Screen Oriebntation With Lock
     */
    private void setScreenOrientation(int i){
        if (i ==  SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    // Check Permission.
    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
                this.requestPermissions(premission,REQUEST_PERMISSION);
            }


        }else{
            Log.w(TAG,"All Permissions are Granget");
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.w("TAG","PERMISSION GRANTED");
                }else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.w("TAG","PERMISSION NOT GRANTED");
                }
                return;
        }
    }

    /**
     * On Pressing The Back Button
     */
    @Override
    public void onBackPressed() {
        drawer.openDrawer(Gravity.START);
       //finish();
        super.onBackPressed();
    }
}
