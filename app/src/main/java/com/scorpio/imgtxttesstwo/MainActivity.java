package com.scorpio.imgtxttesstwo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String imgPath;
    Bitmap imgBitmap;
    Uri imgUri;
    InputStream trainDataInputStream;
    OutputStream trainDataOutputStream;
    AssetManager assetManager;
    String trainedDataPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //System.out.println(getFilesDir());
        takePicture();
    }

    private void takePicture()
    {
        File photoFile = null;
        Intent iPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(iPicture.resolveActivity(getPackageManager()) != null ){

            try{
                photoFile = createImageFile();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            //if photo file is created
            if(photoFile!=null)
            {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),"com.scorpio.fileprovider",photoFile);
                System.out.println(imgPath);
                iPicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(iPicture,1);

            }
        }
    }

    private File createImageFile()
    {
        File imgFile = null;
        String fileStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStorageDirectory();
        try {
            imgFile = File.createTempFile(fileStamp,".jpeg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
         imgPath = imgFile.getAbsolutePath();
        return imgFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            galleryAddPic();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imgPath);
        Uri contentUri = Uri.fromFile(f);
        imgUri = contentUri;
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    public void ocrImage()
    {
        try {
            imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExifInterface exif=null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = imgBitmap.getWidth();
            int h = imgBitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, w, h, mtx, false);
        }
        imgBitmap = imgBitmap.copy(Bitmap.Config.ARGB_8888, true);
        TessBaseAPI baseApi = new TessBaseAPI();

    }

    class DataRunnable implements Runnable{
        public void run()
        {
            assetManager = getAssets();
            trainedDataPath = getFilesDir() + "/tesseract/eng.traineddata";
            try {
                trainDataInputStream = assetManager.open("/tesseract/eng.traineddata");
                trainDataOutputStream = new FileOutputStream(trainedDataPath);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = trainDataInputStream.read(buffer)) != -1) {
                    trainDataOutputStream.write(buffer, 0, read);
                }
                trainDataOutputStream.flush();
                trainDataOutputStream.close();
                trainDataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
