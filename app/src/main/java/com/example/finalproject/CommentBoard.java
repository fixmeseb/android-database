package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentBoard extends AppCompatActivity {

    private String currentTags = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.comment_board);
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
    protected void genComment(CommentCallback callback, String user, String tags){
        Map<String, String> dict = new HashMap(){{
            put("Nei", "a nice friend");
            put("Lu", "a jokester friend");
            put("Zoff", "a mean friend");
            put("Prost", "a cheerful friend");
            put("Versatility", "a sarcastic friend");
        }};

        String friendType = dict.get(user);

        String json = "{\"contents\":[{\"parts\":[{\"text\":\"write a comment in twenty words or less like you are " + friendType + " who's commenting on a photo with the following characteristics: " + tags + ". \"}]}]}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request r = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                .addHeader("x-goog-api-key", "AIzaSyBcpz0br-dYRIrv0B01LQTlKIdx5WWTA3Y")
                .post(body)
                .build();

        OkHttpClient c = new OkHttpClient();
        c.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException{
                String returnedInfo = response.body().string();
                Log.v("MYTAG", returnedInfo);
                try {
                    JSONObject json = new JSONObject(returnedInfo);
                    JSONArray candidates = null;
                    candidates = json.getJSONArray("candidates");
                    JSONObject firstCandidate = candidates.getJSONObject(0);
                    JSONObject content = firstCandidate.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");
                    JSONObject firstPart = parts.getJSONObject(0);
                    String text = firstPart.getString("text");
                    Log.v("MYTAG", text);
                    callback.onSuccess(text);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

            }
        });
    }
    public void refreshDatabase(String tags){
        SQLiteDatabase db = this.openOrCreateDatabase("DB_IMG", Context.MODE_PRIVATE, null);
        Cursor c;

        List<String> tagsList = Arrays.asList(tags.split(", "));

        StringBuilder whereClause = new StringBuilder();
        String[] args = new String[tagsList.size()];

        for (int i = 0; i < tagsList.size(); i++) {
            if (i > 0) whereClause.append(" OR ");
            whereClause.append("TAGS LIKE ?");
            args[i] = "%" + tagsList.get(i) + "%";
        }

        if (Objects.equals(tags, "EMPTY####")){
            c = db.rawQuery("SELECT * FROM DB_IMG", null);
        }
        else{
            c = db.rawQuery("SELECT * FROM DB_IMG WHERE " + whereClause.toString(), args);
        }
        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ArrayList<CheckItem> data = new ArrayList<>();

        CheckBox includeSketchBox = findViewById(R.id.includeSketchBox);
        boolean includeSketches = includeSketchBox.isChecked();

        if (c != null && c.moveToLast()) {
            do{
                int id = c.getInt(c.getColumnIndexOrThrow("ID"));
                String a = c.getString(c.getColumnIndexOrThrow("NAME"));
                String aa = c.getString(c.getColumnIndexOrThrow("TAGS"));
                String aaa = c.getString(c.getColumnIndexOrThrow("DATE"));
                String aaaa = c.getString(c.getColumnIndexOrThrow("TYPE"));

                Log.d("DB_ROW", "ID: " + id + " | NAME: " + a + " | TAGS: " + aa + " | DATE: " + aaa + " | TYPE: " + aaaa);

                if (Objects.equals(aaaa, "Photo") || includeSketches){
                    File imageFile = new File(imageDir, c.getString(1) + ".png");
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    data.add(new CheckItem(bitmap, c.getString(2), c.getString(3), false));
                }
            } while (c.moveToPrevious());
        }
        else{
            data.add(new CheckItem(null, "unavailable", "", false));
        }


        RecyclerView recyclerView = findViewById(R.id.myList);
        CheckListAdapter adapter = new CheckListAdapter(this, data);

        adapter.setOnItemSelectedListener(item -> {
            Log.d("CALLBACK", "Callback fired! Tags = " + item.tags);
            TextView selected = findViewById(R.id.selection);
            selected.setText("you selected:\n" + item.tags);
            currentTags = item.tags;
        });
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public void includeSketchBox(View view){
        EditText tagsField = findViewById(R.id.searchTags);
        String tags = tagsField.getText().toString();
        refreshDatabase(tags);
    }

    public void FindButton(View view){
        EditText tagsField = findViewById(R.id.searchTags);
        String tags = tagsField.getText().toString();
        refreshDatabase(tags);
    }

    public void backButton(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void CommentButton(View view){
        ArrayList<CommentItem>data = new ArrayList<>();

        CommentItemAdapter adapter = new CommentItemAdapter(this, data);
        RecyclerView lv = findViewById(R.id.commentsList);
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(adapter);

        int[] profiles = {
                R.drawable.queenbee,
                R.drawable.dadanana,
                R.drawable.man,
                R.drawable.melissa,
                R.drawable.vers
        };

        Random rand = new Random();
        int n = rand.nextInt(4);
        int RNG = n+7;

        String[] usernames = {
                "Nei",
                "Lu",
                "Zoff",
                "Prost",
                "Versatility"
        };

        EditText tagsField = findViewById(R.id.searchTags);
        String tags = tagsField.getText().toString();

        for (int i = 0; i < RNG; i++) {
            final int index = i;
            //data.add(new CommentItem(profiles[index], usernames[index], "\"" + dummyComments[index] + "\"", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())));
            genComment(new CommentCallback() {
                @Override
                public void onSuccess(String text) {
                    runOnUiThread(() -> {
                        data.add(new CommentItem(profiles[index%5], usernames[index%5], "\"" + text + "\"", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())));
                        adapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("MYTAG", "ERROR", e);
                }
            }, usernames[index%5], currentTags);
        }
    }
}
