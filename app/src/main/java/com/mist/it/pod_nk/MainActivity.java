package com.mist.it.pod_nk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

import static com.mist.it.pod_nk.MyConstant.urlGetUser;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.imgMALogo)
    ImageView logoImageView;
    @BindView(R.id.edtMAUsername)
    EditText usernameEditText;
    @BindView(R.id.edtMAPassword)
    EditText passwordEditText;
    @BindView(R.id.btnMALogin)
    Button loginButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.information  :
                Intent intent = new Intent(MainActivity.this, VideoViewerActivity.class);
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        logoImageView.setImageResource(R.drawable.htslogo);


        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 101);

    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected class SynGetUser extends AsyncTask<Void, Void, String> {
        @BindView(R.id.imgCAAlert)
        ImageView alertImageView;
        @BindView(R.id.txtCAHeader)
        TextView headerTextView;
        @BindView(R.id.txtCADescript)
        TextView descriptTextView;
        private String usernameString, passwordString;

        SynGetUser(String usernameString, String passwordString) {
            this.usernameString = usernameString;
            this.passwordString = passwordString;

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("Tag", "Send ==> " + usernameString + " , " + passwordString);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("username", usernameString)
                        .add("password", passwordString)
                        .build();
                Request request = builder.post(requestBody).url(urlGetUser).build();
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
            if (!s.equals("null")) {
                Log.d("TAG", s);
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String truckIdString = jsonObject.getString("TruckID");
                    String truckRegString = jsonObject.getString("TruckReg");
                    String truckTypeIdString = jsonObject.getString("TruckTypeID");
                    String driverNameString = jsonObject.getString("DriverName");
                    String driverSurname = jsonObject.getString("DriverSurname");
                    String[] loginStrings = new String[]{truckIdString, driverNameString, driverSurname, truckRegString, truckTypeIdString, usernameString};

                    Intent intent = new Intent(MainActivity.this, JobListActivity.class);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("Date", "");
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = View.inflate(getBaseContext(), R.layout.custom_alert, null);

                ButterKnife.bind(this, view);

                alertImageView.setImageResource(R.drawable.caution);
                headerTextView.setText(getResources().getText(R.string.err_login_h));
                descriptTextView.setText(getResources().getText(R.string.err_login_d));

                builder.setView(view);


                builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Not do every thing
                    }
                });
                builder.show();
            }
        }
    }

    @OnClick(R.id.btnMALogin)
    public void onViewClicked() {
        SynGetUser synGetUser = new SynGetUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        synGetUser.execute();
    }
}
