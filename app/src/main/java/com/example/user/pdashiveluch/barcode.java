package com.example.user.pdashiveluch;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class barcode extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtresult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;



    public static final String RESULT = "result";
    public static String mess=" ";
    String s;
    int inv=0;
    String[] allcodes = {"11111", "22222", "33333", "44444", "55555", "66666", "77777",
            "88888", "99999", "00000", "10121", "10122", "10123", "10124", "10125", "12321", "15151", "16161", "14141"};

    String [] results = {"Сталкерская куртка","Кольчужная куртка бандитов", "Броня \"Долга\"", "Броня \"Свободы\"", "Комбинезон  \"Монолита\"",
            "Халат  ученых", "Комбинезон \"СЕВА\"","Комбинезон \"Заря\"","Комбинезон \"ЭКОЛОГ\"",
            "Броня военных","Артефакт ОГНЕННЫЙ ШАР","Артефакт ЗОЛОТАЯ РЫБКА",
            "Артефакт ДУША","Артефакт БАТАРЕЙКА","Артефакт МЕДУЗА","Ремонт костюма", "Восстановление антирада","Восстановление аптечек", "Опыт: 10 ед."};
   // int[] suites = {R.drawable.kurt, R.drawable.duty, R.drawable.bandit,
 //           R.drawable.freedom, R.drawable.science, R.drawable.monolitst, R.drawable.seva,
 //           R.drawable.zaria, R.drawable.ecolog, R.drawable.bulat};

    //0. 11111 - сталкерская куртка
    //1. 22222 - бандитская куртка
    //2. 33333 - броня Долга
    //3. 44444 - броня Свободы
    //4. 55555 - Броня Монолита
    //5. 66666 - халат ученого
    //6. 77777 - костюм Сева
    //7. 88888 - костюм Заря
    //8. 99999 - костюм Эколог
    //9. 00000 - бронекостюм "Булат"
    //10. 10121 - артефакт Огненный шар
    //11. 10122 - артефакт Золотая рыбка
    //12. 10123 - артефакт Батарейка
    //13. 10124 - артефакт Медуза
    //14. 10125 - артефакт Душа
    //15. 12321 - ремонт костюма
    //16. 15151 - восстановление антирада
    //17. 16161 - восстановление аптечек


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(barcode.this,
                                new String[]{android.Manifest.permission.CAMERA},RequestCameraPermissionID);
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtresult = (TextView) findViewById(R.id.txtResult);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/graffiti.ttf");
        txtresult.setTypeface(face);

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(barcode.this,
                            new String[]{android.Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes=detections.getDetectedItems();
                if (qrcodes.size()!=0)
                {

                    txtresult.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            barcode.mess=qrcodes.valueAt(0).displayValue;
                            pda.mess=barcode.mess;
                            txtresult.setText(barcode.mess.toString());
                            for (int i=0;i<allcodes.length;i++)
                            {if (barcode.mess.toString().equals(allcodes[i])) {
                                txtresult.setText("НАЙДЕНО: " + "\n" + results[i]);
                            inv=1;
                            }
                            }

                            if (barcode.mess.toString().equals(allcodes[10])||barcode.mess.toString().equals(allcodes[11])||barcode.mess.toString().equals(allcodes[12])||barcode.mess.toString().equals(allcodes[13])||barcode.mess.toString().equals(allcodes[14]))
                            {
                                pda.artslot.setVisibility(View.VISIBLE);
                            }



                           //txtresult.setText(qrcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        txtresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), barcode.mess.toString(), Toast.LENGTH_LONG).show();
                cameraSource.stop();
                pda.qr_result=1;
                finish();



            }
        });

    }


}
