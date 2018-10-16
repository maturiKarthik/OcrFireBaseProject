package com.example.maturi.cardscannew;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

public class Waze extends Fragment {


    //Mobile_App_Id : ca-app-pub-7848862584025779~7509408567

    private View view;
    private Context mContext;

    @BindView(R.id.addr_search)
    EditText addr_Serach;

    @BindView(R.id.waze)
    Button waze;

    //@BindView(R.id.adView)
  //  AdView adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.waze,container,false);
        mContext = view.getContext();
        ButterKnife.bind(this,view);

        waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchWaze(addr_Serach.getText().toString().replace(" ","%20"));
                Log.w("TAG","DATA::"+addr_Serach.getText().toString().replace(" ","%20"));

            }
        });

        /**
         * Initializing And Loading Ads

        MobileAds.initialize(mContext, "ca-app-pub-7848862584025779~7509408567");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
         */
        return view;
    }

    private void  launchWaze(String addr){
        try
        {
            // Launch Waze to look for Hawaii:
            String url = "https://waze.com/ul?q="+addr;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }
}
