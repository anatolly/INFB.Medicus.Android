package com.intrafab.medicus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.intrafab.medicus.actions.ActionRequestStorageTask;
import com.intrafab.medicus.actions.ActionRequestUploadFileTask;
import com.intrafab.medicus.adapters.StorageAdapter;
import com.intrafab.medicus.adapters.StoragePagerAdapter;
import com.intrafab.medicus.adapters.StorageTripAdapter;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.loaders.MeLoader;
import com.intrafab.medicus.loaders.StorageListLoader;
import com.intrafab.medicus.loaders.StorageTripListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.FileUtils;
import com.intrafab.medicus.utils.Logger;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.TaskHandler;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class StorageActivity extends BaseActivity
        implements StorageAdapter.OnClickListener,
        StorageTripAdapter.OnClickListener,
        View.OnClickListener {

    public static final String TAG = StorageActivity.class.getName();
    public static final String EXTRA_OPEN_STORAGE = "openStorage";

    private static final int LOADER_STORAGE_ID = 10;
    private static final int LOADER_STORAGE_TRIP_ID = 11;
    private static final int LOADER_ME_ID = 15;

    private static final int REQUEST_CODE_PICK_FILE = 500;
    public static final int REQUEST_CODE_PICK_IMAGE = 600;

    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private StoragePagerAdapter mAdapter;

    private FloatingActionsMenu mActionsMenu;
//    private FloatingActionButton mBtnSyncCloud;
    private FloatingActionButton mBtnCamera;
    private FloatingActionButton mBtnGallery;

    private boolean mEnabledSyncMenu;

    private CallbacksManager mCallbacksManager;
    private TaskHandler mUploadTaskHandler;
    private MaterialDialog mUploadProgressDialog;

    private boolean mIsStartUpload = false;
    private String mLastFilePath;

    private android.app.LoaderManager.LoaderCallbacks<List<StorageInfo>> mLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<List<StorageInfo>>() {
        @Override
        public android.content.Loader<List<StorageInfo>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_STORAGE_ID:
                    return createStorageAllLoader();
                case LOADER_STORAGE_TRIP_ID:
                    return createStorageTripLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<List<StorageInfo>> loader, List<StorageInfo> data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_STORAGE_ID:
                    finishedStorageLoader(data);
                    break;
                case LOADER_STORAGE_TRIP_ID:
                    finishedStorageTripLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<List<StorageInfo>> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_STORAGE_ID:
                    resetStorageLoader();
                    break;
                case LOADER_STORAGE_TRIP_ID:
                    resetStorageLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.app.LoaderManager.LoaderCallbacks<Account> mLoaderMeCallback = new android.app.LoaderManager.LoaderCallbacks<Account>() {
        @Override
        public android.content.Loader<Account> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ME_ID:
                    return createAccountLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<Account> loader, Account data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    finishedAccountLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<Account> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    resetAccountLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<List<StorageInfo>> createStorageAllLoader() {
        Logger.d(TAG, "createStorageAllLoader");
        return new StorageListLoader(StorageActivity.this);
    }

    private android.content.Loader<List<StorageInfo>> createStorageTripLoader() {
        Logger.d(TAG, "createStorageTripLoader");
        return new StorageTripListLoader(StorageActivity.this);
    }

    private void finishedStorageLoader(List<StorageInfo> data) {
        if (data == null) {
            Logger.d(TAG, "finishedStorageLoader start ActionRequestStorageTask");
            mAdapter.showProgress(0);
            String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
            Groundy.create(ActionRequestStorageTask.class)
                    .callback(StorageActivity.this)
                    .callbackManager(mCallbacksManager)
                    .arg(ActionRequestStorageTask.ARG_USER_OWNER_ID, userUid)
                    .queueUsing(StorageActivity.this);
        } else {
            Logger.d(TAG, "finishedStorageLoader setData size = " + data.size());
            mAdapter.hideProgress(0);
            mAdapter.setData(data, 0);
        }
    }

    private void finishedStorageTripLoader(List<StorageInfo> data) {
        if (data == null) {
            Logger.d(TAG, "finishedStorageTripLoader start ActionRequestStorageTripTask");
            mAdapter.showProgress(1);
            String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
            Groundy.create(ActionRequestStorageTask.class)
                    .callback(StorageActivity.this)
                    .callbackManager(mCallbacksManager)
                    .arg(ActionRequestStorageTask.ARG_USER_OWNER_ID, userUid)
                    .queueUsing(StorageActivity.this);
        } else {
            Logger.d(TAG, "finishedStorageTripLoader setData size = " + data.size());
            mAdapter.hideProgress(1);
            mAdapter.setData(data, 1);
        }
    }

    private void resetStorageLoader() {
        Logger.d(TAG, "resetStorageLoader");
        mAdapter.hideProgress(mPager.getCurrentItem());
        mAdapter.setData(null, mPager.getCurrentItem());
    }

    private android.content.Loader<Account> createAccountLoader() {
        Logger.d(TAG, "createAccountLoader");
        return new MeLoader(StorageActivity.this);
    }

    private void finishedAccountLoader(Account data) {
        Logger.d(TAG, "finishedAccountLoader data: " + (data == null ? "NULL" : data.getName()));
        AppApplication.getApplication(this).setUserAccount(data);

        if (mIsStartUpload) {
            String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
            mUploadTaskHandler = Groundy.create(ActionRequestUploadFileTask.class)
                    .callback(StorageActivity.this)
                    .callbackManager(mCallbacksManager)
                    .arg(ActionRequestUploadFileTask.ARG_FILE_DESTINATION, true/*mPager.getCurrentItem() == 0 ? false : true*/ ? ActionRequestUploadFileTask.DEST_TRIP : ActionRequestUploadFileTask.DEST_STORAGE)
                    .arg(ActionRequestUploadFileTask.ARG_FILE_PATH, mLastFilePath)
                    .arg(ActionRequestUploadFileTask.ARG_USER_OWNER_ID, userUid)
                    .queueUsing(StorageActivity.this);

            mIsStartUpload = false;
            mLastFilePath = null;
        }
    }

    private void resetAccountLoader() {
        Logger.d(TAG, "resetAccountLoader");
    }

    private MaterialTabListener mTabListener = new MaterialTabListener() {
        @Override
        public void onTabSelected(MaterialTab materialTab) {
            mPager.setCurrentItem(materialTab.getPosition());
            mActionsMenu.collapse();

            switch (materialTab.getPosition()) {
                case 0:
                    startStorageLoading();
                    break;
                case 1:
                    startStorageTripLoading();
                    break;
            }
        }

        @Override
        public void onTabReselected(MaterialTab materialTab) {

        }

        @Override
        public void onTabUnselected(MaterialTab materialTab) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE) {
            mActionsMenu.collapse();
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();

                    mLastFilePath = FileUtils.getPath(this, uri);
                    if (TextUtils.isEmpty(mLastFilePath)) {
                        showSnackBarError(getResources().getString(R.string.error_file_not_selected));
                        return;
                    } else if (!checkFileFormat(mLastFilePath)) {
                        Logger.e(TAG, "REQUEST_CODE_PICK_FILE Selected file: " + mLastFilePath);
                        showSnackBarError(getResources().getString(R.string.error_supported_file_format));
                        return;
                    }

//                    String filePath = uri.getPath();
//                    if (SupportVersion.Kitkat()) {
//                        filePath = FileUtils.getRealPathFromURI_API19(this, uri);
//                    } else {
//                        filePath = FileUtils.getRealPathFromURI_API11to18(this, uri);
//                    }
                    showSetupDialog(mLastFilePath, true/*mPager.getCurrentItem() == 0 ? false : true*/);
                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Logger.e(TAG, "onActivityResult requestCode REQUEST_CODE_PICK_IMAGE");
            if (resultCode == Activity.RESULT_OK) {
                Logger.e(TAG, "onActivityResult resultCode RESULT_OK");
                Uri imageUri = getPickImageResultUri(data);

                mLastFilePath = FileUtils.getPath(this, imageUri);
                if (TextUtils.isEmpty(mLastFilePath)) {
                    showSnackBarError(getResources().getString(R.string.error_file_not_selected));
                    return;
                } else if (!checkFileFormat(mLastFilePath)) {
                    Logger.e(TAG, "REQUEST_CODE_PICK_IMAGE Selected file: " + mLastFilePath);
                    showSnackBarError(getResources().getString(R.string.error_supported_file_format));
                    return;
                }

//                if (imageUri == null) {
//                    showSnackBarError(getResources().getString(R.string.error_file_not_selected));
//                    return;
//                } else if (!checkFileFormat(imageUri.getPath())) {
//                    Logger.e(TAG, "REQUEST_CODE_PICK_IMAGE Selected file: " + imageUri.getPath());
//                    showSnackBarError(getResources().getString(R.string.error_supported_file_format));
//                    return;
//                }


                showSetupDialog(mLastFilePath, true/*mPager.getCurrentItem() == 0 ? false : true*/);
            }
        }
    }

    private boolean checkFileFormat(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return false;

        String extension = null;
        int index = filePath.lastIndexOf(".");
        if (index > 0 && index + 1 < filePath.length()) {
            extension = filePath.substring(index + 1);
            Logger.e(TAG, "checkFileFormat extension: " + extension);
        }

//        String mimeType = null;
//
//        if (!TextUtils.isEmpty(extension) && MimeTypeMap.getSingleton().hasExtension(extension)) {
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//            Logger.e(TAG, "checkFileFormat mimeType: " + (TextUtils.isEmpty(mimeType) ? "NULL" : mimeType));
//        }

        if (TextUtils.isEmpty(extension))
            return false;

        return true;

//        if (extension.equalsIgnoreCase("JPG")
//                || extension.equalsIgnoreCase("JPEG")
//                || extension.equalsIgnoreCase("PDF")
//                || extension.equalsIgnoreCase("DOC")
//                || extension.equalsIgnoreCase("DOCX")
//                || extension.equalsIgnoreCase("DCM")
//                || extension.equalsIgnoreCase("DICOM")
//                || extension.equalsIgnoreCase("ZIP")) {
//            return true;
//        }
//
//        return false;
    }

    private void showSetupDialog(final String filePath, final boolean isTrip) {
        if (TextUtils.isEmpty(filePath))
            return;

        int lastSlash = filePath.lastIndexOf(File.separator);
        String contentText = String.format(
                getResources().getString(R.string.dialog_upload_content),
                filePath.substring(lastSlash < 0 ? 0 : lastSlash + 1));

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .titleColor(getResources().getColor(R.color.colorLightTextMain))
                .title(getResources().getString(R.string.dialog_upload_header))
                .contentColor(getResources().getColor(R.color.colorLightTextMain))
                .backgroundColor(getResources().getColor(R.color.colorLightWindowBackground))
                .content(contentText)
                .positiveText(getResources().getString(R.string.dialog_upload_button_success))
                .negativeText(getResources().getString(R.string.dialog_upload_button_cancel))
                .autoDismiss(false)
                .cancelable(false)
                .contentGravity(GravityEnum.CENTER)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        startFileUpload(filePath, isTrip);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.show();
    }

    private void startFileUpload(final String filePath, boolean isTrip) {
        mUploadProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_progress_upload_file)
                .titleColor(getResources().getColor(R.color.colorLightTextMain))
                .content(R.string.please_wait)
                .contentColor(getResources().getColor(R.color.colorLightTextMain))
                .backgroundColor(getResources().getColor(R.color.colorLightWindowBackground))
                .progress(true, 0)
                .autoDismiss(false)
                .cancelable(false)
//                .negativeText(getResources().getString(R.string.dialog_upload_button_cancel))
//                .callback(new MaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onNegative(final MaterialDialog dialog) {
//                        super.onNegative(dialog);
//
//                        if (mUploadTaskHandler != null) {
//                            mUploadTaskHandler.cancel(StorageActivity.this, 0, new GroundyManager.SingleCancelListener() {
//                                @Override
//                                public void onCancelResult(long id, int result) {
//                                    dialog.dismiss();
//                                }
//                            });
//                        } else {
//                            dialog.dismiss();
//                        }
//                    }
//                })
                .build();

        mUploadProgressDialog.show();

        Account userAccount = AppApplication.getApplication(this).getUserAccount();
        if (userAccount == null) {
            mIsStartUpload = true;
            getLoaderManager().initLoader(LOADER_ME_ID, null, mLoaderMeCallback);
            return;
        }

        String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
        mUploadTaskHandler = Groundy.create(ActionRequestUploadFileTask.class)
                .callback(StorageActivity.this)
                .callbackManager(mCallbacksManager)
                .arg(ActionRequestUploadFileTask.ARG_FILE_DESTINATION, isTrip ? ActionRequestUploadFileTask.DEST_TRIP : ActionRequestUploadFileTask.DEST_STORAGE)
                .arg(ActionRequestUploadFileTask.ARG_FILE_PATH, filePath)
                .arg(ActionRequestUploadFileTask.ARG_USER_OWNER_ID, userUid)
                .queueUsing(StorageActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle(getString(R.string.storage_screen_header));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        mEnabledSyncMenu = true;

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_STORAGE);

        mCallbacksManager = CallbacksManager.init(savedInstanceState);
        mCallbacksManager.linkCallbacks(this);

        mTabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        mPager = (ViewPager) this.findViewById(R.id.tabPager);

        mActionsMenu = (FloatingActionsMenu) this.findViewById(R.id.famAddFiles);
//        mBtnSyncCloud = (FloatingActionButton) this.findViewById(R.id.fabSyncCloud);
        mBtnCamera = (FloatingActionButton) this.findViewById(R.id.fabCamera);
        mBtnGallery = (FloatingActionButton) this.findViewById(R.id.fabGallery);

//        mBtnSyncCloud.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        mBtnGallery.setOnClickListener(this);

//        mBtnClear = (TextView) this.findViewById(R.id.btnClear);
//        mBtnSync = (TextView) this.findViewById(R.id.btnSync);
//
//        int rippleColor = getResources().getColor(R.color.colorLightEditTextHint);
//        float rippleAlpha = 0.5f;
//
//        mBtnClear.setOnClickListener(this);
//
//        MaterialRippleLayout.on(mBtnClear)
//                .rippleColor(rippleColor)
//                .rippleAlpha(rippleAlpha)
//                .rippleHover(true)
//                .create();
//
//        mBtnSync.setOnClickListener(this);
//
//        MaterialRippleLayout.on(mBtnSync)
//                .rippleColor(rippleColor)
//                .rippleAlpha(rippleAlpha)
//                .rippleHover(true)
//                .create();

        // init view pager
        mAdapter = new StoragePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                mTabHost.setSelectedNavigationItem(position);
                mActionsMenu.collapse();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    int position = mPager.getCurrentItem();
                    mTabHost.setSelectedNavigationItem(position);
                    mPager.setCurrentItem(position);
                    mActionsMenu.collapse();

                    switch (position) {
                        case 0:
                            startStorageLoading();
                            break;
                        case 1:
                            startStorageTripLoading();
                            break;
                    }
                }
            }
        });

        mTabHost.addTab(
                mTabHost.newTab()
                        .setText(getString(R.string.tab_storage_all))
                        .setTabListener(mTabListener)
        );
        mTabHost.addTab(
                mTabHost.newTab()
                        .setText(getString(R.string.tab_storage_trip))
                        .setTabListener(mTabListener)
        );

        mTabHost.post(new Runnable() {
            @Override
            public void run() {
                if (mPager.getCurrentItem() == 0) {
                    mTabHost.setSelectedNavigationItem(0);
                    startStorageLoading();
                } else {
                    mTabHost.setSelectedNavigationItem(1);
                    startStorageTripLoading();
                }
            }
        });

        Account userAccount = AppApplication.getApplication(this).getUserAccount();
        if (userAccount == null) {
            getLoaderManager().initLoader(LOADER_ME_ID, null, mLoaderMeCallback);
        }
    }

    private void startStorageLoading() {
        if (mAdapter.isProgress(mPager.getCurrentItem()))
            return;
        mAdapter.showProgress(mPager.getCurrentItem());
        getLoaderManager().initLoader(LOADER_STORAGE_ID, null, mLoaderCallback);
    }

    private void startStorageTripLoading() {
        if (mAdapter.isProgress(mPager.getCurrentItem()))
            return;
        mAdapter.showProgress(mPager.getCurrentItem());
        getLoaderManager().initLoader(LOADER_STORAGE_TRIP_ID, null, mLoaderCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_storage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_sync_with_cloud);
        if (item != null)
            item.setEnabled(mEnabledSyncMenu);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_add_document) {
//            //Toast.makeText(this, "Add Document. Coming soon.", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("file/*");
//            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
//            return true;
//        } else
        if (id == R.id.action_sync_with_cloud) {
            mAdapter.setData(null, mPager.getCurrentItem());

            mActionsMenu.collapse();
//            mBtnSyncCloud.setEnabled(false);
            enabledMenu(false);
            if (!Connectivity.isConnected(this)) {
                showSnackBarError(getResources().getString(R.string.error_internet_not_available));
//                mBtnSyncCloud.setEnabled(true);
                enabledMenu(true);
                return true;
            }

            mAdapter.showProgress(mPager.getCurrentItem());
            String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
            Groundy.create(ActionRequestStorageTask.class)
                    .callback(StorageActivity.this)
                    .callbackManager(mCallbacksManager)
                    .arg(ActionRequestStorageTask.ARG_USER_OWNER_ID, userUid)
                    .queueUsing(StorageActivity.this);
            return true;
        } else
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enabledMenu(boolean enabled) {
        mEnabledSyncMenu = enabled;
        invalidateOptionsMenu();
        mBtnCamera.setEnabled(enabled);
        mBtnGallery.setEnabled(enabled);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_storage;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCallbacksManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbacksManager.onDestroy();
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent storageIntent = new Intent(activity, StorageActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_STORAGE
                );
        ActivityCompat.startActivity(activity, storageIntent, options.toBundle());
    }

    private void showSnackBarError(String error) {
        SnackbarManager.show(
                Snackbar.with(StorageActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(error)
                        .color(getResources().getColor(R.color.colorLightError))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(true)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                , StorageActivity.this); // activity where it is displayed
    }

    private void showSnackBarSuccess(String text) {
        SnackbarManager.show(
                Snackbar.with(StorageActivity.this) // context
                        .type(SnackbarType.MULTI_LINE)
                        .text(text)
                        .color(getResources().getColor(R.color.colorLightSuccess))
                        .textColor(getResources().getColor(R.color.colorLightEditTextHint))
                        .swipeToDismiss(false)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                , StorageActivity.this); // activity where it is displayed
    }

    @OnSuccess(ActionRequestStorageTask.class)
    public void onSuccessRequestStorage() {

        mAdapter.hideProgress(mPager.getCurrentItem());

        mActionsMenu.collapse();
//        mBtnSyncCloud.setEnabled(true);
        enabledMenu(true);
    }

    @OnFailure(ActionRequestStorageTask.class)
    public void onFailureRequestStorage(@Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable) {
        mAdapter.hideProgress(mPager.getCurrentItem());

        mActionsMenu.collapse();
//        mBtnSyncCloud.setEnabled(true);
        enabledMenu(true);

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }
    }

//    @OnSuccess(ActionRequestStorageTripTask.class)
//    public void onSuccessRequestStorageTrip() {
//        mAdapter.hideProgress(mPager.getCurrentItem());
//    }
//
//    @OnFailure(ActionRequestStorageTripTask.class)
//    public void onFailureRequestStorageTrip(@Param(ActionRequestStorageTripTask.INTERNET_AVAILABLE) boolean isAvailable) {
//        mAdapter.hideProgress(mPager.getCurrentItem());
//
//        if (!isAvailable) {
//            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
//        } else {
//            showSnackBarError(getResources().getString(R.string.error_occurred));
//        }
//    }

    @OnSuccess(ActionRequestUploadFileTask.class)
    public void onSuccessRequestUploadFile() {
        Logger.e(TAG, "ActionRequestUploadFileTask onSuccessRequestUploadFile");
        if (mUploadProgressDialog != null) {
            Logger.e(TAG, "ActionRequestUploadFileTask dismiss");
            mUploadProgressDialog.dismiss();
            mUploadProgressDialog = null;
        }

        showSnackBarSuccess(getResources().getString(R.string.success_file_uploding));

        mActionsMenu.collapse();
//        mBtnSyncCloud.setEnabled(true);
        enabledMenu(true);

        mIsStartUpload = false;
        mLastFilePath = null;

//        DBManager.getInstance().onContentChanged();

//        mAdapter.setData(null, mPager.getCurrentItem());
//
//        if (mPager.getCurrentItem() == 0) {
//            DBManager.getInstance().deleteObject(Constants.Prefs.PREF_PARAM_STORAGE, StorageListLoader.class);
//        } else {
//            DBManager.getInstance().deleteObject(Constants.Prefs.PREF_PARAM_STORAGE_TRIP, StorageTripListLoader.class);
//        }
    }

    @OnFailure(ActionRequestUploadFileTask.class)
    public void onFailureRequestUploadFile(
            @Param(Constants.Extras.PARAM_INTERNET_AVAILABLE) boolean isAvailable,
            @Param(Constants.Extras.PARAM_SUPPORTED_FORMAT) boolean isSupportedFormat,
            @Param(Constants.Extras.PARAM_FILE_NOT_FOUND) boolean isFileNotFound) {

        Logger.e(TAG, "ActionRequestUploadFileTask onFailureRequestUploadFile");
        if (mUploadProgressDialog != null) {
            Logger.e(TAG, "ActionRequestUploadFileTask dismiss");
            mUploadProgressDialog.dismiss();
            mUploadProgressDialog = null;
        }

        if (!isAvailable) {
            showSnackBarError(getResources().getString(R.string.error_internet_not_available));
        } else if (isFileNotFound) {
            showSnackBarError(getResources().getString(R.string.error_file_not_found));
        } else if (!isSupportedFormat) {
            showSnackBarError(getResources().getString(R.string.error_supported_file_format));
        } else {
            showSnackBarError(getResources().getString(R.string.error_occurred));
        }

        mActionsMenu.collapse();
//        mBtnSyncCloud.setEnabled(true);
        enabledMenu(true);

        mIsStartUpload = false;
        mLastFilePath = null;
    }

    @Override
    public void onClickItem(StorageInfo itemStorageInfo) {
        StorageDocumentActivity.launch(this, toolbar, itemStorageInfo);
    }

    @Override
    public void onStorageTripClickItem(StorageInfo itemStorageInfo) {
        StorageDocumentActivity.launch(this, toolbar, itemStorageInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCamera:
                startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.fabGallery:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
                break;
//            case R.id.fabSyncCloud:
//                mAdapter.setData(null, mPager.getCurrentItem());
//
//                mActionsMenu.collapse(true);
//                mBtnSyncCloud.setEnabled(false);
//                mBtnCamera.setEnabled(false);
//                mBtnGallery.setEnabled(false);
//                if (!Connectivity.isConnected(this)) {
//                    showSnackBarError(getResources().getString(R.string.error_internet_not_available));
//                    mBtnSyncCloud.setEnabled(true);
//                    mBtnCamera.setEnabled(true);
//                    mBtnGallery.setEnabled(true);
//                    return;
//                }
//
//                mAdapter.showProgress(mPager.getCurrentItem());
//                String userUid = AppApplication.getApplication(this).getUserAccount().getUid();
//                Groundy.create(ActionRequestStorageTask.class)
//                        .callback(StorageActivity.this)
//                        .callbackManager(mCallbacksManager)
//                        .arg(ActionRequestStorageTask.ARG_USER_OWNER_ID, userUid)
//                        .queueUsing(StorageActivity.this);
//                break;
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // Create a chooser
        Intent chooserIntent = Intent.createChooser(allIntents.get(0), getResources().getString(R.string.chooser_header));
        allIntents.remove(0);
        // Add all other intents
        if (!allIntents.isEmpty())
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Logger.e(TAG, "getCaptureImageOutputUri start");
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();

        if (getImage != null) {
            if (!getImage.exists()) {
                getImage.mkdirs();
            }
            File imageFile = new File(getImage.getPath(), "pickImageResult.jpg");
            if (!imageFile.exists()) {
                try {
                    imageFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Logger.e(TAG, "getCaptureImageOutputUri imageFile: " + imageFile.getPath());
            outputFileUri = Uri.fromFile(imageFile);
            Logger.e(TAG, "getCaptureImageOutputUri outputFileUri: " + outputFileUri.getPath());
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
}
