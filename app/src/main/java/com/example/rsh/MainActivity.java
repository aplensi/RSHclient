package com.example.rsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.example.rsh.databinding.ActivityMainBinding;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'rsh' library on application startup.
    static {
        System.loadLibrary("rsh");
    }
    private MyAdapter myAdapter;
    String key = "";

    private LinearLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        List<Item> items = new ArrayList<Item>();
        Vector<String> namesOfItems = new Vector<>();
        Vector<String> urlImageItems = new Vector<>();
        Vector<String> priceItems = new Vector<>();
        Vector<String> countOfVout = new Vector<>();
        Vector<Boolean> votes = new Vector<>();
        int countOfItems = -1;

        String fileKey = "myKey.txt";
        File file = new File(getFilesDir(), fileKey);

        RecyclerView recyclerView = findViewById(R.id.recyclerviewItems);

        MyAdapter adapter = new MyAdapter(this, items);


        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                adapter.notifyItemChanged(position);
                if (key == "")
                {
                    key = getKey(getApplicationContext(), fileKey);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        char[] ch = key.toCharArray();
                        String st = getVout(ch,""+position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                                int h = Integer.parseInt(countOfVout.get(position));
                                boolean vote = false;
                                char fc = st.charAt(0), fr = ' ';
                                if(fc == '+' && fr == ' ')
                                {
                                    fr = '+';
                                    h = h + 1;
                                    vote = true;
                                    countOfVout.set(position, ""+ h );
                                    
                                }
                                else if(fc == '-' && fr == ' ')
                                {
                                    fr = '-';
                                    h = h - 1;
                                    vote = false;
                                    countOfVout.set(position, ""+ h );
                                }
                                else if(fc == '+' && fr == '+')
                                {
                                    fr = '-';
                                    h = h - 1;
                                    vote = false;
                                    countOfVout.set(position, ""+ h );
                                }
                                else if (fc == '-' && fr == '-'){
                                    fr = '+';
                                    h = h + 1;
                                    vote = false;
                                    countOfVout.set(position, ""+ h );
                                }

                                adapter.updateItem(position, namesOfItems.get(position),countOfVout.get(position), priceItems.get(position), urlImageItems.get(position), vote);
                            }
                        });
                    }
                }).start();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = connectToServer();
                int countOfPages = Integer.parseInt(result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView)findViewById(R.id.textView);
                        txt.setText("Загрузка");
                        //txt.setMovementMethod(new ScrollingMovementMethod());
                        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        if (file.exists()) {
                            long fileSize = file.length();
                            if (fileSize == 0) {
                                try {
                                    FileOutputStream outputStream = new FileOutputStream(file);
                                    outputStream.write(createRequest("j").getBytes());
                                    outputStream.close();
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            try {
                                file.createNewFile();
                                FileOutputStream outputStream = new FileOutputStream(file);
                                outputStream.write(createRequest("j").getBytes());
                                outputStream.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                key = getKey(getApplicationContext(), fileKey);
                                char[] ch = key.toCharArray();
                                String vot = getVoutC(ch);
                                for(int i = 0; i < countOfPages; i++)
                                {
                                    namesOfItems.add(getItem("name", i));
                                    urlImageItems.add(getItem("url", i));
                                    priceItems.add(getItem("price", i));
                                    countOfVout.add(getItem("vote", i));
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int i = 0; i < vot.length(); i++)
                                        {
                                            if(vot.charAt(i) == '-')
                                            {
                                                votes.add(false);
                                            }
                                            else votes.add(true);
                                        }
                                        txt.setVisibility(View.GONE);
                                        recyclerView.setAdapter(adapter);
                                        for (int i = 0; i < countOfPages; i++)
                                        {
                                            items.add(new Item("Название: " + namesOfItems.get(i), "Кол-во голосов: " + countOfVout.get(i), "Цена: " + priceItems.get(i),urlImageItems.get(i), votes.get(i)));
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }

    public String getKey(Context context, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String firstLine = bufferedReader.readLine();

            fileInputStream.close();

            return firstLine;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public native String connectToServer();
    public native String createRequest(String request);
    public native String getVout(char[] key, String number);
    public native String getVoutC(char[] key);
    public native String getItem(String name, int index);
}