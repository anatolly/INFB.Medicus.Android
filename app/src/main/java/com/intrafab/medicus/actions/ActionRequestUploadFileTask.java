package com.intrafab.medicus.actions;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.io.File;

import retrofit.mime.TypedFile;

/**
 * Created by Artemiy Terekhov on 04.06.2015.
 */
public class ActionRequestUploadFileTask extends GroundyTask {
    public static final String TAG = ActionRequestUploadFileTask.class.getName();

    public static final String ARG_FILE_PATH = "arg_file_path";
    public static final String ARG_FILE_DESTINATION = "arg_file_destination";

    public static final String DEST_STORAGE = "storage";
    public static final String DEST_TRIP = "trip";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false)
                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, true)
                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, false);
        }

        Bundle inputBundle = getArgs();
        String filePath = inputBundle.getString(ARG_FILE_PATH);
        String destination = inputBundle.getString(ARG_FILE_DESTINATION);

        Logger.e(TAG, "ActionRequestUploadFileTask filePath: " + filePath);
        Logger.e(TAG, "ActionRequestUploadFileTask destination: " + destination);

        if (TextUtils.isEmpty(filePath)) {
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true)
                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, true)
                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, true);
        }

        String extension = null;
        int index = filePath.lastIndexOf(".");
        if (index > 0 && index + 1 < filePath.length()) {
            extension = filePath.substring(index + 1);
            Logger.e(TAG, "ActionRequestUploadFileTask extension: " + extension);
        }

        String mimeType = null;

        if (!TextUtils.isEmpty(extension) && MimeTypeMap.getSingleton().hasExtension(extension)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }


        if (TextUtils.isEmpty(mimeType)) {
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true)
                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, false)
                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, false);
        }

        Logger.e(TAG, "ActionRequestUploadFileTask mimeType: " + mimeType);

        try {
            HttpRestService service = RestApiConfig.getRestService();

            TypedFile typedFile = new TypedFile(mimeType, new File(filePath));
            //service.uploadFileToStorage(new TypedString(extension), new TypedString(destination), typedFile);
            // only test
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true)
                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, true)
                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, false);
        }

        Logger.e(TAG, "ActionRequestUploadFileTask success");
        return succeeded();
    }
}
