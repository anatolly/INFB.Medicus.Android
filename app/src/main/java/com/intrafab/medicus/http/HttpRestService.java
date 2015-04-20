package com.intrafab.medicus.http;

import com.intrafab.medicus.wrappers.RequestStorageInfo;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public interface HttpRestService {

    @GET("/storage.json")
    public RequestStorageInfo loadStorage();

    @GET("/storage_trip.json")
    public RequestStorageInfo loadStorageTrip();
}
