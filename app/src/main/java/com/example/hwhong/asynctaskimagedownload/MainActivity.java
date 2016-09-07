package com.example.hwhong.asynctaskimagedownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start, end;
    private ImageView imageView;
    private TextView tv;

    private Task task;

    private String imgUrl = "https://d235mwrq2dn9n5.cloudfront.net/wp-content/uploads/2016/05/02111544/spotify-260516.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start);
        end = (Button) findViewById(R.id.end);
        imageView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);

        start.setOnClickListener(this);
        end.setOnClickListener(this);

        task = new Task();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                imageView.setImageBitmap(null);

                if(!task.isCancelled()) {
                    task.execute(imgUrl);
                } else {
                    Toast.makeText(getApplicationContext(), "Task is Cancelled", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.end:
                task.cancel(true);
                break;
        }
    }

    private class Task extends AsyncTask<String, Integer, Bitmap>{

        private Bitmap bitmap = null;
        private InputStream stream = null;
        private ByteArrayOutputStream output = new ByteArrayOutputStream();

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "in onPreExecute()", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setTitle(values[0] + "%");
            tv.setText(values[0] + "%");
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {


            try {

                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                stream = connection.getInputStream();
                double length = connection.getContentLength();
                byte[] buffer = new byte[64];

                int readsize = 0;
                double sum = 0;

                while((readsize = stream.read(buffer)) != -1) {
                    output.write(buffer, 0, readsize);
                    //record process updates and then publishes it
                    sum += (readsize/length) * 100;
                    publishProgress((int) sum);
                }

                byte[] results = output.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(results, 0, results.length);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    //tidy up
                    stream.close();
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*
            try {
                //Toast.makeText(getApplicationContext(), strings[0] + " is the URL", Toast.LENGTH_SHORT).show();
                URL url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openStream());

            } catch (IOException e) {
                e.printStackTrace();
                bitmap = null;
                Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_SHORT).show();
            }
            */

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            super.onPostExecute(bitmap);
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "in onCancelled", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            Toast.makeText(getApplicationContext(), "in onCancelled(Bitmap bitmap)", Toast.LENGTH_SHORT).show();
            super.onCancelled(bitmap);
        }
    }
}
