package com.intrafab.medicus.http;

import com.intrafab.medicus.data.WrapperLogin;
import com.intrafab.medicus.wrappers.RequestStateEntries;
import com.intrafab.medicus.wrappers.RequestStorageInfo;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

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

    // Порядок важен. Файл должен быть последним параметром
    @Multipart
    @POST("/serviceuse/storeFile")
    public void uploadFileToStorage(
            @Part("fileFormat") TypedString fileFormat,
            @Part("destination") TypedString destination,
            @Part("_data") TypedFile file);

    @POST("/serviceuse/user/login")
    @Headers({"Content-Type: application/json"})
    public Response login(@Body WrapperLogin data);
}
