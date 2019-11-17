package com.dbao1608.fingerdraw;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_BITMAP_FROM_CAMERA = 1000;
    private DrawFingerView drawFingerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawFingerView = findViewById(R.id.drawFingerView);
        drawFingerView.setImageDrawable(getDrawable(R.drawable.image_test));
        findViewById(R.id.photoBtn).setOnClickListener(this);
        findViewById(R.id.clearBtn).setOnClickListener(this);
        findViewById(R.id.undoBtn).setOnClickListener(this);
        findViewById(R.id.saveBtn).setOnClickListener(this);

        findViewById(R.id.redBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFingerView.setColor(Color.RED);
            }
        });

        findViewById(R.id.blackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFingerView.setColor(Color.BLACK);
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoBtn:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, PICK_BITMAP_FROM_CAMERA);
                }
                break;
            case R.id.clearBtn:
                drawFingerView.resetView();
                break;
            case R.id.undoBtn:
                drawFingerView.undoPath();
                break;
            case R.id.saveBtn:
                saveImage(setViewToBitmap(drawFingerView));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_BITMAP_FROM_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                drawFingerView.setImageBitmap(imageBitmap);
            }
        }
    }

    private Bitmap setViewToBitmap(View view){
        Bitmap returnBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable backgroundDrawable = view.getBackground();
        if(backgroundDrawable != null){
            backgroundDrawable.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnBitmap;
    }

    private void saveImage(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myDir = cw.getDir("saveImages", Context.MODE_PRIVATE);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String name = "Image_" + n + ".jpg";
        File file = new File(myDir, name);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
