package com.lol.ChampionStatsPojo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class W{
    public String name;
    public String icon;
    public ArrayList<Effect> effects;
    public Object cost;
    public Cooldown cooldown;
    public String targeting;
    public String affects;
    public String spellshieldable;
    public Object resource;
    public String damageType;
    public String spellEffects;
    public String projectile;
    public Object onHitEffects;
    public Object occurrence;
    public String notes;
    public String blurb;
    public Object missileSpeed;
    public Object rechargeRate;
    public Object collisionRadius;
    public Object tetherRadius;
    public Object onTargetCdStatic;
    public Object innerRadius;
    public String speed;
    public String width;
    public Object angle;
    public String castTime;
    public Object effectRadius;
    public Object targetRange;
}
