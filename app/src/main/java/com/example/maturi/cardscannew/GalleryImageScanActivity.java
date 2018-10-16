package com.example.maturi.cardscannew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.CAMERA_SERVICE;

public class GalleryImageScanActivity extends Fragment {

    private View view;
    private Context mContext;


    private static final int REQUEST_IMAGE =123;
    private static final String TAG = "GalleryImageScanActi";
    private Uri imageUri;

    @BindView(R.id.imageView)
    ImageView imageView;



    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    Button fab_save;

    @BindView(R.id.contact_layout)
    ScrollView contact_layout;

    @BindView(R.id.name)
    EditText First_Name;

    @BindView(R.id.tel_home)
    EditText Tel_Home;

    @BindView(R.id.tel_work)
    EditText Tel_Work;


    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.website)
    EditText website;



    private ExifInterface exif;


    String resultText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_img_scan,container,false);
        ButterKnife.bind(this,view);
        mContext = view.getContext();

        openGallery();

        return  view;
    }



    @OnClick(R.id.fab)
    public void onClickAddToContact(View view){
        String name = First_Name.getText().toString();
        String phone_home =  Tel_Home.getText().toString();
        String phone_work = Tel_Work.getText().toString();
        String addr_email = email.getText().toString();
        String web = website.getText().toString();
        AddContacts addContacts = new AddContacts(name,phone_home,phone_work,addr_email,web,mContext);
    }


    /**
     * Intent To Open Gallery
     */
    private void openGallery(){
        Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
        openGallery.setType("image/*");
        startActivityForResult(openGallery,REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG,"ON ACTIVITY RESULT");
        if (requestCode == REQUEST_IMAGE){

            try {


                imageUri = data.getData();
                Log.w(TAG,"imageUri"+imageUri);


                final InputStream imageStream = view.getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

              /**
                 * Scaling Or Reducing The Size .
                 */
                int nh = (int) ( selectedImage.getHeight() * (512.0 / selectedImage.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 512,nh, true);

                //imageView.setImageBitmap(newBitmap);

                imageView.setImageBitmap(scaled);
                //scaled.recycle();
                if (imageUri != null){
                   // startScan();
                    startScan(imageUri);
                }else {
                    Log.w(TAG,"DISPLAY");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "You haven't selected Anything", Toast.LENGTH_LONG).show();
                getActivity().finish();
                launchFirstActivity();
            }


        }
    }

    /**
     * Method To Know  The Bitmap Rotation ..
     */


    /**
     * Finish Block Of Test
     */
    public void launchFirstActivity(){
        Intent intent = new Intent(mContext,ContainerActivity.class);
        startActivity(intent);
    }

    /**
     * Scan Text ..
     */
    public void startScan(Uri imageUri){


        FirebaseVisionImage image;
        FirebaseVisionTextRecognizer textRecognizer;
        Log.w("TAG","ENTERD INTO SCAN");
        try {
            //image = FirebaseVisionImage.fromBitmap(bitmap);
            image = FirebaseVisionImage.fromFilePath(mContext, imageUri);
            textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    /**
                     * TEXT EXTRACTION
                     */
                    Log.w(TAG,"ENTERD INTO SUCESS");



                     resultText = firebaseVisionText.getText().replace(" ","");
                    Log.w(TAG,"ENTERD INTO SUCESS" + resultText);
                     String phone1 = RegExStringExtractor.extractString(resultText,RegExStringExtractor.pattern_phone);
                    //scanOcrText.setText(resultText);
                    Tel_Home.setText(phone1);
                    Tel_Work.setText(RegExStringExtractor.extractString(resultText.replace(phone1,"!"),RegExStringExtractor.pattern_phone));
                   String email_Ocr = RegExStringExtractor.extractString(resultText,RegExStringExtractor.pattern_email_address);
                   Log.w("TAG","TEST ++"+ email_Ocr);
                    email.setText(email_Ocr);
                    website.setText(RegExStringExtractor.extractString(resultText,RegExStringExtractor.pattern_website));

                    if (email_Ocr.equals("NO SCAN RESULT")){
                        First_Name.setText("NO SCAN RESULT");
                    }else {
                        First_Name.setText(email_Ocr.substring(0, email_Ocr.indexOf("@")));
                    }

                    /**
                     * UI RELATED ACTIVITY ..
                     */
                    progressBar.setVisibility(View.GONE);
                   imageView.setVisibility(View.VISIBLE);
                   contact_layout.setVisibility(View.VISIBLE);

                    Log.w(TAG,"ENTERD INTO SUCESS=====" + resultText);

                    /**
                     * EOL
                     */

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG","FAILED TO SCAN");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
