package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PhotoTagger extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private Uri imageUri;

    private String currentIMG;
    private String currentTimeStamp;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phototagger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SQLiteDatabase db = this.openOrCreateDatabase("DB_IMG", Context.MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS DB_IMG;");
        try {
            db.execSQL("CREATE TABLE DB_IMG (ID INT PRIMARY KEY, NAME TEXT, TAGS TEXT, DATE TEXT, TYPE TEXT);");
        }
        catch(Exception e){
            Log.d("SQL", "Already exists!");
        }
        refreshDatabase("EMPTY####");
    }

    @Override
    protected void onActivityResult (int reqCode, int resCode, Intent x){
        super.onActivityResult(reqCode, resCode, x);
        if (reqCode == 1 && resCode == RESULT_OK){
            ImageView mainView = (ImageView) findViewById(R.id.imageView);

            Bitmap image = BitmapFactory.decodeFile(imagePath);

            mainView.setImageBitmap(image);

            MediaScannerConnection.scanFile(this, new String[]{imagePath}, null, null);
        }
    }

    public void foo(View view){
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File imageFile = new File(imageDir, "IMG_" + timeStamp + ".png");
        Log.d("PHOTO_PATH", "Saved to: " + imageFile.getAbsolutePath());

        imagePath = imageFile.getAbsolutePath();

        imageUri = FileProvider.getUriForFile(this, "com.example.finalproject.fileprovider", imageFile);

        currentIMG = "IMG_" + timeStamp;
        currentTimeStamp = new SimpleDateFormat("MM/dd/yyyy - HH:mm", Locale.US).format(new Date());;

        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(camIntent, 1);
    }

    public void SaveButton(View view){
        SQLiteDatabase db = this.openOrCreateDatabase("DB_IMG", Context.MODE_PRIVATE, null);
        EditText tagsField = findViewById(R.id.addTags);
        String tags = tagsField.getText().toString();

        ContentValues values = new ContentValues();
        values.put("NAME", currentIMG);
        values.put("TAGS", tags);
        values.put("DATE", currentTimeStamp);
        values.put("TYPE", "Photo");

        db.insert("DB_IMG", null, values);
        Log.d("SQL", "Saved image with tags!");

        refreshDatabase("EMPTY####");
    }

    public void FindButton(View view){
        EditText tagsField = findViewById(R.id.searchTags);
        String tags = tagsField.getText().toString();
        refreshDatabase(tags);
    }

    public void refreshDatabase(String tags){
        SQLiteDatabase db = this.openOrCreateDatabase("DB_IMG", Context.MODE_PRIVATE, null);
        Cursor c;
        if (Objects.equals(tags, "EMPTY####")){
            c = db.rawQuery("SELECT * FROM DB_IMG", null);
        }
        else{
            c = db.rawQuery("SELECT * FROM DB_IMG WHERE TAGS LIKE ?", new String[]{ "%" + tags + "%" });
        }
        int rowNumber = c.getCount();

        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ArrayList<DatabaseItem>data = new ArrayList<>();

        if (c != null && c.moveToLast()) {
            do{
                int id = c.getInt(c.getColumnIndexOrThrow("ID"));
                String a = c.getString(c.getColumnIndexOrThrow("NAME"));
                String aa = c.getString(c.getColumnIndexOrThrow("TAGS"));
                String aaa = c.getString(c.getColumnIndexOrThrow("DATE"));
                String aaaa = c.getString(c.getColumnIndexOrThrow("TYPE"));

                Log.d("DB_ROW", "ID: " + id + " | NAME: " + a + " | TAGS: " + aa + " | DATE: " + aaa + " | TYPE: " + aaaa);

                if (Objects.equals(aaaa, "Photo")){
                    File imageFile = new File(imageDir, c.getString(1) + ".png");
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    data.add(new DatabaseItem(bitmap, c.getString(2), c.getString(3)));
                }
            } while (c.moveToPrevious());
        }
        else{
            data.add(new DatabaseItem(null, "unavailable", ""));
        }


        RecyclerView recyclerView = findViewById(R.id.myList);
        DatabaseItemAdapter adapter = new DatabaseItemAdapter(this, data);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public void backButton(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void popTags(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myVisionTester();
                } catch (IOException e) {
                    Log.v("MYTAG", "Nope, didn't work :(");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void myVisionTester() throws IOException {
        //1. ENCODE image.
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);
        Image myimage = new Image();
        myimage.encodeContent(bout.toByteArray());

        //2. PREPARE AnnotateImageRequest
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setImage(myimage);
        Feature f = new Feature();
        f.setType("LABEL_DETECTION");
        f.setMaxResults(5);
        List<Feature> lf = new ArrayList<Feature>();
        lf.add(f);
        annotateImageRequest.setFeatures(lf);

        //3.BUILD the Vision
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyA1U7hj1EWApPAOecXEcx2PvXpkqoibH00"));
        Vision vision = builder.build();

        //4. CALL Vision.Images.Annotate
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
        list.add(annotateImageRequest);
        batchAnnotateImagesRequest.setRequests(list);
        Vision.Images.Annotate task = vision.images().annotate(batchAnnotateImagesRequest);
        BatchAnnotateImagesResponse response = task.execute();
        Log.v("MYTAG", "Found Response");
        List<AnnotateImageResponse> responses = response.getResponses();
        AnnotateImageResponse res = responses.get(0);
        List<EntityAnnotation> labels = res.getLabelAnnotations();
        if (labels != null && !labels.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(labels.size(), 2); // first 2

            for (int i = 0; i < limit; i++) {
                sb.append(labels.get(i).getDescription()).append(", ");
            }

            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }

            runOnUiThread(() -> {
                TextView tv = findViewById(R.id.addTags);
                tv.setText(sb.toString());
            });
        }
    }
}