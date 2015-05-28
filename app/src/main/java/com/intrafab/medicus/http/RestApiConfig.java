package com.intrafab.medicus.http;

import com.intrafab.medicus.Constants;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class RestApiConfig {

    public static final String BASE_HOST_NAME = Constants.RELEASE_MODE ? "medicus.caramba-shop.ru" : "medicus.caramba-shop.ru";
    public static final String VERSION_API = "v1.0";
    public static final String BASE_HOST_URL = "http://" + BASE_HOST_NAME;// + "/" + VERSION_API;

    public static HttpRestService getRestService(String token) {
        return ServiceGenerator.createService(HttpRestService.class, RestApiConfig.BASE_HOST_URL, token);
    }
}
