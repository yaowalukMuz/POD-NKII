package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static com.mist.it.pod_nk.MyConstant.urlGetJobListDate;

public class DateActivity extends AppCompatActivity {
    @BindView(R.id.lisDAJobDate)
    ListView tripDateListView;

    String[] loginStrings, deliveryDateStrings, sumjobStrings;
    String dateString;

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
        setContentView(R.layout.activity_date);
        ButterKnife.bind(this);

        loginStrings = getIntent().getStringArrayExtra("Login");
        dateString = getIntent().getStringExtra("Date");

        SyncGetDate syncGetDate = new SyncGetDate(this, loginStrings[0]);
        syncGetDate.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DateActivity.this, JobListActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("Date", dateString);
        Log.d("Tag", "Send ==> " + dateString + " " + Arrays.toString(loginStrings));
        startActivity(intent);
        finish();

    }

    @OnItemClick(R.id.lisDAJobDate)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DateActivity.this, JobListActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("Date", deliveryDateStrings[position]);
        Log.d("Tag", "Send ==> " + deliveryDateStrings[position] + " " + Arrays.toString(loginStrings));
        startActivity(intent);
        finish();
    }

    class SyncGetDate extends AsyncTask<Void, Void, String> {
        Context context;
        String truckIDString;


        public SyncGetDate(Context context, String truckIDString) {
            this.context = context;
            this.truckIDString = truckIDString;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("Tag", "Send ==> " + truckIDString);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("truck_id", truckIDString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlGetJobListDate).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.d("Tag", "Error Date Activity SyncGetDate do in back ==> " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "" + s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                deliveryDateStrings = new String[jsonArray.length()];
                sumjobStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    deliveryDateStrings[i] = jsonObject.getString("DeliveryDate");
                    sumjobStrings[i] = jsonObject.getString("SUMJOB");
                }

                TripDateAdaptor tripDateAdaptor = new TripDateAdaptor(context, deliveryDateStrings, sumjobStrings);
                tripDateListView.setAdapter(tripDateAdaptor);

            } catch (JSONException e) {
                Log.d("Tag", "Error Date Activity SyncGetDate on post JSONArray ==> " + e);

            }
        }
    }

    protected class TripDateAdaptor extends BaseAdapter {
        Context context;
        String[] dateStrings, jobStrings;
        ViewHolder viewHolder;

        public TripDateAdaptor(Context context, String[] dateStrings, String[] jobStrings) {
            this.context = context;
            this.dateStrings = dateStrings;
            this.jobStrings = jobStrings;
        }

        @Override
        public int getCount() {
            return dateStrings.length;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.date_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String date, job;

            date = getResources().getString(R.string.Date) + " : " + dateStrings[position];
            job = jobStrings[position] + " " + getResources().getString(R.string.Trip);
//            job = jobStrings[position];
            viewHolder.dateTextView.setText(date);
            viewHolder.sumjobTextView.setText(job);

            return convertView;
        }


        class ViewHolder {
            @BindView(R.id.txtDLVDate)
            TextView dateTextView;
            @BindView(R.id.txtDLVSumjob)
            TextView sumjobTextView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


}
