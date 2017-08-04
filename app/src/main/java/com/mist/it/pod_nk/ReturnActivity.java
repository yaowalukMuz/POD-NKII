package com.mist.it.pod_nk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Criteria;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.mist.it.pod_nk.MyConstant.projectString;
import static com.mist.it.pod_nk.MyConstant.serverString;
import static com.mist.it.pod_nk.MyConstant.urlGetJobDetailProduct;
import static com.mist.it.pod_nk.MyConstant.urlSaveImagePerInvoice;
import static com.mist.it.pod_nk.MyConstant.urlSaveQuantityReturnAll;
import static com.mist.it.pod_nk.MyConstant.urlSaveQuantityReturnByItem;
import static com.mist.it.pod_nk.MyConstant.urlUploadPicture;

public class ReturnActivity extends AppCompatActivity {

    @BindView(R.id.txtRAInvoiceNo)
    TextView invoiceNoTextView;
    @BindView(R.id.imgRAOne)
    ImageView firstImageView;
    @BindView(R.id.imgRATwo)
    ImageView secondImageView;
    @BindView(R.id.btnRASave)
    Button saveButton;
    @BindView(R.id.btnRAReturnAll)
    Button returnAllButton;
    @BindView(R.id.btnRAConfirm)
    Button confirmButton;
    @BindView(R.id.lisRAItemList)
    ListView itemListView;

    private String invoiceNoString, subJobString, placeString, tripNoString, storeIdString, dateString;
    private String[] invoiceNoStrings, imgFileNameStrings, subJobStrings, loginStrings;
    private String[][] modelStrings, amountStrings, detailStrings, returnAmountStrings, invoiceNoSeqStrings, imgFileStrings;
    private ArrayList<ReturnItem> returnItems = new ArrayList<ReturnItem>();

    private Uri firstUri, secondUri;
    private boolean firstImgFlagABoolean, secondImgFlagABoolean, saveImgABoolean;
    private String pathFirstImgString, pathSecondImgString, imgFirstPathString, imgSecondPathString;
    private Criteria criteria;
    private Bitmap firstImgBitmap = null;
    private Bitmap secondImgBitmap = null;

    DialogViewHolder dialogViewHolder;
    AlertMessageViewHolder alertMessageViewHolder;
    ConfirmReturnAllViewHolder confirmReturnAllViewHolder;

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
        setContentView(R.layout.activity_return);

        //1. Bind Widget
        ButterKnife.bind(this);

        invoiceNoString = getIntent().getStringExtra("Invoice");
        loginStrings = getIntent().getStringArrayExtra("Login");
        subJobString = getIntent().getStringExtra("SubJobNo");
        dateString = getIntent().getStringExtra("Date");
        tripNoString = getIntent().getStringExtra("Position");
        placeString = getIntent().getStringExtra("Place");
        storeIdString = getIntent().getStringExtra("StoreId");

        //Set flag img
        firstImgFlagABoolean = false;
        secondImgFlagABoolean = false;
        saveImgABoolean = false;


