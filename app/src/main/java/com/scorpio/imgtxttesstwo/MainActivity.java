package com.scorpio.imgtxttesstwo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String imgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void takePicture()
    {
        Intent iPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(iPicture.resolveActivity(getPackageManager()) != null ){
            startActivityForResult(iPicture,1);
        }
    }

    private File createFile()
    {
        File imgFile = null;
        String fileStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
        File imageFile;
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            imageFile = createFile();
            if(imageFile!=null)
            {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),,imageFile);
            }

        }
    }
}
