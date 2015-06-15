package com.intrafab.medicus.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.StorageTripAdapter;
import com.intrafab.medicus.data.StorageInfo;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class ItemStorageTripInfoView extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private StorageInfo mItem;
    private StorageTripAdapter.OnClickListener mListener;

    private ImageView mImageThumbnail;
    private TextView mTextViewDocumentName;
    private ImageView mImageArrowRight;
    private View rootView;

    public ItemStorageTripInfoView(View view, StorageTripAdapter.OnClickListener listener) {
        super(view);

        rootView = view;

        itemView.setOnClickListener(this);
        mListener = listener;

        setupChildren(view);
        rootView.setOnClickListener(this);
    }

    private void setupChildren(View view) {
        mImageThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
        mTextViewDocumentName = (TextView) view.findViewById(R.id.textViewName);
        mImageArrowRight = (ImageView) view.findViewById(R.id.ivRightArrow);
    }

    public void setItem(StorageInfo itemStorageInfo) {
        mItem = itemStorageInfo;

        fillView(mItem);
    }

    @Override
    public void onClick(View view) {
        if (mItem != null && mListener != null) {
            mListener.onStorageTripClickItem(mItem);
        }
    }

    public Context getContext() {
        if (rootView == null)
            return null;

        return rootView.getContext();
    }

    private void fillView(StorageInfo item) {
        if (item == null)
            return;
//TODO load document
//        if (!TextUtils.isEmpty(item.getName())) {
//            Picasso.with(getContext())
//                    .load(item.getName())
//                    .placeholder(R.mipmap.ic_document_default)
//                    .error(R.mipmap.ic_document_error)
//                    .into(mImageThumbnail);
//        } else {
//            mImageThumbnail.setImageResource(R.mipmap.ic_document_default);
//        }

        if (!TextUtils.isEmpty(item.getType())) {
            String type = item.getType();

            if (type.equalsIgnoreCase("JPG") ||
                    type.equalsIgnoreCase("JPEG")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_jpg);
            } else if (type.equalsIgnoreCase("ZIP")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_zip);
            } else if (type.equalsIgnoreCase("PDF")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_pdf);
            } else if (type.equalsIgnoreCase("DOC")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_doc);
            } else if (type.equalsIgnoreCase("DOCX")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_docx);
            } else if (type.equalsIgnoreCase("DCM") ||
                    type.equalsIgnoreCase("DICOM")) {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_dcm);
            } else {
                mImageThumbnail.setImageResource(R.mipmap.ic_format_unknown);
            }
        }

        if (!TextUtils.isEmpty(item.getName())) {
            mTextViewDocumentName.setText(item.getName());
        } else {
            mTextViewDocumentName.setText(R.string.document_name_unknown);
        }

    }

}
