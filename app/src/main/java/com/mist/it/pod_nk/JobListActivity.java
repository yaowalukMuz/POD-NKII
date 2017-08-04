package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.mist.it.pod_nk.MyConstant.urlGetJobList;

public class JobListActivity extends AppCompatActivity {

    @BindView(R.id.txtJLANameSurN)
    TextView fullNameTextView;
    @BindView(R.id.txtJLATruckLicense)
    TextView truckLicenseTextView;
    @BindView(R.id.btnJLADate)
    Button dateButton;
    @BindView(R.id.lisJLATrip)
    ListView tripListView;
    String[] loginStrings, subJobNoStrings, locationStrings, deliveryTypeStrings, transportStrings, truckStrings, driverNameStrings, driverSurnameStrings;
    String[] clerkStrings, truckTypeStrings, deliveryTripNoStrings, numberStrings;
    String[][] arriveTimeStrings, detailListStrings, invoiceStrings, jobNoStrings, placeStrings;
    String dateString, driverNameString, truckString;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_list);
        ButterKnife.bind(this);

        loginStrings = getIntent().getStringArrayExtra("Login");
        dateString = getIntent().getStringExtra("Date");

        if (driverNameStrings == null) {
            String name = loginStrings[1] + " " + loginStrings[2];
            fullNameTextView.setText(name);
        }
        truckLicenseTextView.setText(loginStrings[3]);

        Log.d("Tag", "Bool 1 ==> " + (dateString.equals("")) + " Bool 2 ==> " + (dateString == "") + " Date ==> " + dateString);
        if (dateString.equals("")) {
            SynGetJobList synGetJobList = new SynGetJobList(this, loginStrings[0]);
            synGetJobList.execute();
        } else {
            SynGetJobList synGetJobList = new SynGetJobList(this, loginStrings[0], dateString);
            synGetJobList.execute();
        }

    }

    @Override
    public void onBackPressed() {

    }

    protected class SynGetJobList extends AsyncTask<Void, Void, String> {
        private Context context;
        private String truckIDString;
        private String deliveryDateString = "";

        public SynGetJobList(Context context, String truckIDString, String deliveryDateString) {
            this.context = context;
            this.truckIDString = truckIDString;
            this.deliveryDateString = deliveryDateString;
        }

        public SynGetJobList(Context context, String truckIDString) {
            this.context = context;
            this.truckIDString = truckIDString;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("truck_id", truckIDString)
                        .add("deliveryDate", deliveryDateString)
                        .build();
                Request request = builder.url(urlGetJobList).post(requestBody).build();
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
            Log.d("Tag", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                deliveryDateString = jsonObject.getString("DeliveryDate");
                driverNameString = jsonObject.getString("DriverName");
                truckString = jsonObject.getString("Truck");

                JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                subJobNoStrings = new String[dataJsonArray.length()];
                locationStrings = new String[dataJsonArray.length()];
                deliveryTypeStrings = new String[dataJsonArray.length()];
                transportStrings = new String[dataJsonArray.length()];
                driverNameStrings = new String[dataJsonArray.length()];
                driverSurnameStrings = new String[dataJsonArray.length()];
                clerkStrings = new String[dataJsonArray.length()];
                truckStrings = new String[dataJsonArray.length()];
                truckTypeStrings = new String[dataJsonArray.length()];
                deliveryTripNoStrings = new String[dataJsonArray.length()];
                numberStrings = new String[dataJsonArray.length()];
                arriveTimeStrings = new String[dataJsonArray.length()][];
                detailListStrings = new String[dataJsonArray.length()][];
                placeStrings = new String[dataJsonArray.length()][];
                invoiceStrings = new String[dataJsonArray.length()][];
                jobNoStrings = new String[dataJsonArray.length()][];

                for (int i = 0; i < dataJsonArray.length(); i++) {
                    JSONObject jsonObject1 = dataJsonArray.getJSONObject(i);
                    subJobNoStrings[i] = jsonObject1.getString("SubJobNo");
                    locationStrings[i] = jsonObject1.getString("Location");
                    deliveryTypeStrings[i] = jsonObject1.getString("DeliveryType");
                    transportStrings[i] = jsonObject1.getString("Transport");
                    truckStrings[i] = jsonObject1.getString("Truck");
                    driverNameStrings[i] = jsonObject1.getString("DriverName");
                    driverSurnameStrings[i] = jsonObject1.getString("DriverSurname");
                    clerkStrings[i] = jsonObject1.getString("Clerk");
                    truckTypeStrings[i] = jsonObject1.getString("TruckType");
                    deliveryTripNoStrings[i] = jsonObject1.getString("DeliveryTripNo");
                    numberStrings[i] = String.valueOf(i + 1);

                    JSONArray deliveryPlaceJsonArray = jsonObject1.getJSONArray("DeliveryPlace");
                    arriveTimeStrings[i] = new String[deliveryPlaceJsonArray.length()];
                    detailListStrings[i] = new String[deliveryPlaceJsonArray.length()];
                    placeStrings[i] = new String[deliveryPlaceJsonArray.length()];
                    invoiceStrings[i] = new String[deliveryPlaceJsonArray.length()];
                    jobNoStrings[i] = new String[deliveryPlaceJsonArray.length()];


                    for (int j = 0; j < deliveryPlaceJsonArray.length(); j++) {

                        JSONObject jsonObject2 = deliveryPlaceJsonArray.getJSONObject(j);
                        Log.d("Tag", "i ==> " + String.valueOf(i) + " j ==> " + String.valueOf(j) + " invoice ==> " + jsonObject2.getString("Invoice"));
                        arriveTimeStrings[i][j] = jsonObject2.getString("ArrivalTime");
                        placeStrings[i][j] = jsonObject2.getString("DetailList");
                        detailListStrings[i][j] = jsonObject2.getString("DetailDesc");
                        invoiceStrings[i][j] = jsonObject2.getString("Invoice");
                        jobNoStrings[i][j] = jsonObject2.getString("JobNo");
                    }
                }
                Log.d("Tag", "Truck ==> " + loginStrings[3]);
                fullNameTextView.setText(driverNameString);
                dateButton.setText(deliveryDateString);

                JobListAdaptor jobListAdaptor = new JobListAdaptor(numberStrings, detailListStrings, arriveTimeStrings, subJobNoStrings, context);
                tripListView.setAdapter(jobListAdaptor);
                

                for (int i = 0; i < subJobNoStrings.length; i++) {
                    Log.d("Tag", Arrays.toString(invoiceStrings[i]));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected class JobListAdaptor extends BaseAdapter {
        String[] numberStrings, subJobNoStrings;
        String[][] detailListStrings, arriveTimeStrings;
        JobListViewHolder jobListViewHolder;
        Context context;

        public JobListAdaptor(String[] numberStrings, String[][] detailListStrings, String[][] arriveTimeStrings, String[] subJobNoStrings, Context context) {
            this.numberStrings = numberStrings;
            this.detailListStrings = detailListStrings;
            this.arriveTimeStrings = arriveTimeStrings;
            this.subJobNoStrings = subJobNoStrings;
            this.context = context;
        }

        @Override
        public int getCount() {
            return numberStrings.length;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.job_list_listview, null);
                jobListViewHolder = new JobListViewHolder(convertView);
                convertView.setTag(jobListViewHolder);
            } else {
                jobListViewHolder = (JobListViewHolder) convertView.getTag();
            }
            String s = getResources().getText(R.string.Trip) + " " + numberStrings[position];
            jobListViewHolder.roundTextView.setText(s);

            final InJobListAdaptor inJobListAdaptor = new InJobListAdaptor(detailListStrings[position], arriveTimeStrings[position]);
            jobListViewHolder.inJobListView.setAdapter(inJobListAdaptor);
            jobListViewHolder.roundTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(JobListActivity.this, ManageJobActivity.class);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("Date", dateString);
                    intent.putExtra("Position", "" + (position + 1));
                    intent.putExtra("SubJobNo", subJobNoStrings[position]);
                    startActivity(intent);
                    Log.d("Tag", "Position ==> " + position);

                }
            });

            return convertView;
        }

        protected class JobListViewHolder {
            @BindView(R.id.txtJLLRound)
            TextView roundTextView;
            @BindView(R.id.linJLLInJobListView)
            ListView inJobListView;

            JobListViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    protected class InJobListAdaptor extends BaseAdapter {
        String[] placeStrings, timeStrings;
        InJobListViewHolder inJobListViewHolder;

        public InJobListAdaptor(String[] placeStrings, String[] timeStrings) {
            this.placeStrings = placeStrings;
            this.timeStrings = timeStrings;
        }

        @Override
        public int getCount() {
            return placeStrings.length;
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
                convertView = View.inflate(getBaseContext(), R.layout.in_joblist_listview, null);
                inJobListViewHolder = new InJobListViewHolder(convertView);
                convertView.setTag(inJobListViewHolder);
            } else {
                inJobListViewHolder = (InJobListViewHolder) convertView.getTag();
            }

            inJobListViewHolder.placeTextView.setText(placeStrings[position]);
            inJobListViewHolder.timeTextView.setText(timeStrings[position]);

            return convertView;
        }


        protected class InJobListViewHolder {
            @BindView(R.id.txtIJLLPlace)
            TextView placeTextView;
            @BindView(R.id.txtIJLLTime)
            TextView timeTextView;

            InJobListViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @OnClick(R.id.btnJLADate)
    public void onViewClicked() {
        Intent intent = new Intent(JobListActivity.this, DateActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("Date", dateString);
        startActivity(intent);

    }
}
