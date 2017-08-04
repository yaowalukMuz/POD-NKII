package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mist.it.pod_nk.MyConstant.projectString;
import static com.mist.it.pod_nk.MyConstant.serverString;
import static com.mist.it.pod_nk.MyConstant.urlSaveImagePerInvoice;
import static com.mist.it.pod_nk.MyConstant.urlSaveImagePerStore;
import static com.mist.it.pod_nk.MyConstant.urlUploadPicture;

public class DetailJobActivity extends AppCompatActivity {

    @BindView(R.id.txtDJAStore)
    TextView storeTextView;
    @BindView(R.id.txtDJAArrivalTime)
    TextView arrivalTimeTextView;
    @BindView(R.id.txtDJADate)
    TextView dateTextView;
    @BindView(R.id.lisDJAInvoiceList)
    ListView invoiceListView;
    @BindView(R.id.imgDJAOne)
    ImageView firstImageView;
    @BindView(R.id.imgDJATwo)
    ImageView secondImageView;
    @BindView(R.id.imgDJAThree)
    ImageView thirdImageView;
    @BindView(R.id.imgDJAFour)
    ImageView fourthImageView;
    @BindView(R.id.btnDJAArrive)
    Button arriveButton;
    @BindView(R.id.btnDJASavePic)
    Button savePicButton;
    @BindView(R.id.btnDJASignature)
    Button signatureButton;
    @BindView(R.id.btnDJAConfirm)
    Button confirmButton;
    @BindView(R.id.btnDJABack)
    Button backButton;
    @BindView(R.id.linDJABottom)
    LinearLayout linDJABottom;

