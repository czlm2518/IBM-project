package com.example.sociar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AR extends AppCompatActivity {

    float SmoothFactorCompass = 0.01f;
    float SmoothThresholdCompass = 30.0f;
    float oldCompass = 0.0f;
    double smooth = 0.5f;
    double lastSin, lastCos;
    private ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private SensorEventListener rotationUpdate;
    private float fov;
    private float pictureWidth;
    private float longitude, latitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //disable app title and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        //permissions
        if (!getPermissions()) System.exit(1);
        // camera code
        if (!init_camera()) System.exit(2);
        //sensor code
        if (!init_sensors()) System.exit(3);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Init Fuse
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AR.this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    //Showing the latitude, longitude and accuracy on the home screen.
                    for (Location location : locationResult.getLocations()) {
                        longitude = (float) location.getLongitude();
                        latitude = (float) location.getLatitude();
                        ((TextView) findViewById(R.id.latitude)).setText(String.valueOf(latitude));
                        ((TextView) findViewById(R.id.longitude)).setText(String.valueOf(longitude));
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean init_sensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        rotationUpdate = new SensorEventListener() {
            private final float[] rotMat = new float[9];
            private final float[] orientation = new float[3];

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                    SensorManager.getRotationMatrixFromVector(rotMat, event.values);
                    SensorManager.getOrientation(rotMat, orientation);

                    orientation[0] = (float) Math.toDegrees(orientation[0]);
                    orientation[1] = (float) Math.toDegrees(orientation[1]);
                    orientation[2] = (float) Math.toDegrees(orientation[2]);

                    String x = "flat yaw " + orientation[0];
                    String y = "flat pitch " + orientation[1];
                    String z = "flat roll " + orientation[2];

                    ((TextView) findViewById(R.id.rotX)).setText(x);
                    ((TextView) findViewById(R.id.rotY)).setText(y);
                    ((TextView) findViewById(R.id.rotZ)).setText(z);

                    //variable for broadcast
                    Intent brca = getIntent();
                    String data = brca.getStringExtra("info");
                    





                    String d1, d2;
                    d1 = "Looking down";

                    if (Math.abs(orientation[2]) > 40) {
                        d1 = "Looking up";
                    }


                    //find azimuth independently of pitch
                    float azimuth = (float) Math.atan2((rotMat[1] - rotMat[3]), (rotMat[0] + rotMat[4]));

                    lastSin = smooth * lastSin + (1 - smooth) * Math.sin(azimuth);
                    lastCos = smooth * lastCos + (1 - smooth) * Math.cos(azimuth);

                    azimuth = (float) Math.atan2(lastSin, lastCos);

                    //todo funny behaviour around south
                    //smoothing function to avoid jittering texts
                    if (Math.abs(azimuth - oldCompass) < 180) {
                        if (Math.abs(azimuth - oldCompass) > SmoothThresholdCompass) {
                            oldCompass = azimuth;
                        } else {
                            oldCompass = oldCompass + SmoothFactorCompass * (azimuth - oldCompass);
                        }
                    } else {
                        if (360.0 - Math.abs(azimuth - oldCompass) > SmoothThresholdCompass) {
                            oldCompass = azimuth;
                        } else {
                            if (oldCompass > azimuth) {
                                oldCompass = (oldCompass + SmoothFactorCompass * ((360 + azimuth - oldCompass) % 360) + 360) % 360;
                            } else {
                                oldCompass = (oldCompass - SmoothFactorCompass * ((360 - azimuth + oldCompass) % 360) + 360) % 360;
                            }
                        }
                    }

                    d2 = String.valueOf(Math.toDegrees(azimuth));

                    ((TextView) findViewById(R.id.debug1)).setText(d1);
                    ((TextView) findViewById(R.id.debug2)).setText(d2);

                    //draw user boxes
                    updateBubbles(oldCompass);

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        return true;
    }

    private void updateBubbles(float azimuth) {
        List<User> users = getUsersInView(azimuth, getUsers());

        FrameLayout layout = findViewById(R.id.userlayout);
        layout.removeAllViews();

        String[] visible = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            TextView text = new TextView(this);

            float bx = (float) ((user.longitude * pictureWidth) / (user.latitude * Math.tan(fov / 2)));

            text.setText(user.name);
            text.setX(bx + layout.getWidth() / 2f);
            text.setY(layout.getHeight() / 2f);

            layout.bringChildToFront(text);
            layout.addView(text);
            visible[i] = user.name;
        }

        ((TextView) findViewById(R.id.debug1)).setText(Arrays.toString(visible));

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(rotationUpdate, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rotationUpdate);
    }

    private boolean getPermissions() { //todo this is janky. read up on permissions
        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {

                    } else {
                        //explain why permission is necessary
                    }
                });

        if (ContextCompat.checkSelfPermission(AR.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // go work
            return true;
        } else {
            //get permissions
            launcher.launch(Manifest.permission.CAMERA);
        }
        return false;
    }

    private boolean init_camera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        //get fov (deprecated methods are fine to only retrieve settigs)
        android.hardware.Camera camera;
        try {
            camera = android.hardware.Camera.open();
            android.hardware.Camera.Parameters p = camera.getParameters();
            fov = p.getHorizontalViewAngle();
            pictureWidth = p.getPreviewSize().width;
            Toast.makeText(this, "picture size: " + p.getPreviewSize().width, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            return false;
        }

        Toast.makeText(this, "FOV: " + fov, Toast.LENGTH_SHORT).show();

        return true;
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        PreviewView cameraview = findViewById(R.id.camera_view);
        preview.setSurfaceProvider(cameraview.getSurfaceProvider());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    private User[] getUsers() { //long, lat //todo connections integration
        return new User[]{
                new User("North", -1.571661f, 84.7709f),
                new User("East", 28.428339f, 54.770897f),
                new User("South", -1.571661f, 24.770897f),
                new User("West", -31.571661f, 54.770897f),
                new User("opposite", -1.571298f, 54.771136f), //opposite house
                new User("cathedral", -1.576235f, 54.773478f), //cathedral
                new User("25", -1.5711873f, 54.7708742f) //25 os crt
        };
    }

    private List<User> getUsersInView(float azimuth, User[] users) {

        List<User> visible = new ArrayList<>();

        for (User user : users) {
            float[] toUser = new float[]{user.longitude - longitude, user.latitude - latitude};
            float[] rot = new float[]{
                    (float) (toUser[0] * Math.cos(azimuth) - toUser[1] * Math.sin(azimuth)),
                    (float) (toUser[0] * Math.sin(azimuth) + toUser[1] * Math.cos(azimuth)),
            };
            if (Math.abs(Math.toDegrees(Math.atan2(rot[0], rot[1]))) < fov / 2) {
                visible.add(new User(user.name, rot[0], rot[1], user.interests));
            }
        }

        return visible;
    }
}
