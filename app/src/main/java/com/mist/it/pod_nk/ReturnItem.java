package com.mist.it.pod_nk;

/**
 * Created by Tunyaporn on 7/31/2017.
 */

public class ReturnItem {

    private String invoiceSeqString,invoiceNoString, modelString,descriptionString,amountString,retrunAmountString,imgFileString;

    public ReturnItem() {
        invoiceSeqString = "";
        invoiceNoString = "";
        modelString = "";
        descriptionString = "";
        amountString = "";
        retrunAmountString = "";
        imgFileString = "";
    }

    public ReturnItem(String invoiceSeqString, String invoiceNoString, String modelString, String descriptionString, String amountString, String retrunAmountString, String imgFileString) {
        this.invoiceSeqString = invoiceSeqString;
        this.invoiceNoString = invoiceNoString;
        this.modelString = modelString;
        this.descriptionString = descriptionString;
        this.amountString = amountString;
        this.retrunAmountString = retrunAmountString;
        this.imgFileString = imgFileString;
    }

    public String getAmountString() {
        return amountString;
    }

    public void setAmountString(String amountString) {
        this.amountString = amountString;
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    public String getInvoiceSeqString() {
        return invoiceSeqString;
    }

    public void setInvoiceSeqString(String invoiceSeqString) {
        this.invoiceSeqString = invoiceSeqString;
    }

    public String getInvoiceNoString() {
        return invoiceNoString;
    }

    public void setInvoiceNoString(String invoiceNoString) {
        this.invoiceNoString = invoiceNoString;
    }

    public String getModelString() {
        return modelString;
    }

    public void setModelString(String modelString) {
        this.modelString = modelString;
    }

    public String  getRetrunAmountString() {
        return retrunAmountString;
    }

    public void setRetrunAmountString(String retrunAmountString) {
        this.retrunAmountString = retrunAmountString;
    }

    public String getImgFileString() {
        return imgFileString;
    }

    public void setImgFileString(String imgFileString) {
        this.imgFileString = imgFileString;
    }
}