    String dateString, placeString, subJobNoString, inTimeString, outTimeString, tripNoString, storeString, storeIdString, arriveTimeString, pathImgFirstString, pathImgSecondString, pathImgThirdString, pathImgFourthString, pathImgInviceFirstString;
    String[] loginStrings, invoiceStrings;
    private Uri firstUri, secondUri, thirdUri, fourthUri, invFirstUri;
    private Boolean imgFirstFlagABoolean, imgSecondFlagABoolean, imgThirdFlagABoolean, imgFourthFlagABoolean, imgInvoiceFirstABoolean, flagSaveABoolean;
    private Bitmap imgFirstBitmap = null;
    private Bitmap imgSecondBitmap = null;
    private Bitmap imgThirdBitmap = null;
    private Bitmap imgFourthBitmap = null;
    private Bitmap imgInvoiceFirstBitmap = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_job);
        ButterKnife.bind(this);

        //Set flag img
        imgFirstFlagABoolean = false;
        imgSecondFlagABoolean = false;
        imgThirdFlagABoolean = false;
        imgFourthFlagABoolean = false;

        dateString = getIntent().getStringExtra("Date");
        tripNoString = getIntent().getStringExtra("Position");
        loginStrings = getIntent().getStringArrayExtra("Login");
        subJobNoString = getIntent().getStringExtra("SubJobNo");
        storeString = getIntent().getStringExtra("Place");

        dateTextView.setText(getResources().getText(R.string.Date) + " " + dateString);

        SynGetJobDetail synGetJobDetail = new SynGetJobDetail();
        synGetJobDetail.execute();
    }

    @OnClick(R.id.btnDJABack)
    public void onViewClicked() {
        Intent intent = new Intent(DetailJobActivity.this, ManageJobActivity.class);
        intent.putExtra("Date", dateString);
        intent.putExtra("Position", tripNoString);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("SubJobNo", subJobNoString);
        startActivity(intent);
        finish();

    }

    class SynGetJobDetail extends AsyncTask<Void, Void, String> {
        public SynGetJobDetail() {
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("TAG", "Send ==> " + subJobNoString + " , " + storeString);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("subjob_no", subJobNoString)
                        .add("invoiceNo", storeString)
                        .add("isAdd", "true")
                        .build();
                Request request = builder.post(requestBody).url(MyConstant.urlGetJobDetail).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Tag", s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    storeIdString = jsonObject1.getString("StoreId");
                    arriveTimeString = jsonObject1.getString("ArrivalTime");
                    placeString = jsonObject1.getString("DetailDesc");

                    pathImgFirstString = jsonObject1.getString("ImgFileName_1");
                    pathImgSecondString = jsonObject1.getString("ImgFileName_2");
                    pathImgThirdString = jsonObject1.getString("ImgFileName_3");
                    pathImgFourthString = jsonObject1.getString("ImgFileName_4");

                    inTimeString = jsonObject1.getString("InTime");
                    outTimeString = jsonObject1.getString("OutTime");

                    JSONArray jsonArray1 = jsonObject1.getJSONArray("Invoice");
                    invoiceStrings = new String[jsonArray1.length()];

                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                        invoiceStrings[j] = jsonObject2.getString("Invoice");
                    }
                }

                storeTextView.setText(getResources().getText(R.string.Store) + " : " + placeString);
                arrivalTimeTextView.setText(getResources().getText(R.string.ArrivalTime) + " " + arriveTimeString);
                InvoiceListAdaptor invoiceListAdaptor = new InvoiceListAdaptor(DetailJobActivity.this, invoiceStrings);
                invoiceListView.setAdapter(invoiceListAdaptor);


                Log.d("Tag", inTimeString + outTimeString);

                if (inTimeString.equals("null")) {
                    arriveButton.setVisibility(View.VISIBLE);
                    backButton.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.GONE);
                    savePicButton.setVisibility(View.GONE);
                    signatureButton.setVisibility(View.GONE);
                    firstImageView.setVisibility(View.INVISIBLE);
                    secondImageView.setVisibility(View.INVISIBLE);
                    thirdImageView.setVisibility(View.INVISIBLE);
                    fourthImageView.setVisibility(View.INVISIBLE);
                } else if (outTimeString.equals("null")) {
                    arriveButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.VISIBLE);
                    savePicButton.setVisibility(View.VISIBLE);
                    signatureButton.setVisibility(View.VISIBLE);
                    firstImageView.setVisibility(View.VISIBLE);
                    secondImageView.setVisibility(View.VISIBLE);
                    thirdImageView.setVisibility(View.VISIBLE);
                    fourthImageView.setVisibility(View.VISIBLE);
                    if (!pathImgFirstString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFirstString).into(firstImageView);
                    }
                    if (!pathImgSecondString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgSecondString).into(secondImageView);
                    }
                    if (!pathImgThirdString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgThirdString).into(thirdImageView);
                    }
                    if (!pathImgFourthString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFourthString).into(fourthImageView);
                    }

                } else {
                    arriveButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.GONE);
                    savePicButton.setVisibility(View.VISIBLE);
                    signatureButton.setVisibility(View.GONE);
                    firstImageView.setVisibility(View.VISIBLE);
                    secondImageView.setVisibility(View.VISIBLE);
                    thirdImageView.setVisibility(View.VISIBLE);
                    fourthImageView.setVisibility(View.VISIBLE);
                    if (!pathImgFirstString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFirstString).into(firstImageView);
                    }
                    if (!pathImgSecondString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgSecondString).into(secondImageView);
                    }
                    if (!pathImgThirdString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgThirdString).into(thirdImageView);
                    }
                    if (!pathImgFourthString.equals("")) {
                        Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFourthString).into(fourthImageView);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
            }

        }
    }

    class InvoiceListAdaptor extends BaseAdapter {
        Context context;
        String[] invoiceStrings;
        InvoiceListViewHolder invoiceListViewHolder;

        public InvoiceListAdaptor(Context context, String[] invoiceStrings) {
            this.context = context;
            this.invoiceStrings = invoiceStrings;
            Log.d("Tag", String.valueOf(invoiceStrings.length));
        }

        @Override
        public int getCount() {
            return invoiceStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.invoice_listview, null);
                invoiceListViewHolder = new InvoiceListViewHolder(convertView);
                convertView.setTag(invoiceListViewHolder);
            } else {
                invoiceListViewHolder = (InvoiceListViewHolder) convertView.getTag();
            }
            Log.d("Tag", invoiceStrings[position]);

            invoiceListViewHolder.invoiceTextView.setText(invoiceStrings[position]);

            invoiceListViewHolder.invoiceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DetailJobActivity.this, ReturnActivity.class);
                    intent.putExtra("Date", dateString);
                    intent.putExtra("Position", tripNoString);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("SubJobNo", subJobNoString);
                    intent.putExtra("Place", storeString);
                    intent.putExtra("StoreId", storeIdString);
                    intent.putExtra("Invoice", invoiceStrings[position]);
                    startActivity(intent);
                }
            });

            invoiceListViewHolder.cameraImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "inv_first.png");
                    Intent cameraIntent5 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    invFirstUri = Uri.fromFile(originalFile1);
                    cameraIntent5.putExtra(MediaStore.EXTRA_OUTPUT, invFirstUri);
                    startActivityForResult(cameraIntent5, 5);

                }
            });

            return convertView;
        }

        class InvoiceListViewHolder {
            @BindView(R.id.txtILInvoice)
            TextView invoiceTextView;
            @BindView(R.id.imgILCamera)
            ImageView cameraImageView;

            InvoiceListViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class SynUpdateConfirmStatus extends AsyncTask<Void, Void, String> {
        String latString, longString, timeString;

        public SynUpdateConfirmStatus(String latString, String longString, String timeString) {
            this.latString = latString;
            this.longString = longString;
            this.timeString = timeString;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("user_name", loginStrings[5])
                        .add("subjob_no", subJobNoString)
                        .add("gps_lat", latString)
                        .add("gps_lon", longString)
                        .add("timeStamp", timeString)
                        .add("StoreId", storeIdString)
                        .build();
                Request request = builder.url(MyConstant.urlSaveConfirmedOfStore).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", s);
            if (s.equals("OK")) {
                Intent intent = new Intent(DetailJobActivity.this, ManageJobActivity.class);
                intent.putExtra("Date", dateString);
                intent.putExtra("Position", tripNoString);
                intent.putExtra("Login", loginStrings);
                intent.putExtra("SubJobNo", subJobNoString);
                startActivity(intent);

            } else if (s.equals("NOK")) {
                Toast.makeText(getBaseContext(), getResources().getText(R.string.save_incomp), Toast.LENGTH_LONG).show();
            }
        }
    }

    class SynUpdateStatusArrive extends AsyncTask<Void, Void, String> {
        String latString, longString, timeString;

        public SynUpdateStatusArrive(String latString, String longString, String timeString) {
            this.latString = latString;
            this.longString = longString;
            this.timeString = timeString;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("Tag", "Send ==> " + loginStrings[5] + " , " + storeString + " , " + subJobNoString + " , " + invoiceStrings[0] + " , " + latString + " , " + longString + " , " + timeString);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("user_name", loginStrings[5])
                        .add("dealerName", storeString)
                        .add("subjob_no", subJobNoString)
                        .add("invoiceNo", invoiceStrings[0])
                        .add("gps_lat", latString)
                        .add("gps_lon", longString)
                        .add("timeStamp", timeString)
                        .build();
                Request request = builder.url(MyConstant.urlSaveArrivedToStore).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    storeIdString = jsonObject.getString("StoreId");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                }

                arriveButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
                savePicButton.setVisibility(View.VISIBLE);
                signatureButton.setVisibility(View.VISIBLE);
                firstImageView.setVisibility(View.VISIBLE);
                secondImageView.setVisibility(View.VISIBLE);
                thirdImageView.setVisibility(View.VISIBLE);
                fourthImageView.setVisibility(View.VISIBLE);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.arrive_comp), Toast.LENGTH_LONG).show();
                    }
                });
            } else {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.err_arr), Toast.LENGTH_LONG).show();
                    }
                });
            }


        }
    }

    @OnClick({R.id.btnDJAArrive, R.id.btnDJASavePic, R.id.btnDJASignature, R.id.btnDJAConfirm, R.id.imgDJAOne, R.id.imgDJATwo, R.id.imgDJAThree, R.id.imgDJAFour})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnDJAArrive:
                GPSManager gpsManager = new GPSManager(DetailJobActivity.this);
                if (gpsManager.setLatLong(0)) {
                    SynUpdateStatusArrive synUpdateStatusArrive = new SynUpdateStatusArrive(gpsManager.getLatString(), gpsManager.getLongString(), gpsManager.getDateTime());
                    synUpdateStatusArrive.execute();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.err_gps), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnDJASavePic:
                Log.d("Tag", (pathImgFirstString != null) + " , " + !(pathImgFirstString == null));
                Log.d("Tag", (pathImgSecondString != null) + " , " + !(pathImgSecondString == null));
                Log.d("Tag", pathImgSecondString + pathImgFirstString);

                if (imgFirstBitmap != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgFirstBitmap, invoiceStrings[0], subJobNoString, "sto_first.png", storeIdString);
                    synUploadImage.execute();
                    imgFirstBitmap = null;
                }
                if (imgSecondBitmap != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgSecondBitmap, invoiceStrings[0], subJobNoString, "sto_second.png", storeIdString);
                    synUploadImage.execute();
                    imgSecondBitmap = null;
                }
                if (imgThirdBitmap != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgThirdBitmap, invoiceStrings[0], subJobNoString, "sto_third.png", storeIdString);
                    synUploadImage.execute();
                    imgThirdBitmap = null;
                }
                if (imgFourthBitmap != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgFourthBitmap, invoiceStrings[0], subJobNoString, "sto_fourth.png", storeIdString);
                    synUploadImage.execute();
                    imgFourthBitmap = null;

                }
                break;
            case R.id.btnDJASignature:
                Intent intent = new Intent(DetailJobActivity.this, SignatureActivity.class);
                intent.putExtra("Date", dateString);
                intent.putExtra("Position", tripNoString);
                intent.putExtra("Login", loginStrings);
                intent.putExtra("SubJobNo", subJobNoString);
                intent.putExtra("Place", storeString);
                intent.putExtra("StoreId", storeIdString);
                startActivity(intent);
                break;
            case R.id.btnDJAConfirm:
                GPSManager manager = new GPSManager(DetailJobActivity.this);
                if (manager.setLatLong(0)) {
                    SynUpdateConfirmStatus synUpdateConfirmStatus = new SynUpdateConfirmStatus(manager.getLatString(), manager.getLongString(), manager.getDateTime());
                    synUpdateConfirmStatus.execute();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.err_gps), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imgDJAOne:
                if (!imgFirstFlagABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_first.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    firstUri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, firstUri);
                    startActivityForResult(cameraIntent1, 1);

                }
                break;
            case R.id.imgDJATwo:
                if (!imgSecondFlagABoolean) {
                    File originalFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_second.png");
                    Intent cameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    secondUri = Uri.fromFile(originalFile2);
                    cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, secondUri);
                    startActivityForResult(cameraIntent2, 2);
                }
                break;
            case R.id.imgDJAThree:
                if (!imgThirdFlagABoolean) {
                    File originalFile3 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_third.png");
                    Intent cameraIntent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    thirdUri = Uri.fromFile(originalFile3);
                    cameraIntent3.putExtra(MediaStore.EXTRA_OUTPUT, thirdUri);
                    startActivityForResult(cameraIntent3, 3);
                }
                break;
            case R.id.imgDJAFour:
                if (!imgFourthFlagABoolean) {
                    File originalFile4 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_fourth.png");
                    Intent cameraIntent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fourthUri = Uri.fromFile(originalFile4);
                    cameraIntent4.putExtra(MediaStore.EXTRA_OUTPUT, fourthUri);
                    startActivityForResult(cameraIntent4, 4);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    pathImgFirstString = firstUri.getPath().toString();
                    try {
                        imgFirstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(firstUri));
                        if (imgFirstBitmap.getHeight() < imgFirstBitmap.getWidth()) {
                            imgFirstBitmap = rotateBitmap(imgFirstBitmap);
                        }
                        firstImageView.setImageBitmap(imgFirstBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    pathImgSecondString = secondUri.getPath().toString();
                    try {
                        imgSecondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(secondUri));
                        if (imgSecondBitmap.getHeight() < imgSecondBitmap.getWidth()) {
                            imgSecondBitmap = rotateBitmap(imgSecondBitmap);
                        }
                        secondImageView.setImageBitmap(imgSecondBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    pathImgThirdString = thirdUri.getPath().toString();
                    try {
                        imgThirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(thirdUri));
                        if (imgThirdBitmap.getHeight() < imgThirdBitmap.getWidth()) {
                            imgThirdBitmap = rotateBitmap(imgThirdBitmap);
                        }
                        thirdImageView.setImageBitmap(imgThirdBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 4:
                if (resultCode == RESULT_OK) {
                    pathImgFourthString = fourthUri.getPath().toString();
                    try {
                        imgFourthBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fourthUri));
                        if (imgFourthBitmap.getHeight() < imgFourthBitmap.getWidth()) {
                            imgFourthBitmap = rotateBitmap(imgFourthBitmap);
                        }
                        fourthImageView.setImageBitmap(imgFourthBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;
            case 5:
                if (resultCode == RESULT_OK) {
                    //pathImgInviceFirstString = invFirstUri.getPath().toString();
                    try {
                        imgInvoiceFirstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(invFirstUri));
                        Log.d("Tag", "Before Call  ==> " + imgInvoiceFirstBitmap + " , " + invoiceStrings[0].toString() + " , " + subJobNoString);


                        SynUploadImagePerInv synUploadImage = new SynUploadImagePerInv(DetailJobActivity.this, imgInvoiceFirstBitmap, invoiceStrings[0].toString(), subJobNoString, "inv_first.png");
                        synUploadImage.execute();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        private Context context;
        private Bitmap bitmap;
        private String invoiceNoString, subjobNoString, mFileNameString, storeIdString;
        private UploadImageUtils uploadImageUtils;


        public SynUploadImage(Context context, Bitmap bitmap, String invoiceNoString, String subjobNoString, String mFileNameString, String storeIdString) {
            this.context = context;
            this.bitmap = bitmap;
            this.invoiceNoString = invoiceNoString;
            this.subjobNoString = subjobNoString;
            this.mFileNameString = mFileNameString;
            this.storeIdString = storeIdString;
        }

        @Override
        protected String doInBackground(Void... params) {
            uploadImageUtils = new UploadImageUtils();
            final String result = uploadImageUtils.uploadFile(mFileNameString, urlUploadPicture, bitmap, storeIdString, "P", subjobNoString, invoiceNoString);
            if (result.equals("NOK")) {
                return "NOK";

            } else {
                try {
                    GPSManager gpsManager = new GPSManager(DetailJobActivity.this);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("subjob_no", subjobNoString)
                            .add("invoiceNo", invoiceNoString)
                            .add("File_Name", result)
                            .add("user_name", loginStrings[5])
                            .add("StoreId", storeIdString)
                            .add("timeStamp", gpsManager.getDateTime())
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.post(requestBody).url(urlSaveImagePerStore).build();
                    Response response = okHttpClient.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                    return null;
                }

            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "____ Save image / store _______________" + s);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_success), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_unsuccessful), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private class SynUploadImagePerInv extends AsyncTask<Void, Void, String> {
        private Context context;
        private Bitmap bitmap;
        private String invoiceNoString, subjobNoString, mFileNameString;
        private UploadImageUtils uploadImageUtils;


        public SynUploadImagePerInv(Context context, Bitmap bitmap, String invoiceNoString, String subjobNoString, String mFileNameString) {
            this.context = context;
            this.bitmap = bitmap;
            this.invoiceNoString = invoiceNoString;
            this.subjobNoString = subjobNoString;
            this.mFileNameString = mFileNameString;
        }

        @Override
        protected String doInBackground(Void... params) {

            uploadImageUtils = new UploadImageUtils();
            final String result = uploadImageUtils.uploadFile(mFileNameString, urlUploadPicture, bitmap, storeIdString, "I", subjobNoString, invoiceNoString);
            if (result == "NOK") {
                return "NOK";

            } else {
                try {
                    GPSManager gpsManager = new GPSManager(DetailJobActivity.this);

                    Log.d("Tag", "send ==> " + subjobNoString + " , " + invoiceNoString + " , " + result + " , " + loginStrings[0] + " , " + gpsManager.getDateTime());
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("subjob_no", subJobNoString)
                            .add("invoiceNo", invoiceNoString)
                            .add("File_Name", result)
                            .add("user_name", loginStrings[5])
                            .add("gps_timeStamp", gpsManager.getDateTime())
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.post(requestBody).url(urlSaveImagePerInvoice).build();
                    Response response = okHttpClient.newCall(request).execute();

                    return response.body().string();
                } catch (IOException e) {
                    Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                    return null;
                }

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "________Save image / invoice___________" + s);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_success), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_unsuccessful), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

}