        // 2.create class for synDataAdaptor to listview
        SynJobDtlProduct synJobDtlProduct = new SynJobDtlProduct(this, subJobString, invoiceNoString, returnItems);
        synJobDtlProduct.execute(urlGetJobDetailProduct);


    }// main Method


    //Set On Click Listener
    @OnClick({R.id.btnRASave, R.id.btnRAReturnAll, R.id.btnRAConfirm, R.id.imgRAOne, R.id.imgRATwo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRASave:
                if (pathFirstImgString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(ReturnActivity.this, firstImgBitmap, invoiceNoStrings[0].toString(), subJobString, "inv_first.png");
                    synUploadImage.execute();
                    if (saveImgABoolean) {

                    } else {
                        firstImgFlagABoolean = true;
                        pathFirstImgString = null;
                        saveImgABoolean = false;
                    }
                }
                if (pathSecondImgString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(ReturnActivity.this, secondImgBitmap, invoiceNoStrings[0].toString(), subJobString, "inv_second.png");
                    synUploadImage.execute();
                    if (saveImgABoolean) {

                    } else {
                        saveImgABoolean = false;
                        pathSecondImgString = null;
                        firstImgFlagABoolean = true;
                    }
                }


                break;
            case R.id.btnRAReturnAll:


                AlertDialog.Builder builder = new AlertDialog.Builder(ReturnActivity.this,R.style.ReturnAlertDialogTheme);
                View view1 = View.inflate(getBaseContext(), R.layout.custom_alert, null);

                confirmReturnAllViewHolder = new ConfirmReturnAllViewHolder(view1);

                confirmReturnAllViewHolder.imgRtnAImageView.setImageResource(R.drawable.caution);
                confirmReturnAllViewHolder.headerRtnTextView.setText(getResources().getText(R.string.return_dlg_header));
                confirmReturnAllViewHolder.descriptRtnTextView.setText(getResources().getText(R.string.return_dlg_desc));
                confirmReturnAllViewHolder.headerRtnTextView.setTextColor(Color.parseColor("#f5f5f5"));
                confirmReturnAllViewHolder.descriptRtnTextView.setTextColor(Color.parseColor("#f5f5f5"));
                builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SyncReturnAll syncReturnAll = new SyncReturnAll(ReturnActivity.this, invoiceNoStrings[0]);
                        syncReturnAll.execute(urlSaveQuantityReturnAll);
                        // if() {
                        for (int i = 0; i < returnItems.size(); i++) {
                            returnItems.get(i).setRetrunAmountString(returnItems.get(i).getAmountString());
                        }
                        ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(ReturnActivity.this, returnItems);

                        itemListView.setAdapter(returnProductAdaptor);
                        // }
                    }
                });
                builder.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setView(view1);
                builder.show();


