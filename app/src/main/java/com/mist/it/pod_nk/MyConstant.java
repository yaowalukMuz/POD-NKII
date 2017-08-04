package com.mist.it.pod_nk;

import android.provider.BaseColumns;

/**
 * Created by Tunyaporn on 6/29/2017.
 */

public interface MyConstant extends BaseColumns {
    public static final String projectString = "DMS_NK";
    public static final String serverString = "http://service.eternity.co.th/";
    public static final String urlGetUser = serverString + projectString + "/app/centerservice/getuser.php";
    public static final String urlGetJobList = serverString + projectString + "/app/centerservice/getJobList.php";
    public static final String urlGetJobListDate = serverString + projectString + "/app/centerservice/getListJobDate.php";
    public static final String urlGetJob = serverString + projectString + "/app/centerservice/getJob.php";
    public static final String urlGetJobDetail = serverString + projectString + "/app/centerservice/getJobDetail.php";
    public static final String urlGetJobDetailProduct = serverString + projectString + "/app/centerservice/getJobDetailProduct.php";
    public static final String urlSaveArrivedToStore = serverString + projectString + "/app/centerservice/saveArrivedToStore.php";
    public static final String urlSaveConfirmedOfStore = serverString + projectString + "/app/centerservice/saveConfirmedOfStore.php";
    public static final String urlSaveImagePerInvoice = serverString + projectString + "/app/centerservice/saveImagePerInvoice.php";
    public static final String urlSaveImagePerStore = serverString + projectString + "/app/centerservice/saveImagePerStore.php";
    public static final String urlSaveQuantityReturnAll = serverString + projectString + "/app/centerservice/saveQuantityReturnAll.php";
    public static final String urlSaveQuantityReturnByItem = serverString + projectString + "/app/centerservice/saveQuantityReturnByItem.php";
    public static final String urlSaveSignature = serverString + projectString + "/app/centerservice/saveSignature.php";
    public static final String urlSaveStatusTrip = serverString + projectString + "/app/centerservice/saveStatusTrip.php";
    public static final String urlUploadPicture = serverString + projectString + "/app/centerservice/uploadPicture.php";
}
