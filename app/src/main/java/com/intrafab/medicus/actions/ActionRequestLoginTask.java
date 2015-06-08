package com.intrafab.medicus.actions;

import android.os.Bundle;
import android.text.TextUtils;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.data.WrapperLogin;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.squareup.okhttp.RequestBody;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import org.json.JSONObject;

import java.io.IOException;

import okio.Buffer;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Artemiy Terekhov on 08.06.2015.
 */
public class ActionRequestLoginTask extends GroundyTask {
    public static final String TAG = ActionRequestLoginTask.class.getName();

    public static final String ARG_USER_NAME = "arg_user_name";
    public static final String ARG_USER_PASSWORD = "arg_user_password";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        Bundle inputBundle = getArgs();
        String userName = inputBundle.getString(ARG_USER_NAME);
        String userPassword = inputBundle.getString(ARG_USER_PASSWORD);

        WrapperLogin login = new WrapperLogin();
        login.setUsername(userName);
        login.setPassword(userPassword);

        try {
            HttpRestService service = RestApiConfig.getRestService();
            Response result = service.login(login);

            if (result != null) {
                TypedInput body = result.getBody();

                if (body != null) {
                    String resultString = new String(((TypedByteArray) body).getBytes());
                    Logger.e(TAG, "ActionRequestLoginTask result: " + resultString);

                    JSONObject bodyJson = new JSONObject(resultString);
                    if (bodyJson != null) {
                        String sessid = null;
                        if (bodyJson.has("sessid")) {
                            sessid = bodyJson.optString("sessid");
                        }

                        String sessionName = null;
                        if (bodyJson.has("session_name")) {
                            sessionName = bodyJson.optString("session_name");
                        }

                        String token = null;
                        if (bodyJson.has("token")) {
                            token = bodyJson.optString("token");
                        }

                        Account userAccount = null;
                        if (bodyJson.has("user")) {
                            userAccount = new Account(bodyJson.optJSONObject("user"));
                        }

                        if (userAccount != null && !TextUtils.isEmpty(token)) {
                            return succeeded()
                                    .add(Constants.Extras.PARAM_TOKEN, token)
                                    .add(Constants.Extras.PARAM_USER_DATA, userAccount);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
    }

    private static String bodyAsString(RequestBody body) {
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(body.contentType().charset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
