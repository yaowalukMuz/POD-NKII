package com.mist.it.pod_nk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InJobNoAdaptor extends BaseAdapter {
    Context context;
    String[] invoiceStrings, amountStrings;
    InJobNoViewHolder inJobNoViewHolder;

    public InJobNoAdaptor(Context context, String[] invoiceStrings, String[] amountStrings) {
        this.context = context;
        this.invoiceStrings = invoiceStrings;
        this.amountStrings = amountStrings;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.in_job_no_listview, null);
            inJobNoViewHolder = new InJobNoViewHolder(convertView);
            convertView.setTag(inJobNoViewHolder);
        } else {
            inJobNoViewHolder = (InJobNoViewHolder) convertView.getTag();
        }

        inJobNoViewHolder.amountTextView.setText(amountStrings[position]);
        inJobNoViewHolder.bulletImageView.setImageResource(R.drawable.bullet);
        inJobNoViewHolder.invoiceTextView.setText(invoiceStrings[position]);

        return convertView;
    }

    static class InJobNoViewHolder {
        @BindView(R.id.imgIJNLBullet)
        ImageView bulletImageView;
        @BindView(R.id.txtIJNLInvoice)
        TextView invoiceTextView;
        @BindView(R.id.txtIJNLAmount)
        TextView amountTextView;

        InJobNoViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
