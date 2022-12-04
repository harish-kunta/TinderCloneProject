package com.harish.tinder.model;

public class Interest {
    public String name;
    private boolean isSelected = false;

    public Interest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Interest(String name) {
        this.name = name;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String toString()
    {
        if(this.name != null)
            return this.name;
        return "";
    }
}
