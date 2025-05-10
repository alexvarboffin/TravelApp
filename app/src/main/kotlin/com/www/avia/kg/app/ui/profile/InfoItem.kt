package com.www.avia.kg.app.ui.profile;

public class InfoItem {

    private final int iconRes;
    private final String title;

    public InfoItem(int iconRes, String title) {
        this.iconRes = iconRes;
        this.title = title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getTitle() {
        return title;
    }
}

