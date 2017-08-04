package com.mist.it.pod_nk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JobNoAdaptor extends BaseAdapter {
    Context context;
    String[] jobNoStrings;
    String[][] invoiceStrings, amountStrings;
    JobNoViewHolder jobNoViewHolder;

    public JobNoAdaptor(Context context, String[] jobNoStrings, String[][] invoiceStrings, String[][] amountStrings) {
        this.context = context;
        this.jobNoStrings = jobNoStrings;
        this.invoiceStrings = invoiceStrings;
        this.amountStrings = amountStrings;
    }

    @Override
    public int getCount() {
        return jobNoStrings.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.job_no_listview, null);
            jobNoViewHolder = new JobNoViewHolder(convertView);
            convertView.setTag(jobNoViewHolder);
        } else {
            jobNoViewHolder = (JobNoViewHolder) convertView.getTag();
        }

        jobNoViewHolder.jobNoTextView.setText(jobNoStrings[position]);

        InJobNoAdaptor inJobNoAdaptor = new InJobNoAdaptor(context, invoiceStrings[position], amountStrings[position]);
        jobNoViewHolder.invoiceListView.setAdapter(inJobNoAdaptor);

        return convertView;
    }

    static class JobNoViewHolder {
        @BindView(R.id.txtJNLJobno)
        TextView jobNoTextView;
        @BindView(R.id.lisJNLInvoice)
        ListView invoiceListView;

        JobNoViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

