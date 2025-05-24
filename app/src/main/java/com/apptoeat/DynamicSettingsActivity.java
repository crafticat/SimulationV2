package com.apptoeat;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.apptoeat.R;
import com.apptoeat.env.proprties.PropertyGroup;
import com.apptoeat.env.proprties.SettingField;
import com.apptoeat.env.types.EntityType;
import com.apptoeat.utils.ReflectionUtils;
import com.apptoeat.utils.WorldRepo;
import com.apptoeat.views.WorldCreatorActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class DynamicSettingsActivity extends AppCompatActivity {

    private LinearLayout container;
    private List<SettingField> allSettings = new ArrayList<>();
    private MaterialButton btnApply;

    public static EntityType entityTypeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_settings);

        container = findViewById(R.id.dynamic_settings_container);
        btnApply = findViewById(R.id.btnApply);

        // Define property groups to edit – add as many as needed
        List<PropertyGroup> propertyGroups = new ArrayList<>();
        propertyGroups.add(new PropertyGroup("Flower Properties", entityTypeRef.getFlowerProperty()));
        propertyGroups.add(new PropertyGroup("Health Properties", entityTypeRef.getHealthProperty()));
        propertyGroups.add(new PropertyGroup("Multiplication Properties", entityTypeRef.getMultiplicationProperty()));
        propertyGroups.add(new PropertyGroup("Creation Properties", entityTypeRef.getCreationProperty()));

        // Loop through each group and generate a header plus material input fields
        for (PropertyGroup pg : propertyGroups) {
            // Create group header
            TextView groupHeader = new TextView(this);
            groupHeader.setText(pg.getTitle());

            groupHeader.setTextSize(18f);
            groupHeader.setTextColor(getResources().getColor(R.color.colorAccentCyan));
            groupHeader.setPadding(0, 16, 0, 8);
            container.addView(groupHeader);

            groupHeader.setShadowLayer(2f, 1f, 1f, Color.WHITE);


            // Reflect properties for this group
            List<SettingField> fields = ReflectionUtils.reflectProperties(pg.getPropertyObject());
            for (SettingField field : fields) {
                // Create a TextInputLayout for a better Material look
                TextInputLayout til = new TextInputLayout(this);
                LinearLayout.LayoutParams tilParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                tilParams.setMargins(0, 8, 0, 8);
                til.setLayoutParams(tilParams);
                til.setHint(field.getFieldName());
                til.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

                // Set all corners to 8dp (converted to pixels)
                float radius = 8 * getResources().getDisplayMetrics().density;
                til.setBoxCornerRadii(radius, radius, radius, radius);
                til.setBoxStrokeColor(getResources().getColor(R.color.colorAccentCyan));
                til.setHintTextColor(getResources().getColorStateList(R.color.colorAccentCyan));

                // Create a TextInputEditText inside the TextInputLayout
                TextInputEditText editText = new TextInputEditText(this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                editText.setText(field.getStringValue());
                editText.setTextColor(getResources().getColor(R.color.colorOnSurfaceDark));
                til.addView(editText);

                // Add the TextInputLayout to the container
                container.addView(til);

                // Save a reference to the editor in the field for later update
                field.setLinkedEditor(editText);
                allSettings.add(field);
            }
        }

        btnApply.setOnClickListener(view -> {
            for (SettingField sf : allSettings) {
                String newValue = sf.getLinkedEditor().getText().toString();
                sf.setValueFromString(newValue);
                ReflectionUtils.updateProperty(sf.getParentObject(), sf);
            }

            WorldRepo.save(WorldCreatorActivity.getCurrentWorld());
            setResult(RESULT_OK);
            finish();
        });
    }
}
