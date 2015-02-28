package com.zetterstrom.android.list.dto;

public class ListItem {
    private boolean mCompleted = false;
    private String mTitle = "";
    private String mId = "";
    private String mParentListId = "";

    public ListItem() {

    }

    public ListItem(String title) {
        mTitle = title;
    }

    public ListItem(String title, boolean completed) {
        mTitle = title;
        mCompleted = completed;
    }

    public void setCompleted(boolean complete) {
        mCompleted = complete;
    }

    public boolean getCompleted() {
        return mCompleted;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setItemId(String id) {
        mId = id;
    }

    public String getItemId() {
        return mId;
    }

    public void setParentListId(String id) {
        mParentListId = id;
    }

    public String getParentListId() {
        return mParentListId;
    }
}
