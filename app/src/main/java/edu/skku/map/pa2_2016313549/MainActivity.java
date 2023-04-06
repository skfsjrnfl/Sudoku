package edu.skku.map.pa2_2016313549;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.time.format.TextStyle;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public float dpToPx(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    int[][] anwser;
    int[][] submit;
    int[][] column;
    int[][] row;
    int[] rownumber;
    int[] colnumber;
    GridView board;
    GridView columhint;
    GridView rowhint;
    boardAdapter basic;
    rowadapter rowadapter;
    columnadapter columnadapter;
    String urlbit;
    Bitmap searchbit;
    EditText editText;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String clientID = "OzgE6emztVPBHftq8qT9";
        String clientsecret = "PjvRfoptBB";
        Button search = (Button) findViewById(R.id.search);
        Button gallery = (Button) findViewById(R.id.gallery);
        editText = (EditText) findViewById(R.id.query);
        board = (GridView) findViewById(R.id.board);
        columhint = (GridView) findViewById(R.id.colnumber);
        rowhint = (GridView) findViewById(R.id.rownumber);

        imageView = (ImageView) findViewById(R.id.imageView);




        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (anwser[position/20][position%20]==-1){
                    board.setAdapter(basic);
                    Toast.makeText(MainActivity.this,"Worng Place",Toast.LENGTH_SHORT).show();
                    for (int i=0;i<20;i++){
                        for (int j=0;j<20;j++){
                            submit[i][j]=-1;
                        }
                    }
                }else {
                    ImageView cell = (ImageView) view;
                    cell.setImageResource(R.drawable.black);
                    submit[position/20][position%20]=1;
                    int complete = 1;
                    for (int i=0;i<20;i++){
                        if (complete==0){
                            break;
                        }
                        for (int j=0;j<20;j++){
                            if (submit[i][j]!=anwser[i][j]) {
                                complete = 0;
                                break;
                            }
                        }
                    }
                    if (complete==1){
                        Toast.makeText(MainActivity.this,"FINISH",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {

                }
                else{

                    OkHttpClient client = new OkHttpClient();
                    String query = "";

                    try {
                        query = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openapi.naver.com/v1/search/image").newBuilder();
                    urlBuilder.addQueryParameter("query", query);
                    urlBuilder.addQueryParameter("display", "1");

                    String url = urlBuilder.build().toString();

                    Request req = new Request.Builder()
                            .url(url)
                            .addHeader("X-Naver-Client-Id", clientID)
                            .addHeader("X-Naver-Client-Secret", clientsecret)
                            .method("GET", null)
                            .build();

                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            Gson gson = new GsonBuilder().create();
                            JsonParser parser = new JsonParser();
                            JsonElement jsonitems = parser.parse(response.body().charStream()).getAsJsonObject().get("items");
                            items[] image = gson.fromJson(jsonitems, items[].class);
                            urlbit = image[0].getLink();

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url1 = new URL(urlbit);
                                        InputStream inputStream = url1.openStream();
                                        searchbit = BitmapFactory.decodeStream(inputStream);
                                        inputStream.close();
                                        makeboard(searchbit);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            thread.start();

                            try {
                                thread.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    basic = new boardAdapter(MainActivity.this,anwser);
                                    board.setAdapter(basic);

                                    rowadapter = new rowadapter(MainActivity.this,row);
                                    rowhint.setAdapter(rowadapter);

                                    columnadapter = new columnadapter(MainActivity.this,column);
                                    columhint.setAdapter(columnadapter);

                                    int complete = 1;
                                    for (int i=0;i<20;i++){
                                        if (complete==0){
                                            break;
                                        }
                                        for (int j=0;j<20;j++){
                                            if (submit[i][j]!=anwser[i][j]) {
                                                complete = 0;
                                                break;
                                            }
                                        }
                                    }
                                    if (complete==1){
                                        Toast.makeText(MainActivity.this,"FINISH",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap imp = BitmapFactory.decodeStream(in);
                    in.close();
                    makeboard(imp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                basic = new boardAdapter(MainActivity.this,anwser);
                board.setAdapter(basic);

                rowadapter = new rowadapter(MainActivity.this,row);
                rowhint.setAdapter(rowadapter);

                columnadapter = new columnadapter(MainActivity.this,column);
                columhint.setAdapter(columnadapter);

                int complete = 1;
                for (int i=0;i<20;i++){
                    if (complete==0){
                        break;
                    }
                    for (int j=0;j<20;j++){
                        if (submit[i][j]!=anwser[i][j]) {
                            complete = 0;
                            break;
                        }
                    }
                }
                if (complete==1){
                    Toast.makeText(MainActivity.this,"FINISH",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void makeboard(Bitmap img) {

        int dp;
        //Resize
        int width = img.getWidth();
        int height = img.getHeight();
        if (width > height) {
            img = Bitmap.createBitmap(img, (width - height) / 2, 0, height, height);
        } else {
            img = Bitmap.createBitmap(img, 0, (height - width) / 2, width, width);
        }
        dp = (int) dpToPx(MainActivity.this, 280);
        img = Bitmap.createScaledBitmap(img, dp, dp, false);
        //Convert image into black-and-white
        Bitmap gray = Bitmap.createBitmap(dp, dp, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(gray);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                85.f, 85.f, 85.f, 0.f, -255.f * 128,
                85.f, 85.f, 85.f, 0.f, -255.f * 128,
                85.f, 85.f, 85.f, 0.f, -255.f * 128,
                0f, 0f, 0f, 1f, 0f
        });

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        c.drawBitmap(img, 0, 0, paint);


        anwser = new int[20][20];
        submit = new int[20][20];
        Bitmap[] image_cut = new Bitmap[400];
        int r, g, b;
        for (int i = 0; i < 400; i++) {
            image_cut[i] = Bitmap.createBitmap(gray, (dp / 20) * (i % 20), (dp / 20) * (i / 20), dp / 20, dp / 20);
            int black = 0;
            for (int x = 0; x < image_cut[i].getWidth(); x++) {
                for (int y = 0; y < image_cut[i].getHeight(); y++) {
                    int pixel = image_cut[i].getPixel(x, y);
                    r = Color.red(pixel);
                    g = Color.green(pixel);
                    b = Color.blue(pixel);
                    black = black + (int) (0.2989 * r + 0.5870 * g + 0.1140 * b);
                }
            }
            black = black / (image_cut[i].getWidth() * image_cut[i].getHeight());
            if (black > 128)
                anwser[i/20][i%20]=-1;
            else
                anwser[i/20][i%20]=+1;
            submit[i/20][i%20]=-1;
        }//board make
        //imageView.setImageBitmap(gray);
        //making row number
        row = new int[20][10];
        column = new int[20][10];
        rownumber = new int[20];
        colnumber = new int[20];
        int pointer;

        for (int i = 0; i<20;i++){
            int count=0;
            int num=0;
            pointer=0;
            for (int j=0; j<20; j++){
                if (anwser[i][j]==1) {
                    count++;
                }
                else if((anwser[i][j]==-1)&&(count!=0)){
                    row[i][pointer]=count;
                    num++;
                    pointer++;
                    count=0;
                }
                if ((j==19)&&(count!=0)){
                    row[i][pointer]=count;
                    num++;
                    pointer++;
                    count=0;
                }
            }
            rownumber[i]=num;
        }

        for (int j = 0; j<20;j++){
            int count=0;
            int num=0;
            pointer=0;
            for (int i=0; i<20; i++){
                if (anwser[i][j]==1) {
                    count++;
                }
                else if((anwser[i][j]==-1)&&(count!=0)){
                    column[j][pointer]=count;
                    num++;
                    pointer++;
                    count=0;
                }
                if ((i==19)&&(count!=0)){
                    column[j][pointer]=count;
                    num++;
                    pointer++;
                    count=0;
                }
            }
            colnumber[j]=num;
        }


    }



    class items {
        public String title;
        public String link;
        public String thumbnail;
        public String sizeheight;
        public String sizewidth;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getSizeheight() {
            return sizeheight;
        }

        public void setSizeheight(String sizeheight) {
            this.sizeheight = sizeheight;
        }

        public String getSizewidth() {
            return sizewidth;
        }

        public void setSizewidth(String sizewidth) {
            this.sizewidth = sizewidth;
        }
    }

    public class boardAdapter extends BaseAdapter {
        Context context;
        int[][] board;

        public boardAdapter(Context c, int[][] b){
            context=c;
            board = b;
        }

        @Override
        public int getCount() {
            return 400;
        }

        @Override
        public Object getItem(int position) {
            return board[position/20][position%20];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            int dp = (int) dpToPx(MainActivity.this, 14);
            imageView.setLayoutParams(new GridView.LayoutParams(dp, dp));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(0,0,0,0);
            imageView.setImageResource(R.drawable.white);
            return imageView;
        }

    }

    public class columnadapter extends BaseAdapter{
        Context context;
        int[][] col;
        int max;

        public columnadapter(Context c, int[][] r){
            context=c;
            col = r;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Integer getItem(int position) {
            return col[position/20][position%10];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            int hei = (int) dpToPx(MainActivity.this, 100);
            int wid = (int) dpToPx(MainActivity.this, 14);
            textView.setLayoutParams(new GridView.LayoutParams(wid,hei));
            textView.setPadding(0,0,5,0);
            textView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(9);
            String hint="";
            for (int i=0;i<colnumber[position];i++){
                hint=hint+"\n"+String.valueOf(col[position][i]);
            }
            textView.setText(hint);
            return textView;
        }
    }

    public class rowadapter extends BaseAdapter{
        Context context;
        int[][] row;
        int max;

        public rowadapter(Context c, int[][] r){
            context=c;
            row = r;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Integer getItem(int position) {
            return row[position/20][position%10];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            int hei = (int) dpToPx(MainActivity.this, 14);
            int wid = (int) dpToPx(MainActivity.this, 100);
            textView.setLayoutParams(new GridView.LayoutParams(wid,hei));
            textView.setPadding(0,0,5,0);
            textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            textView.setTextSize(9);
            String hint="";
            for (int i=0;i<rownumber[position];i++){
                hint=hint+" "+String.valueOf(row[position][i]);
            }
            textView.setText(hint);
            return textView;
        }
    }


}
