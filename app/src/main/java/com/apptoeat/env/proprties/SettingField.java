package com.apptoeat.env.proprties;

import android.widget.EditText;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingField {

    private String fieldName;
    private Class<?> fieldType;
    private Object currentValue;   // e.g. could be Double, Integer, Boolean, or a raw String
    private Object parentObject;   // Optional reference to the parent property (FlowerProperty, etc.)

    // (Optional) If you want to link a UI widget directly:
    private EditText linkedEditor;

    /**
     * Minimal constructor if you only want name, type, and value.
     */
    public SettingField(String fieldName, Class<?> fieldType, Object currentValue) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.currentValue = currentValue;
    }

    /**
     * Full constructor if you also want to store the parent object.
     */
    public SettingField(String fieldName, Class<?> fieldType, Object currentValue, Object parentObject) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.currentValue = currentValue;
        this.parentObject = parentObject;
    }

    // GETTERS
    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public Object getParentObject() {
        return parentObject;
    }

    // If you want to store a linked editor:
    // public void setLinkedEditor(EditText et) { this.linkedEditor = et; }
    // public EditText getLinkedEditor() { return this.linkedEditor; }

    /**
     * Convert the currentValue to string form for editing (e.g., for an EditText).
     */
    public String getStringValue() {
        return (currentValue == null) ? "" : currentValue.toString();
    }

    /**
     * Store a new value as a raw string.
     * Later, you might parse it when actually applying the change to the object.
     */
    public void setValueFromString(String newValue) {
        // We'll keep it as a raw string here (or parse it immediately—your choice).
        this.currentValue = newValue;
    }
}

