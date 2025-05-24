package com.apptoeat.env.proprties;

public class PropertyGroup {
    private String title;
    private Object propertyObject;

    public PropertyGroup(String title, Object propertyObject) {
        this.title = title;
        this.propertyObject = propertyObject;
    }

    public String getTitle() {
        return title;
    }

    public Object getPropertyObject() {
        return propertyObject;
    }
}
