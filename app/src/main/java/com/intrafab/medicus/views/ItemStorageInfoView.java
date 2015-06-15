package com.intrafab.medicus.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.StorageAdapter;
import com.intrafab.medicus.data.StorageInfo;
import com.squareup.picasso.Picasso;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class ItemStorageInfoView extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private StorageInfo mItem;
    private StorageAdapter.OnClickListener mListener;

    private ImageView mImageThumbnail;
    private TextView mTextViewDocumentName;
    private ImageView mImageArrowRight;
    private View rootView;

    public ItemStorageInfoView(View view, StorageAdapter.OnClickListener listener) {
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
            mListener.onClickItem(mItem);
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
        if (!TextUtils.isEmpty(item.getName())) {
            Picasso.with(getContext())
                    .load(item.getName())
                    .placeholder(R.mipmap.ic_document_default)
                    .error(R.mipmap.ic_document_error)
                    .into(mImageThumbnail);
        } else {
            mImageThumbnail.setImageResource(R.mipmap.ic_document_default);
        }

        if (!TextUtils.isEmpty(item.getName())) {
            mTextViewDocumentName.setText(item.getName());
        } else {
            mTextViewDocumentName.setText(R.string.document_name_unknown);
        }

    }

}
