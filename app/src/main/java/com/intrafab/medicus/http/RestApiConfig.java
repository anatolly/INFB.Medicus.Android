package com.intrafab.medicus.http;

import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class RestApiConfig {

    public static final String BASE_HOST_NAME = Constants.RELEASE_MODE ? "medicus.caramba-shop.ru" : "medicus.caramba-shop.ru";
    public static final String VERSION_API = "v1.0";
    public static final String BASE_HOST_URL = "http://" + BASE_HOST_NAME;// + "/" + VERSION_API;
    public static final String BASE_HOST_URL2 = "http://" + BASE_HOST_NAME + ":1537";// + "/" + VERSION_API;

    public static final String URL_CSU = "/serviceuse/csu";
    public static final String URL_ISU = "/serviceuse/isu";
    public static final String URL_NODE = "/serviceuse/node";

    public static HttpRestService getRestService() {
        return ServiceGenerator.createService(HttpRestService.class, RestApiConfig.BASE_HOST_URL, AppApplication.getToken());
    }

    public static HttpRestService getRestService2() {
        return ServiceGenerator.createService(HttpRestService.class, RestApiConfig.BASE_HOST_URL2, AppApplication.getToken());
    }
}
