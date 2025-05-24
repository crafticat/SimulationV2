package com.apptoeat.env.proprties;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class CreationProperty {

    public int type = 0;
    public boolean randomSpawn = false;
    public int baseAmount = 10;

    public CreationPropertyType getTypeEnum() {
        return CreationPropertyType.values()[type];
    }

    public enum CreationPropertyType {
        Planet,
        EntityLiving
    }
}
