package com.lol.ChampionStatsPojo;
import lombok.Data;

@Data
public class Stats{
    public Health health;
    public HealthRegen healthRegen;
    public Mana mana;
    public ManaRegen manaRegen;
    public Armor armor;
    public MagicResistance magicResistance;
    public AttackDamage attackDamage;
    public Movespeed movespeed;
    public AcquisitionRadius acquisitionRadius;
    public SelectionRadius selectionRadius;
    public PathingRadius pathingRadius;
    public GameplayRadius gameplayRadius;
    public CriticalStrikeDamage criticalStrikeDamage;
    public CriticalStrikeDamageModifier criticalStrikeDamageModifier;
    public AttackSpeed attackSpeed;
    public AttackSpeedRatio attackSpeedRatio;
    public AttackCastTime attackCastTime;
    public AttackTotalTime attackTotalTime;
    public AttackDelayOffset attackDelayOffset;
    public AttackRange attackRange;
    public AramDamageTaken aramDamageTaken;
    public AramDamageDealt aramDamageDealt;
    public AramHealing aramHealing;
    public AramShielding aramShielding;
    public UrfDamageTaken urfDamageTaken;
    public UrfDamageDealt urfDamageDealt;
    public UrfHealing urfHealing;
    public UrfShielding urfShielding;
}
