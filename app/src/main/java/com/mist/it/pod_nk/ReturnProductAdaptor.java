package com.mist.it.pod_nk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tunyaporn on 7/31/2017.
 */

public class ReturnProductAdaptor extends BaseAdapter {
    //Explicit
    private Context context;
    private String[] modelStrings, detailStrings, amountStrings, returnQtyStrings, invoiceSeqStrings;
    ReturnListViewHolder productListViewHolder;
    private ArrayList<ReturnItem> returnItems;

    public ReturnProductAdaptor(Context context, ArrayList<ReturnItem> returnItems) {
        this.context = context;
        this.returnItems = returnItems;
    }

    public String[] getInvoiceSeqStrings() {
        return invoiceSeqStrings;
    }

    public void setInvoiceSeqStrings(String[] invoiceSeqStrings) {
        this.invoiceSeqStrings = invoiceSeqStrings;
    }

    @Override
    public int getCount() {
        return returnItems.size();
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
            // create view holder for remember data in list view
            convertView = LayoutInflater.from(context).inflate(R.layout.return_listview, null);
            productListViewHolder = new ReturnListViewHolder(convertView);
            convertView.setTag(productListViewHolder);
        } else {
            productListViewHolder = (ReturnListViewHolder) convertView.getTag();
        }


        String text = convertView.getResources().getString(R.string.Model) + " : " + returnItems.get(position).getModelString();
        productListViewHolder.modelTextView.setText(text);
        text = convertView.getResources().getString(R.string.Desc) + " : " + returnItems.get(position).getDescriptionString();
        productListViewHolder.detailTextView.setText(text);
        text = convertView.getResources().getString(R.string.Quantity) + " : " + returnItems.get(position).getAmountString();
        productListViewHolder.amountTextView.setText(text);
        text = convertView.getResources().getString(R.string.RetQuantity) + " : " + returnItems.get(position).getRetrunAmountString();
        productListViewHolder.returnAmtTextView.setText(text);


        return convertView;
    }


    class ReturnListViewHolder {
        @BindView(R.id.textView)
        TextView modelTextView;
        @BindView(R.id.textView2)
        TextView detailTextView;
        @BindView(R.id.textView3)
        TextView amountTextView;
        @BindView(R.id.textView4)
        TextView returnAmtTextView;

        ReturnListViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
