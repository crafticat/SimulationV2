package com.apptoeat.env.types;

import com.apptoeat.env.proprties.CreationProperty;
import com.apptoeat.env.proprties.FlowerProperty;
import com.apptoeat.env.proprties.HealthProperty;
import com.apptoeat.env.proprties.MultiplicationProperty;
import com.apptoeat.env.proprties.PlanetProperty;
import com.apptoeat.env.proprties.ViewProperty;
import com.google.firebase.database.IgnoreExtraProperties;

import lombok.Getter;

@Getter
@IgnoreExtraProperties
public class EntityType {

    public FlowerProperty flowerProperty;
    public MultiplicationProperty multiplicationProperty;
    public ViewProperty viewProperty;
    public HealthProperty healthProperty;
    public CreationProperty creationProperty;
    public int id = nextId++;
    public static int nextId =0;

    public EntityType() {
        flowerProperty = new FlowerProperty();
        multiplicationProperty = new MultiplicationProperty();
        viewProperty = new ViewProperty();
        healthProperty = new HealthProperty();
        creationProperty = new CreationProperty();
    }

    public String getName() {
        return "Entity #" + id;
    }
}
