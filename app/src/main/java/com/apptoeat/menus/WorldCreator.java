package com.apptoeat.menus;


import com.apptoeat.env.types.EntityType;
import com.apptoeat.utils.WorldRepo;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class WorldCreator {
    private String name;
    private List<EntityType> entityTypes;

    public WorldCreator() {

    }

    public WorldCreator(String name) {
        this.name = name;

        this.entityTypes = new ArrayList<>();

        EntityType baseFlower = new EntityType();
        baseFlower.healthProperty.initHealth = 0.01;
        baseFlower.multiplicationProperty.reproductionRate = 0.002;

        EntityType typeB = new EntityType();
        EntityType typeC = new EntityType();
        baseFlower.getCreationProperty().baseAmount = 100;
        typeC.healthProperty.attackDamage = 0.1;
        typeB.getCreationProperty().type = 1;
        typeB.getCreationProperty().baseAmount = 30;
        typeC.getCreationProperty().baseAmount = 10;
        typeC.getCreationProperty().type = 1;
        typeC.getHealthProperty().initHealth = 1;
        baseFlower.getCreationProperty().randomSpawn = true;
        typeC.getHealthProperty().initFood = 2.0;
        typeB.getHealthProperty().initFood = 2.0;
        typeC.multiplicationProperty.reproductionRate *= 0.6;
        typeC.healthProperty.carnivore = true;

        entityTypes.add(baseFlower);
        entityTypes.add(typeB);
        entityTypes.add(typeC);
    }

    public String getName() {
        return name;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }
}
