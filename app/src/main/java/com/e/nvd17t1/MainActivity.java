package com.e.nvd17t1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    ImageView imgInput;
    TessBaseAPI mTess;
    Button btn,btnCamera;
    TextView tvResult;
    static final int REQUEST_IMAGE_CAPTURE=1;

    private void takePhoto(){
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePhotoIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extra = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extra.get("data");

            int w = imageBitmap.getWidth();
            int h = imageBitmap.getHeight();
            Matrix mtx =  new Matrix();
            imageBitmap = Bitmap.createBitmap(imageBitmap,0,0,w,h,mtx,false);
            imageBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888,true);
            imgInput.setImageBitmap(imageBitmap);
            mTess.setImage(imageBitmap);
        }
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            prepareData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTess = new TessBaseAPI()   ;
        mTess.setPageSegMode(TessBaseAPI.OEM_CUBE_ONLY);
        mTess.init(getFilesDir() + "", "vie");
        imgInput = (ImageView) findViewById(R.id.img_input);
        Bitmap input = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        imgInput.setImageBitmap(input);
        btn = findViewById(R.id.bt_action);
        btnCamera = findViewById(R.id.camera);
        tvResult = findViewById(R.id.tv_result);
        mTess.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.test));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = mTess.getUTF8Text();
                tvResult.setText(res);
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });



    }

    public void prepareData() throws IOException {
        File dir = new File(getFilesDir() + "/tessdata");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File trainedData = new File(getFilesDir() + "/tessdata/vie.traineddata");
        if (!trainedData.exists()) {
            AssetManager asset = getAssets();

            InputStream is = asset.open("tessdata/vie.traineddata");
            OutputStream os = new FileOutputStream(getFilesDir() + "/tessdata/vie.traineddata");

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            os.flush();
            os.close();
        }
    }
}
