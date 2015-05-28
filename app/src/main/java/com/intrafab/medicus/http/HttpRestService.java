package com.intrafab.medicus.http;

import com.intrafab.medicus.wrappers.RequestStateEntries;
import com.intrafab.medicus.wrappers.RequestStorageInfo;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public interface HttpRestService {

    @GET("/storage.json")
    public RequestStorageInfo loadStorage();

    @GET("/storage_trip.json")
    public RequestStorageInfo loadStorageTrip();

    @GET("/state_entries.json")
    public RequestStateEntries loadStateEntries();

    @GET("/serviceuse/csu.json")
    public Response getAllOrders();

    @GET("/serviceuse/csu/{id}.json")
    public Response getOrder(@Path("id") String id);
}
