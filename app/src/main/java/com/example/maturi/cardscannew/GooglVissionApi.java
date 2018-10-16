package com.example.maturi.cardscannew;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.HAPTIC_FEEDBACK_ENABLED;

public class GooglVissionApi extends Fragment {

    private static final String   TAG ="GoogleVissionAPi";

   private View view;
   private Context mCOntext;

   CameraSource mCameraSource;

   @BindView(R.id.surfaceView)
   SurfaceView mCameraView;

   //@BindView(R.id.text_view)
   //TextView mTextView;




    @BindView(R.id.bottomNavigation)
    BottomNavigationView googleBottonScan;

    @BindView(R.id.whole_View)
    LinearLayout whole_View;

    private String data;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.googlevison_layout,container,false);
       mCOntext = view.getContext();
        ButterKnife.bind(this,view);

        mCameraView.setZOrderOnTop(false);

        googleBottonScan.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.scan:
                        scanText(data);
                        break;
                }
                return true;
            }
        });

        startCameraSource();
        return view;
    }





    private void scanText(String resultText){



       final  String phone1 = RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_phone);
       final String Tel_Work = RegExStringExtractor.extractString(resultText.replace(phone1, "!"), RegExStringExtractor.pattern_phone);
       final String email_ocr = RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_email_address);
       final String webSite = RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_website);
       final String First_Name;
        if (email_ocr.equals("NO SCAN RESULT")){
             First_Name = "NO SCAN RESULT";
        }else{
             First_Name = email_ocr.substring(0,email_ocr.indexOf("@"));
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(mCOntext,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        final View view = getLayoutInflater().inflate(R.layout.scanned_layout,null);
        alert.setView(view);
        TextInputEditText name  = (TextInputEditText) view.findViewById(R.id.name);
        name.setText(First_Name);
        TextInputEditText tel_home  = (TextInputEditText) view.findViewById(R.id.tel_home);
        tel_home.setText(phone1);
        TextInputEditText tel_work  = (TextInputEditText) view.findViewById(R.id.tel_work);
        tel_work.setText(Tel_Work);
        TextInputEditText email  = (TextInputEditText) view.findViewById(R.id.email);
        email.setText(email_ocr);
        TextInputEditText website  = (TextInputEditText) view.findViewById(R.id.website);
        website.setText(webSite);
        Button fab =(Button) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        alert.setPositiveButton("ADD CONTACT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddContacts addContacts = new AddContacts(First_Name, phone1, Tel_Work, email_ocr, webSite, mCOntext);
                Snackbar mySnackbar = Snackbar.make(view,"Added To Contacts Sucessfully",Snackbar.LENGTH_LONG);
                mySnackbar.show();
            }
        });
        alert.setNegativeButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mCOntext, "Starting the Camera", Toast.LENGTH_SHORT).show();
                        //Create the TextRecognizer

                        startCameraSource();
                    }
                }

        );
        alert.show();


        //scaned_data.get(0).setText(phone1);
        //scanOcrText.setText(resultText);
        Log.w("TAG-DATA","Phone ::+"+phone1);
        Log.w("TAG-DATA","Tel_Work ::+"+Tel_Work);
        Log.w("TAG-DATA","email_ocr ::+"+email_ocr);
        Log.w("TAG-DATA","webSite ::+"+webSite);
        Log.w("TAG-DATA","First_Name ::+"+First_Name);

    }

    /**
     * GooglVision API
     * TEXT OCR Code function
     */
    private void startCameraSource() {

        //Create a text Recognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(mCOntext.getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(mCOntext.getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(mCOntext.getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                            return;
                        }
                       mCameraSource.start(mCameraView.getHolder());


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */


                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    StringBuilder stringBuilder = new StringBuilder();
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){
                        for(int i=0;i<items.size();i++){
                            TextBlock item = items.valueAt(i);
                            Log.w("TAG","DATA+"+item.getValue());

                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");

                            data = stringBuilder.toString().replace(" ","");

                            Log.w("TAG","FInal Data ::::"+data);

                        }

                    }
                }
            });
        }
    }
}
