package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.utils.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageDocumentActivity extends BaseActivity {

    public static final String TAG = StorageDocumentActivity.class.getName();
    public static final String EXTRA_OPEN_STORAGE_DOCUMENT = "openStorageDocument";
    public static final String ITEM_STORAGE_INFO = "item_storage_info";

    private ImageView mIconSync;
    private ImageView mIconView;
    private ImageView mIconRemove;

    private PhotoViewAttacher mAttacher;

    private ImageView mImageThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        StorageInfo item = getIntent().getParcelableExtra(ITEM_STORAGE_INFO);
        if (item == null) {
            finish();
            return;
        }

        String name = item.getName();
        if (!TextUtils.isEmpty(name))
            getSupportActionBar().setTitle(name);
        else
            getSupportActionBar().setTitle(getString(R.string.unknown_name));
        showActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);

        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_STORAGE_DOCUMENT);

        mIconSync = (ImageView) findViewById(R.id.ivSync);
        mIconView = (ImageView) findViewById(R.id.ivView);
        mIconRemove = (ImageView) findViewById(R.id.ivRemove);

        mIconSync.setColorFilter(getResources().getColor(R.color.colorLightSuccess));
        mIconView.setColorFilter(getResources().getColor(R.color.colorLightPrimary));
        mIconRemove.setColorFilter(getResources().getColor(R.color.colorLightError));

        mImageThumbnail = (ImageView) findViewById(R.id.ivThumbnail);
        if (!TextUtils.isEmpty(item.getImagePath())) {
            Logger.e(TAG, "StorageDocumentActivity load path: " + item.getImagePath());
            Picasso.with(this)
                    .load(item.getImagePath())
                    .placeholder(R.mipmap.ic_document_default)
                    .error(R.mipmap.ic_document_error)
                    .into(mImageThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            mAttacher = new PhotoViewAttacher(mImageThumbnail);
                            mAttacher.setZoomable(true);
                            mAttacher.update();
                        }

                        @Override
                        public void onError() {
                            mAttacher = new PhotoViewAttacher(mImageThumbnail);
                            mAttacher.setZoomable(true);
                            mAttacher.update();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_storage_document;
    }

    public static void launch(BaseActivity activity, View transitionView, StorageInfo item) {
        Intent intent = new Intent(activity, StorageDocumentActivity.class);
        intent.putExtra(ITEM_STORAGE_INFO, item);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_STORAGE_DOCUMENT
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
