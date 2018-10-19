package com.keepcalmandkanji.foureyes;
//if error, delete all of the "checked" lines
public class Item {
    private String title;
    private String subtitle;
    private Boolean checked;

    Item(String title, String subtitle, Boolean checked) {
        this.title = title;
        this.subtitle = subtitle;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

}