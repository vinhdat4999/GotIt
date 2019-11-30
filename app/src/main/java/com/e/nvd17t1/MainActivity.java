package com.e.nvd17t1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    Button btn;
    TextView tvResult;


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
        mTess = new TessBaseAPI();
        mTess.init(getFilesDir() + "", "vie");
        imgInput = (ImageView) findViewById(R.id.img_input);
        Bitmap input = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        imgInput.setImageBitmap(input);
        btn = findViewById(R.id.bt_action);
        tvResult = findViewById(R.id.tv_result);
        mTess.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.test));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = mTess.getUTF8Text();
                tvResult.setText(res);
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
