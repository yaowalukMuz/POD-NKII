package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.channels.GatheringByteChannel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tunyaporn on 7/20/2017.
 */

public class ManageJobAdaptor extends BaseAdapter {
    Context context;
    String dateString, tripNoString, subJobNoString;
    String[] storeStrings, timeStrings, loginStrings, outTimeStrings, placeStrings;
    String[][] jobNoStrings;
    String[][][] invoiceStrings, amountStrings;
    ManageJobViewHolder manageJobViewHolder;

    public ManageJobAdaptor(Context context, String dateString, String tripNoString, String subJobNoString, String[] storeStrings, String[] timeStrings, String[] loginStrings, String[][] jobNoStrings, String[][][] invoiceStrings, String[][][] amountStrings, String[] outTimeStrings, String [] placeStrings) {
        this.context = context;
        this.dateString = dateString;
        this.tripNoString = tripNoString;
        this.subJobNoString = subJobNoString;
        this.storeStrings = storeStrings;
        this.timeStrings = timeStrings;
        this.loginStrings = loginStrings;
        this.jobNoStrings = jobNoStrings;
        this.invoiceStrings = invoiceStrings;
        this.amountStrings = amountStrings;
        this.outTimeStrings = outTimeStrings;
        this.placeStrings = placeStrings;
    }

    @Override
    public int getCount() {
        return storeStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.manage_job_listview, null);
            manageJobViewHolder = new ManageJobViewHolder(convertView);
            convertView.setTag(manageJobViewHolder);
        } else {
            manageJobViewHolder = (ManageJobViewHolder) convertView.getTag();
        }

        manageJobViewHolder.storeTextView.setText(placeStrings[position]);
        manageJobViewHolder.timeTextView.setText(timeStrings[position]);

        JobNoAdaptor jobNoAdaptor = new JobNoAdaptor(context, jobNoStrings[position], invoiceStrings[position], amountStrings[position]);
        manageJobViewHolder.jobNoListView.setAdapter(jobNoAdaptor);
        if (!outTimeStrings[position].equals("null")) {
            manageJobViewHolder.itemLinearLayout.setForeground(context.getDrawable(R.drawable.layout_bg_3));

        }

        manageJobViewHolder.storeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!outTimeStrings[position].equals("null")) {
                    Toast.makeText(context, context.getResources().getText(R.string.finish_store), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, DetailJobActivity.class);
                    intent.putExtra("Date", dateString);
                    intent.putExtra("Position", tripNoString);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("SubJobNo", subJobNoString);
                    intent.putExtra("Place", storeStrings[position]);
                    context.startActivity(intent);
                }
            }
        });

        manageJobViewHolder.timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!outTimeStrings[position].equals("null")) {
                    Toast.makeText(context, context.getResources().getText(R.string.finish_store), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, DetailJobActivity.class);
                    intent.putExtra("Date", dateString);
                    intent.putExtra("Position", tripNoString);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("SubJobNo", subJobNoString);
                    intent.putExtra("Place", storeStrings[position]);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    static class ManageJobViewHolder {
        @BindView(R.id.txtMJLStore)
        TextView storeTextView;
        @BindView(R.id.txtMJLTime)
        TextView timeTextView;
        @BindView(R.id.lisMJLJobNo)
        ListView jobNoListView;
        @BindView(R.id.linMJLItem)
        LinearLayout itemLinearLayout;

        ManageJobViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

