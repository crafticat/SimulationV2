package com.apptoeat.env.proprties;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class HealthProperty {

    public double attackDamage = 0.01;
    public double initFood = 1.0;
    public double food = 1;
    public double initHealth = 1.0;
    public double hungerDec = 0.002;
    public double hungerDamage = 0.01;
    public double knockback = 0.01;
    public double reach = 10;
    public boolean carnivore = false;
}
