package com.intrafab.medicus.http;

import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.data.WrapperCallback;
import com.intrafab.medicus.data.WrapperLogin;
import com.intrafab.medicus.data.WrapperStatus;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public interface HttpRestService {
///increaseв the limit to see all files in storage
// (fix it as soon as possible)
    @GET("/api/v1.0/medicusdocument/")
    public List<StorageInfo> loadStorage(@Query("belongsToUser") String id);

//    @GET("/storage_trip.json")
//    public RequestStorageInfo loadStorageTrip();
//
//    @GET("/state_entries.json")
//    public RequestStateEntries loadStateEntries();

    @GET("/serviceuse/csu.json")
    public Response getAllOrders();

    @PUT("/serviceuse/activity/{id} ")
    @Headers({"Content-Type: application/json"})
    public Response changeStateActivity(@Path("id") String id, @Body WrapperStatus statusJson);

    @GET("/serviceuse/csu/{id}.json")
    public Response getOrder(@Path("id") String id);

    @GET("/serviceuse/isu/{id}.json")
    public Response getOrderItem(@Path("id") String id);

    @GET("/serviceuse/activity/{id}.json")
    public Response getActivityItem(@Path("id") String id);

    @GET("/serviceuse/node/{id}.json")
    public Response getNodeItem(@Path("id") String id);

    // Порядок важен. Файл должен быть последним параметром
    @Multipart
    @POST("/api/v1.0/medicusdocument/storeMedicusDocumentByFormData")
    public StorageInfo uploadFileToStorage(
            @Part("name") TypedString fileName,
            @Part("type") TypedString fileType,
            @Part("belongsToActiveTrip") TypedString belongsToActiveTrip,
            @Part("belongsToUser") TypedString belongsToUser,
            @Part("_data") TypedFile file);

    @POST("/serviceuse/user/login")
    @Headers({"Content-Type: application/json"})
    public Response login(@Body WrapperLogin data);

    @PUT("/serviceuse/csu/{id}")
    @Headers({"Content-Type: application/json"})
    public Response requestCallback(@Path("id") String id, @Body WrapperCallback callbackJson);
}
