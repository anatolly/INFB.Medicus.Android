package com.intrafab.medicus.actions;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.loaders.StorageTripListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.wrappers.RequestStorageInfo;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class ActionRequestStorageTripTask extends GroundyTask {

    public static final String INTERNET_AVAILABLE = "intternet_available";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(INTERNET_AVAILABLE, false);
        }

        try {
            HttpRestService service = RestApiConfig.getRestService();
            //RequestStorageInfo storageInfo = service.loadStorageTrip();

            Thread.sleep(5000);

            // only test
            List<StorageInfo> list = new ArrayList<StorageInfo>();
            list.add(createStorage(1, "https://www.iconfinder.com/icons/315189/document_image_icon#size=512", "Trip Document name 1", "", 0, "", ""));
            list.add(createStorage(3, "http://www.mcmonline.it/images/icona_zip.png", "Trip Document name 3", "", 0, "", ""));
            list.add(createStorage(4, "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRlN9CKVAETf34hS6BxM1sYPRG-LaJEdTrpWNndIVs1iNhKkgHO", "Trip Document name 4", "", 0, "", ""));
            list.add(createStorage(3, "", "Trip Document name 5", "", 0, "", ""));

            RequestStorageInfo storageInfo = new RequestStorageInfo();
            storageInfo.addSticker(list);
            // test ended

            if (storageInfo == null)
                return failed();

            List<StorageInfo> storageList = storageInfo.getStorageList();
            if (storageList.size() > 0)
                DBManager.getInstance().insertArrayObject(getContext(), StorageTripListLoader.class, Constants.Prefs.PREF_PARAM_STORAGE_TRIP, storageList, StorageInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return failed();
        }

        return succeeded();
    }

    private StorageInfo createStorage(int id, String thumbPath, String description, String type, long timestamp, String access_url, String access_session_id) {
        StorageInfo item = new StorageInfo();

        item.setId(id);
        item.setThumbnail(thumbPath);
        item.setDescription(description);
        item.setType(type);
        item.setTimestamp(timestamp);
        item.setAccess_url(access_url);
        item.setAccess_session_id(access_session_id);

        return item;
    }
}
