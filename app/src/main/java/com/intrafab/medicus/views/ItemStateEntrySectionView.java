package com.intrafab.medicus.views;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.intrafab.medicus.R;
import com.intrafab.medicus.data.SectionStateEntry;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class ItemStateEntrySectionView extends RecyclerView.ViewHolder {

    private TextView mTextViewHeader;
    private SectionStateEntry mItem;

    public ItemStateEntrySectionView(View view) {
        super(view);

        mTextViewHeader = (TextView) view.findViewById(R.id.section_text);
    }

    public void setItem(SectionStateEntry item) {
        mItem = item;

        fillView(mItem);
    }

    private void fillView(SectionStateEntry item) {
        if (item == null)
            return;

        if (!TextUtils.isEmpty(item.getTitle())) {
            mTextViewHeader.setText(item.getTitle());
        } else {
            mTextViewHeader.setText(R.string.document_name_unknown);
        }

    }
}