//
                break;
            case R.id.btnRAConfirm:
                finish();
                break;

            case R.id.imgRAOne:
                if (!firstImgFlagABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "inv_first.png");

                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    firstUri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, firstUri);
                    startActivityForResult(cameraIntent1, 1);
                }
                break;

            case R.id.imgRATwo:
                if (!secondImgFlagABoolean) {
                    File orignalFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "inv_second.png");

                    Intent cameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    secondUri = Uri.fromFile(orignalFile2);
                    cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, secondUri);
                    startActivityForResult(cameraIntent2, 2);
                }
                break;
        }
    }

    @OnItemClick(R.id.lisRAItemList)
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        // Log.d("Tag", "onItemSelected: " + position);

        //event onclick listview
        final View view1 = View.inflate(getBaseContext(), R.layout.dialog_return, null);
        //final View view2 = View.inflate(getBaseContext(), R.layout.custom_alert, null);

        dialogViewHolder = new DialogViewHolder(view1);

        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnActivity.this,R.style.ReturnListAlertDialogTheme);
        dialogViewHolder.titleTextview.setText(getResources().getText(R.string.ProductReturn) + ": " + returnItems.get(position).getModelString());
        dialogViewHolder.titleTextview.setTextColor(Color.parseColor("#f5f5f5"));
        dialogViewHolder.amtTxtTextview.setText(getResources().getText(R.string.ProductAmount) + ": ");
        dialogViewHolder.amtTxtTextview.setTextColor(Color.parseColor("#f5f5f5"));
        dialogViewHolder.amtRtnEditText.setText(returnItems.get(position).getRetrunAmountString());
        builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Integer.parseInt(dialogViewHolder.amtRtnEditText.getText().toString()) <= Integer.parseInt(returnItems.get(position).getAmountString())) {
                    SyncUpdateTurn syncUpdateTurn = new SyncUpdateTurn(ReturnActivity.this, returnItems.get(position).getInvoiceNoString(),
                            returnItems.get(position).getInvoiceSeqString(), returnItems.get(position).getModelString(),
                            dialogViewHolder.amtRtnEditText.getText().toString());
                    syncUpdateTurn.execute(urlSaveQuantityReturnByItem);
                    returnItems.get(position).setRetrunAmountString(dialogViewHolder.amtRtnEditText.getText().toString());
                    ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(ReturnActivity.this, returnItems);

                    itemListView.setAdapter(returnProductAdaptor);
                } else {
                    Toast toast = Toast.makeText(ReturnActivity.this, "over amount", Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });

        builder.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view1);
        builder.show();


    }


    //inner class for syn data from adaptor
    protected class SynJobDtlProduct extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;
        private String subjob_no, invoiceNo;
        private ArrayList<ReturnItem> returnItmes;

        public SynJobDtlProduct(Context context, String subjob_no, String invoiceNo, ArrayList<ReturnItem> returnItmes) {
            this.context = context;
            this.subjob_no = subjob_no;
            this.invoiceNo = invoiceNo;
            this.returnItmes = returnItmes;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d("TAG", "Send ==> " + subjob_no + " , " + invoiceNo);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("subjob_no", subjob_no)
                        .add("invoiceNo", invoiceNo).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlGetJobDetailProduct).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                return "NOK";
            }


        }//end doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "onPostExecute: " + s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                invoiceNoStrings = new String[jsonArray.length()];

                subJobStrings = new String[jsonArray.length()];
                imgFileNameStrings = new String[jsonArray.length()];
                modelStrings = new String[jsonArray.length()][];
                amountStrings = new String[jsonArray.length()][];
                detailStrings = new String[jsonArray.length()][];
                returnAmountStrings = new String[jsonArray.length()][];
                invoiceNoSeqStrings = new String[jsonArray.length()][];
                imgFileStrings = new String[jsonArray.length()][];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    invoiceNoStrings[i] = jsonObject1.getString("Invoice");
                    subJobStrings[i] = jsonObject1.getString("SubJobNo");

                    JSONArray productArray = jsonObject1.getJSONArray("Product");

                    modelStrings[i] = new String[productArray.length()];
                    amountStrings[i] = new String[productArray.length()];
                    detailStrings[i] = new String[productArray.length()];
                    returnAmountStrings[i] = new String[productArray.length()];
                    invoiceNoSeqStrings[i] = new String[productArray.length()];


                    for (int j = 0; j < productArray.length(); j++) {
                        JSONObject jsonObject2 = productArray.getJSONObject(j);
                        modelStrings[i][j] = jsonObject2.getString("Product");
                        amountStrings[i][j] = jsonObject2.getString("Quantity");
                        detailStrings[i][j] = jsonObject2.getString("Product1");
                        returnAmountStrings[i][j] = jsonObject2.getString("QTYRT");
                        invoiceNoSeqStrings[i][j] = jsonObject2.getString("InvoiceSeqn");

                        ReturnItem returnItem = new ReturnItem();
                        returnItem.setModelString(jsonObject2.getString("Product"));
                        returnItem.setDescriptionString(jsonObject2.getString("Product1"));
                        returnItem.setRetrunAmountString(jsonObject2.getString("QTYRT"));
                        returnItem.setAmountString(jsonObject2.getString("Quantity"));
                        returnItem.setInvoiceSeqString(jsonObject2.getString("InvoiceSeqn"));
                        returnItem.setInvoiceNoString(invoiceNoStrings[0]);
                        returnItmes.add(returnItem);
                    }

                    JSONArray invoiceImgArray = jsonObject1.getJSONArray("InvoiceImg");
                    imgFileStrings[i] = new String[invoiceImgArray.length()];


                    for (int k = 0; k < invoiceImgArray.length(); k++) {
                        JSONObject jsonObject3 = invoiceImgArray.getJSONObject(k);
                        if (k == 0) {

                            imgFirstPathString = jsonObject3.getString("ImgPath");
                        } else if (k == 1) {

                            imgSecondPathString = jsonObject3.getString("ImgPath");
                        }
                    }

                    Log.d("Tag", "imgFirstPathString: " + imgFirstPathString);
                }

                //set adapter Listview
                invoiceNoTextView.setText(getResources().getString(R.string.InvoiceNo) + " : " + invoiceNoStrings[0]);
                ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(context, returnItmes);

                itemListView.setAdapter(returnProductAdaptor);
                // set image
                Log.d("Tag", "Image Path :::  " + serverString + projectString + "/app/CenterService/" + imgFirstPathString);

                if (!imgFirstPathString.equals("null")) {
                    Glide.with(ReturnActivity.this).load(serverString + projectString + "/app/CenterService/" + imgFirstPathString).into(firstImageView);
                }

                if (!imgSecondPathString.equals("null")) {
                    Glide.with(ReturnActivity.this).load(serverString + projectString + "/app/CenterService/" + imgSecondPathString).into(secondImageView);
                }


            } catch (Exception e) {
                Log.d("Tag", "onPostExecute: " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
            }

        }


    }//end class synJobDetailProduct

    private class SyncUpdateTurn extends AsyncTask<String, Void, String> {
        private Context context;
        private String invoiceNoString, invoiceSeqString, modelString, amountReturnString;

        public SyncUpdateTurn(Context context, String invoiceNoString, String invoiceSeqString, String modelString, String amountReturnString) {
            this.context = context;
            this.invoiceNoString = invoiceNoString;
            this.invoiceSeqString = invoiceSeqString;
            this.modelString = modelString;
            this.amountReturnString = amountReturnString;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Tag", "onPostExecute:->return save return product:::::  " + s);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                GPSManager gpsManager = new GPSManager(ReturnActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("user_name", loginStrings[5])
                        .add("subjob_no", subJobString)
                        .add("invoiceNo", invoiceNoString)
                        .add("invoiceSeq", invoiceSeqString)
                        .add("model", modelString)
                        .add("gps_timeStamp", gpsManager.getDateTime())
                        .add("Quantity", amountReturnString).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(urlSaveQuantityReturnByItem).build();
                Response response = okHttpClient.newCall(request).execute();


                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    private class SyncReturnAll extends AsyncTask<String, Void, String> {

        private Context context;
        private String invoiceNoString;

        public SyncReturnAll(Context context, String invoiceNoString) {
            this.context = context;
            this.invoiceNoString = invoiceNoString;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                GPSManager gpsManager = new GPSManager(ReturnActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("truck_id", loginStrings[0])
                        .add("subjob_no", subJobString)
                        .add("user_name", loginStrings[5])
                        .add("gps_timeStamp", gpsManager.getDateTime())
                        .add("invoiceNo", invoiceNoString).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(urlSaveQuantityReturnAll).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "onPostExecute:->return save return all product:::::  " + s);

        }
    }

    //////// onResult of image /////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: // From take  photo
                if (resultCode == RESULT_OK) {
                    pathFirstImgString = firstUri.getPath().toString();

                    try {
                        firstImgBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(firstUri));
                        if (firstImgBitmap.getHeight() > firstImgBitmap.getWidth()) {
                            firstImgBitmap = rotateBitmap(firstImgBitmap);
                        }
                        firstImageView.setImageBitmap(firstImgBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    pathSecondImgString = secondUri.getPath().toString();

                    try {
                        secondImgBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(secondUri));
                        if (secondImgBitmap.getHeight() > secondImgBitmap.getWidth()) {
                            secondImgBitmap = rotateBitmap(secondImgBitmap);
                        }
                        secondImageView.setImageBitmap(secondImgBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;

        }
    }

    static class DialogViewHolder {
        //        @Nullable
        @BindView(R.id.editText)
        EditText amtRtnEditText;
        @BindView(R.id.textView5)
        TextView amtTxtTextview;
        @BindView(R.id.textView6)
        TextView titleTextview;


        DialogViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class AlertMessageViewHolder {
        @BindView(R.id.imgCAAlert)
        ImageView imgAlertImageView;
        @BindView(R.id.txtCAHeader)
        TextView headerTextView;
        @BindView(R.id.txtCADescript)
        TextView descriptTextView;

        AlertMessageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ConfirmReturnAllViewHolder {
        @BindView(R.id.imgCAAlert)
        ImageView imgRtnAImageView;
        @BindView(R.id.txtCAHeader)
        TextView headerRtnTextView;
        @BindView(R.id.txtCADescript)
        TextView descriptRtnTextView;

        ConfirmReturnAllViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        private Context context;
        private Bitmap bitmap;
        private String invoiceNoString, subjobNoString, mFileNameString;
        private UploadImageUtils uploadImageUtils;

        ProgressDialog progressDialog;
        Runnable progressRunnable;
        Handler pdCancller;

        public SynUploadImage(Context context, Bitmap bitmap, String invoiceNoString, String subjobNoString, String mFileNameString) {
            this.context = context;
            this.bitmap = bitmap;
            this.invoiceNoString = invoiceNoString;
            this.subjobNoString = subjobNoString;
            this.mFileNameString = mFileNameString;
        }


//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage(getResources().getString(R.string.loading));
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//            progressRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.cancel();
//                }
//            };
//        }


        @Override
        protected String doInBackground(Void... params) {

            uploadImageUtils = new UploadImageUtils();
            final String result = uploadImageUtils.uploadFile(mFileNameString, urlUploadPicture, bitmap, "0", "I",subjobNoString, invoiceNoString);
            if (result == "NOK") {
                return "NOK";

            } else {
                try {
                    GPSManager gpsManager = new GPSManager(ReturnActivity.this);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("subjob_no", subJobString)
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
            Log.d("Tag", "___________________" + s);
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