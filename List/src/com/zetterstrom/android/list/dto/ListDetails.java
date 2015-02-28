package com.zetterstrom.android.list.dto;

public class ListDetails {
    private String mId = "";
    private String mTitle = "New List";

    public ListDetails() {

    }

    public ListDetails(String title) {
        mTitle = title;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
