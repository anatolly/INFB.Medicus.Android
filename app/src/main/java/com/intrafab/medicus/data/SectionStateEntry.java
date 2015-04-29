package com.intrafab.medicus.data;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class SectionStateEntry {
    private int mFirstPosition;
    private int mSectionedPosition;
    private CharSequence mTitle;

    public CharSequence getTitle() {
        return mTitle;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
    }

    public int getFirstPosition() {
        return mFirstPosition;
    }

    public void setFirstPosition(int firstPosition) {
        this.mFirstPosition = firstPosition;
    }

    public int getSectionedPosition() {
        return mSectionedPosition;
    }

    public void setSectionedPosition(int sectionedPosition) {
        this.mSectionedPosition = sectionedPosition;
    }

    public SectionStateEntry(int firstPosition, CharSequence title) {
        this.mFirstPosition = firstPosition;
        this.mTitle = title;
    }
}
