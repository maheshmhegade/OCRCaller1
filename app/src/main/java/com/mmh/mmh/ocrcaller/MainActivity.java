package com.mmh.mmh.ocrcaller;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;


public class MainActivity extends ActionBarActivity {

    private Uri fileUri;
    private ImageView image;
    private TextView tessResults;

    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1003;
    public static MainActivity ActivityContext =null;
    public static TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orc_camera);

        ActivityContext = this;

        Button buttonCameraOn = (Button)findViewById(R.id.onCamera);
        tessResults = (TextView)findViewById(R.id.results);
        image = (ImageView)findViewById(R.id.image);

                buttonCameraOn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // create new Intentwith with Standard Intent action that can be
                // sent to have the camera application capture an video and return it.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // create a file to save the video
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                Log.d("saved to", fileUri.toString());
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // set the video image quality to high
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                // start the Video Capture Intent
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

            }
        });


    }

    private static Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static File getOutputMediaFile(int type){

        // Check that the SDCard is mounted

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator +"MyCameraVideo" );


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                output.setText("Failed to create directory MyCameraVideo.");

                Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();


                return null;
            }
        }

        // Create a media file name

        // For unique file name appending current timeStamp with file name

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_tmp" + ".png");

        } else {
            return null;
        }

        return mediaFile; //mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // After camera screen this code will excuted

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {

            String filePathDir = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator +"MyCameraVideo/";
            if (resultCode == RESULT_OK) {
                Bitmap bmp = BitmapFactory.decodeFile(filePathDir + "IMG_tmp.png");

                image.setImageBitmap(bmp);
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(filePathDir, "");
                baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
                baseApi.setImage(bmp);
                String outputText = baseApi.getUTF8Text();
                tessResults.setText(outputText);

            } else if (resultCode == RESULT_CANCELED) {


            } else {

                output.setText("Video capture failed.");

                // Video capture failed, advise user
                Toast.makeText(this, "Video capture failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
