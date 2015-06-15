package com.intrafab.medicus.actions;

import android.os.Bundle;
import android.text.TextUtils;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.loaders.StorageListLoader;
import com.intrafab.medicus.loaders.StorageTripListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Artemiy Terekhov on 04.06.2015.
 */
public class ActionRequestUploadFileTask extends GroundyTask {
    public static final String TAG = ActionRequestUploadFileTask.class.getName();

    public static final String ARG_FILE_PATH = "arg_file_path";
    public static final String ARG_FILE_DESTINATION = "arg_file_destination";
    public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

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
        String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

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

        String fileName = null;
        int indexSeparator = filePath.lastIndexOf(File.separator);
        if (index > 0 && indexSeparator > 0 && index + 1 < filePath.length()) {
            fileName = filePath.substring(indexSeparator + 1, index);
            Logger.e(TAG, "ActionRequestUploadFileTask fileName: " + fileName);
        }

        String mimeType = "multipart/form-data";

//        if (!TextUtils.isEmpty(extension) && MimeTypeMap.getSingleton().hasExtension(extension)) {
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//
//
//        if (TextUtils.isEmpty(mimeType)) {
//            return failed()
//                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true)
//                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, false)
//                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, false);
//        }

        Logger.e(TAG, "ActionRequestUploadFileTask mimeType: " + mimeType);

        try {
            HttpRestService service = RestApiConfig.getRestService2();

            TypedFile typedFile = new TypedFile(mimeType, new File(filePath));
            StorageInfo newStorageInfo = service.uploadFileToStorage(
                    new TypedString(fileName),
                    new TypedString(extension),
                    new TypedString(destination.equals(DEST_TRIP) ? "true" : "false"),
                    new TypedString(userUid),
                    typedFile);

            Logger.e(TAG, "ActionRequestUploadFileTask newStorageInfo: " + newStorageInfo.getId());

            if (newStorageInfo != null)
                DBManager.getInstance().insertObjectToArray(getContext(), StorageListLoader.class, Constants.Prefs.PREF_PARAM_STORAGE, newStorageInfo, StorageInfo[].class);

            if (newStorageInfo != null && newStorageInfo.isBelongsToActiveTrip())
                DBManager.getInstance().insertObjectToArray(getContext(), StorageTripListLoader.class, Constants.Prefs.PREF_PARAM_STORAGE_TRIP, newStorageInfo, StorageInfo[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true)
                    .add(Constants.Extras.PARAM_SUPPORTED_FORMAT, true)
                    .add(Constants.Extras.PARAM_FILE_NOT_FOUND, false);
        }

        DBManager.getInstance().onContentChanged();
        Logger.e(TAG, "ActionRequestUploadFileTask success");
        return succeeded();
    }
}
