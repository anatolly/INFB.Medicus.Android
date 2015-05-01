package com.intrafab.medicus.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.adapters.SectionStateEntryAdapter;
import com.intrafab.medicus.adapters.StateEntryAdapter;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class ItemStateEntryView extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private StateEntry mItem;
    private StateEntryAdapter.OnClickListener mListener;

    private ImageView mImageThumbnail;
    private TextView mTextViewEventName;
    private TextView mTextViewTime;
    private RelativeLayout mItemLayout;
    private View rootView;

    public ImageView getThumbnail() {
        return mImageThumbnail;
    }

    public ItemStateEntryView(View view, StateEntryAdapter.OnClickListener listener) {
        super(view);

        rootView = view;

        itemView.setOnClickListener(this);
        mListener = listener;

        setupChildren(view);
        rootView.setOnClickListener(this);
    }

    private void setupChildren(View view) {
        mImageThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
        mTextViewEventName = (TextView) view.findViewById(R.id.textViewName);
        mTextViewTime = (TextView) view.findViewById(R.id.textViewTime);
        mItemLayout = (RelativeLayout) view.findViewById(R.id.rlItemLayout);
    }

    public void setItem(StateEntry itemStateEntry) {
        mItem = itemStateEntry;

        fillView(mItem);
    }

    @Override
    public void onClick(View view) {
        if (mItem != null && mListener != null) {
            mListener.onClickItem(mItem, this);
        }
    }

    public Context getContext() {
        if (rootView == null)
            return null;

        return rootView.getContext();
    }

    private void fillView(StateEntry item) {
        if (item == null)
            return;


        String rangeTime = "";
        if (item.getStateEnd() > 0 && item.getStateStart() > 0) {
            Calendar calStart = GregorianCalendar.getInstance();
            calStart.setTimeInMillis(item.getStateStart());

            Calendar calEnd = GregorianCalendar.getInstance();
            calEnd.setTimeInMillis(item.getStateEnd());

            rangeTime = DateUtils.formatDateRange(getContext(), calStart.getTimeInMillis(), calEnd.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        } else if (item.getStateEnd() > 0 && item.getStateStart() <= 0) {
            Calendar calEnd = GregorianCalendar.getInstance();
            calEnd.setTimeInMillis(item.getStateEnd());

            rangeTime = DateUtils.formatDateTime(getContext(), calEnd.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        } else if (item.getStateEnd() <= 0 && item.getStateStart() > 0) {
            Calendar calStart = GregorianCalendar.getInstance();
            calStart.setTimeInMillis(item.getStateStart());

            rangeTime = DateUtils.formatDateTime(getContext(), calStart.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        }

        mTextViewTime.setText(rangeTime);

        StateEntryType entryType = null;
        String itemType = item.getStateType();
        if (!TextUtils.isEmpty(itemType) && StateEntryType.TYPES.containsKey(itemType)) {
            entryType = StateEntryType.TYPES.get(itemType);
        } else {
            entryType = StateEntryType.getDefault();
        }

        rootView.setBackgroundColor(entryType.getBackgroundColor());
        mTextViewEventName.setTextColor(entryType.getTextColor());
        mTextViewTime.setTextColor(entryType.getTextColor());
        mImageThumbnail.setImageResource(entryType.getResIconId());

        if (!TextUtils.isEmpty(item.getStateDescription())) {
            mTextViewEventName.setText(item.getStateDescription());
        } else {
            mTextViewEventName.setText(R.string.document_name_unknown);
        }

    }

}
