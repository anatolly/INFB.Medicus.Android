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
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class ActionRequestStorageTask extends GroundyTask {
    public static final String TAG = ActionRequestStorageTask.class.getName();

    public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

    @Override
    protected TaskResult doInBackground() {

        Bundle inputBundle = getArgs();
        String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

        if (!Connectivity.isNetworkConnected()) {
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        try {
            HttpRestService service = RestApiConfig.getFilestorageRestService();
            List<StorageInfo> storageList = service.loadStorage();

//            Thread.sleep(5000);
//
//            // only test
//            List<StorageInfo> list = new ArrayList<StorageInfo>();
//            list.add(createStorage(1, "https://www.iconfinder.com/icons/315189/document_image_icon#size=512", "Document name 1", "", 0, "", ""));
//            list.add(createStorage(2, "", "Document name 2", "", 0, "", ""));
//            list.add(createStorage(3, "http://www.mcmonline.it/images/icona_zip.png", "Document name 3", "", 0, "", ""));
//            list.add(createStorage(4, "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRlN9CKVAETf34hS6BxM1sYPRG-LaJEdTrpWNndIVs1iNhKkgHO", "Document name 4", "", 0, "", ""));
//            list.add(createStorage(5, "", "Document name 5", "", 0, "", ""));
//            list.add(createStorage(6, "", "Document name 6", "", 0, "", ""));
//            list.add(createStorage(7, "", "Document name 7", "", 0, "", ""));
//            list.add(createStorage(8, "", "Document name 8", "", 0, "", ""));
//            list.add(createStorage(9, "", "Document name 9", "", 0, "", ""));
//            list.add(createStorage(10, "", "Document name 10", "", 0, "", ""));
//
//            RequestStorageInfo storageInfo = new RequestStorageInfo();
//            storageInfo.addSticker(list);
            // test ended

//            if (storageInfo == null)
//                return failed();

//            List<StorageInfo> storageList = storageInfo.getStorageList();
            if (storageList.size() > 0) {
                ArrayList<StorageInfo> storageData = new ArrayList<StorageInfo>();
                ArrayList<StorageInfo> storageTripData = new ArrayList<StorageInfo>();

                for (StorageInfo info : storageList) {
                    String id = String.format("%d", info.getBelongsToUser());
                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(userUid) && userUid.equals(id) ) {
                        if (info.isBelongsToActiveTrip()) {
                            storageTripData.add(info);
                        }

                        storageData.add(info);
                    }
                }

                if (storageData.size() > 0)
                    DBManager.getInstance().insertArrayObject(getContext(), StorageListLoader.class, Constants.Prefs.PREF_PARAM_STORAGE, storageData, StorageInfo.class);

                if (storageTripData.size() > 0)
                    DBManager.getInstance().insertArrayObject(getContext(), StorageTripListLoader.class, Constants.Prefs.PREF_PARAM_STORAGE_TRIP, storageTripData, StorageInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return succeeded();
    }

//    private StorageInfo createStorage(int id, String thumbPath, String description, String type, long timestamp, String access_url, String access_session_id) {
//        StorageInfo item = new StorageInfo();
//
//        item.setId(id);
//        item.setThumbnail(thumbPath);
//        item.setDescription(description);
//        item.setType(type);
//        item.setTimestamp(timestamp);
//        item.setAccess_url(access_url);
//        item.setAccess_session_id(access_session_id);
//
//        return item;
//    }
}
