package com.example.maturi.cardscannew;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class CameraActivity extends Fragment {

    private static final String TAG = "CameraActivity";
    private Uri imageUri;


    @BindView(R.id.camera_preview)
    FrameLayout camera_preview;


    @BindView(R.id.camera_nav)
    BottomNavigationView navigationView;

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

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.progess)
    LinearLayout progess;


    android.hardware.Camera camera;
    private View view;
    private Context mContext;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */



    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.camera, container, false);
        ButterKnife.bind(this, view);
        mContext = view.getContext();




        /**
         * Check Device Has Camera
         */

        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
          //  Toast.makeText(mContext, "DEVICE CAMERA FOUND", Toast.LENGTH_LONG).show();
            /**
             * Getting The Camera Instance ..
             */
            camera = android.hardware.Camera.open();
            // Capabilities Of cam

            camera_preview.addView(new CameraPreview(mContext, camera));


        } else {
            Toast.makeText(mContext, "DEVICE CAMERA NOT  FOUND", Toast.LENGTH_LONG).show();

        }

        navigationView.setSelected(true);
        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.takePic:

                        //  Toast.makeText(view.getContext(),"Picture Stored at Path"+imageUri,Toast.LENGTH_LONG).show();
                        takePicture();
                        camera_preview.setVisibility(View.GONE);
                        contact_layout.setVisibility(View.GONE);
                        //startScan();
                        break;

                }
            }
        });

        return view;
    }


    /**
     * Click To Capture Image
     **/
    //  @OnClick(R.id.cam)
    public void takePicture() {

        camera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                /**
                 * Capturing & Saving Picture
                 */

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //  saveImageToExternalStorage(bitmap);
                new ScanText(mContext).execute(bitmap);

            }
        });

    }


    @OnClick(R.id.fab)
    public void onClickAddToContact(View view) {
        String name = First_Name.getText().toString();
        String phone_home = Tel_Home.getText().toString();
        String phone_work = Tel_Work.getText().toString();
        String addr_email = email.getText().toString();
        String web = website.getText().toString();
        AddContacts addContacts = new AddContacts(name, phone_home, phone_work, addr_email, web, mContext);
    }


    /**
     * Starting  An Async Task
     */

    public class ScanText extends AsyncTask<Bitmap, String, Uri> {

        private Context mContext;


        public ScanText(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            Log.w("TAG", "Entered In TO AsyncTask");
            //progressBar.setVisibility(View.VISIBLE);
           // progess.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Uri s) {
            Toast.makeText(mContext, "FiNISHED ::" + s, Toast.LENGTH_LONG).show();
            contact_layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            Log.w("TAG", "Entered In TO PostAsyncTask" + s);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (int i = 0; i < values.length; i++) {
                Toast.makeText(mContext, values[i], Toast.LENGTH_LONG).show();
                Log.w("TAG", "TEST" + values[i]);
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected Uri doInBackground(Bitmap... bitmaps) {
            Log.w("TAG", "Entered In TO Back Ground AsyncTask");
            Uri result = null;
            int length = bitmaps.length;
            for (int i = 0; i < length; i++) {
                publishProgress("Storing File .. " + result);
                result = saveImageToExternalStorage(bitmaps[i]);
                publishProgress("Scanning .. ");
                startScan(result);

            }

            return result;
        }

        /**
         * Saving An Image On To Phone ..
         * Same Logic for Every Storing on phone ..
         */
        private Uri saveImageToExternalStorage(Bitmap finalBitmap) {


            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            Log.w("TAG", "" + root);
            File myDir = new File(root + "/" + "cardscan");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(file);
            //  startScan(uri);
            Log.w("TAG", "URI FROM FILE" + Uri.fromFile(file));

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        Uri takr;

                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                            //image_Addr = uri;
                            takr = uri;
                            // data = scanText(mContext,image_Addr);
                        }
                        //image_Addr = takr;
                    });
            //

            return uri;
        }


        //scan
        public void startScan(Uri imageUri) {


            FirebaseVisionImage image;
            FirebaseVisionTextRecognizer textRecognizer;
            Log.w("TAG", "ENTERD INTO SCAN");
            try {
                // image = FirebaseVisionImage.fromBitmap(bitmap);
                image = FirebaseVisionImage.fromFilePath(mContext, imageUri);
                textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        /**
                         * TEXT EXTRACTION
                         */
                        Log.w("TAG", "ENTERD INTO SUCESS");


                        String resultText = firebaseVisionText.getText().replace(" ", "");

                        String phone1 = RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_phone);
                        //scanOcrText.setText(resultText);
                        Tel_Home.setText(phone1);
                        Tel_Work.setText(RegExStringExtractor.extractString(resultText.replace(phone1, "!"), RegExStringExtractor.pattern_phone));
                        String email_ocr = RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_email_address);
                        email.setText(email_ocr);
                        website.setText(RegExStringExtractor.extractString(resultText, RegExStringExtractor.pattern_website));
                        First_Name.setText(email_ocr.substring(0,email_ocr.indexOf("@")));
                        /**
                         * UI Realted
                         */
                        //  progressBar.setVisibility(View.GONE);
                        //First_Name.setText(resultText);

                        Log.w("TAG", "ENTERD INTO SUCESS=====" + resultText);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "FAILED TO SCAN");

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


}
