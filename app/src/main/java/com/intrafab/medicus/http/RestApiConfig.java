package com.intrafab.medicus.http;

import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class RestApiConfig {

    public static final String BASE_HOST_NAME = Constants.RELEASE_MODE ? "medicus.caramba-shop.ru" : "medicus.caramba-shop.ru";
    public static final String MEDICUS_HOST_NAME = Constants.RELEASE_MODE ? "prod.inmedicus.ru" : "prod.inmedicus.ru";
    public static final String FILESTORAGE_HOST_NAME = Constants.RELEASE_MODE ? "medicus.caramba-shop.ru" : "medicus.caramba-shop.ru";
    public static final String VERSION_API = "v1.0";
    public static final String BASE_HOST_URL = "http://" + BASE_HOST_NAME;// + "/" + VERSION_API;
    public static final String MEDICUS_HOST_URL = "http://" + MEDICUS_HOST_NAME;
    public static final String FILESTORAGE_HOST_URL = "http://" + FILESTORAGE_HOST_NAME + ":1537";// + "/" + VERSION_API;

    public static HttpRestService getRestService() {
        return ServiceGenerator.createService(HttpRestService.class, RestApiConfig.BASE_HOST_URL, AppApplication.getToken());
    }

    public static HttpRestService getFilestorageRestService() {
        return FilestorageServiceGenerator.createService(HttpRestService.class, RestApiConfig.FILESTORAGE_HOST_URL, AppApplication.getToken());
    }

    public static HttpRestService getMedicusRestService() {
        return ServiceGenerator.createService(HttpRestService.class, RestApiConfig.MEDICUS_HOST_URL, AppApplication.getToken());
    }
}
