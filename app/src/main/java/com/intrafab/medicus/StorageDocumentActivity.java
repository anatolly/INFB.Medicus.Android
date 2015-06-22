package com.intrafab.medicus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.utils.BitmapTransform;
import com.intrafab.medicus.utils.Logger;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageDocumentActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = StorageDocumentActivity.class.getName();
    public static final String EXTRA_OPEN_STORAGE_DOCUMENT = "openStorageDocument";
    public static final String ITEM_STORAGE_INFO = "item_storage_info";

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    private ImageView mIconSync;
    private ImageView mIconView;
    private ImageView mIconRemove;

    private PhotoViewAttacher mAttacher;

    private ImageView mImageThumbnail;
    private ProgressWheel mProgressWheel;

    private RelativeLayout mLayoutFileInfo;
    private ImageView mImageViewFileThumbnail;
    private TextView mTextViewFileType;
    private TextView mTextViewFileDate;
    private FloatingActionButton mButtonFileView;

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

        mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        mProgressWheel.setVisibility(View.GONE);

        mImageThumbnail = (ImageView) findViewById(R.id.ivThumbnail);

        mLayoutFileInfo = (RelativeLayout) findViewById(R.id.layoutFileInfo);
        mImageViewFileThumbnail = (ImageView) findViewById(R.id.ivFileThumbnail);
        mTextViewFileType = (TextView) findViewById(R.id.tvFileType);
        mTextViewFileDate = (TextView) findViewById(R.id.tvFileDate);
        mButtonFileView = (FloatingActionButton) findViewById(R.id.btnFileView);
        mButtonFileView.setOnClickListener(this);

        String fileType = item.getType();
        if (!TextUtils.isEmpty(fileType)
                && (fileType.equalsIgnoreCase("JPG") || fileType.equalsIgnoreCase("JPEG"))) {
            mLayoutFileInfo.setVisibility(View.GONE);
            mButtonFileView.setVisibility(View.GONE);
            mImageThumbnail.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(item.getImagePath())) {
                Logger.e(TAG, "StorageDocumentActivity load path: " + item.getImagePath());
                mProgressWheel.setVisibility(View.VISIBLE);
                Picasso.with(this)
                        .load(item.getImagePath())
//                    .placeholder(R.mipmap.ic_document_default)
                        .error(R.mipmap.ic_document_error)
                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                        .resize(size, size)
                        .centerInside()
                        .tag(this)
                        .into(mImageThumbnail, new Callback() {
                            @Override
                            public void onSuccess() {
                                mProgressWheel.setVisibility(View.GONE);
                                mAttacher = new PhotoViewAttacher(mImageThumbnail);
                                mAttacher.setZoomable(true);
                                mAttacher.update();
                            }

                            @Override
                            public void onError() {
                                mProgressWheel.setVisibility(View.GONE);
                                mAttacher = new PhotoViewAttacher(mImageThumbnail);
                                mAttacher.setZoomable(true);
                                mAttacher.update();
                            }
                        });
            }
        } else {
            mLayoutFileInfo.setVisibility(View.VISIBLE);
            mButtonFileView.setVisibility(View.GONE);
            mImageThumbnail.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(item.getType())) {
                String type = item.getType();

                if (type.equalsIgnoreCase("JPG") ||
                        type.equalsIgnoreCase("JPEG")) {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_jpg);
                } else if (type.equalsIgnoreCase("ZIP")) {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_zip);
                } else if (type.equalsIgnoreCase("PDF")) {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_pdf);
                } else if (type.equalsIgnoreCase("DOC") ||
                        type.equalsIgnoreCase("DOCX")) {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_doc);
                } else if (type.equalsIgnoreCase("DCM") ||
                        type.equalsIgnoreCase("DICOM")) {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_dcm);
                } else {
                    mImageViewFileThumbnail.setImageResource(R.mipmap.ic_format_unknown);
                }

                mTextViewFileType.setText(type);
            }

            if (item.getUpdatedAt() != null) {
                long time = item.getUpdatedAt().getTime();
                if (time > 0) {
                    Calendar calStart = GregorianCalendar.getInstance();
                    calStart.setTimeInMillis(time);

                    String dateTime = DateUtils.formatDateTime(this, calStart.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
                    mTextViewFileDate.setText(dateTime);
                } else {
                    mTextViewFileDate.setText("");
                }
            } else {
                mTextViewFileDate.setText("");
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(this);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnFileView:
                break;
        }
    }

//    private void openFile() {
//        MimeTypeMap myMime = MimeTypeMap.getSingleton();
//        Intent newIntent = new Intent(Intent.ACTION_VIEW);
//        String mimeType = myMime.getMimeTypeFromExtension(fileExt(getFile()).substring(1));
//        newIntent.setDataAndType(Uri.fromFile(getFile()),mimeType);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            startActivity(newIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private String fileExt(String url) {
//        if (url.indexOf("?") > -1) {
//            url = url.substring(0, url.indexOf("?"));
//        }
//        if (url.lastIndexOf(".") == -1) {
//            return null;
//        } else {
//            String ext = url.substring(url.lastIndexOf("."));
//            if (ext.indexOf("%") > -1) {
//                ext = ext.substring(0, ext.indexOf("%"));
//            }
//            if (ext.indexOf("/") > -1) {
//                ext = ext.substring(0, ext.indexOf("/"));
//            }
//            return ext.toLowerCase();
//
//        }
//    }
}
