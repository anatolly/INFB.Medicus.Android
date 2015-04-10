package com.intrafab.medicus.http;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public interface HttpRestService {

    @POST("/logout.json")
    public void logout(
            @Body String token);
}
