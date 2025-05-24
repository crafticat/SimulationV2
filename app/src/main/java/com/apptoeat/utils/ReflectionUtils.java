package com.apptoeat.utils;

import com.apptoeat.env.proprties.SettingField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    public static List<SettingField> reflectProperties(Object propObject) {
        List<SettingField> fields = new ArrayList<>();

        // Get all declared public fields on the object's class
        Field[] declaredFields = propObject.getClass().getFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            Class<?> fieldType = field.getType();
            try {
                // Current value in that field
                Object value = field.get(propObject);

                // We only handle certain types, but you can add more if needed
                if (fieldType == double.class || fieldType == Double.class
                        || fieldType == int.class || fieldType == Integer.class
                        || fieldType == boolean.class || fieldType == Boolean.class) {

                    fields.add(new SettingField(name, fieldType, value, propObject));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return fields;
    }

    public static void updateProperty(Object propObject, SettingField settingField) {
        try {
            Field field = propObject.getClass().getField(settingField.getFieldName());
            Class<?> fieldType = field.getType();

            if (fieldType == double.class || fieldType == Double.class) {
                field.setDouble(propObject, Double.parseDouble(settingField.getStringValue()));
            } else if (fieldType == int.class || fieldType == Integer.class) {
                field.setInt(propObject, Integer.parseInt(settingField.getStringValue()));
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                field.setBoolean(propObject, Boolean.parseBoolean(settingField.getStringValue()));
            }
            // Extend with other types if needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
