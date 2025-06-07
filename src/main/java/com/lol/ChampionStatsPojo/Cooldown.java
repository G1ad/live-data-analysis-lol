package com.lol.ChampionStatsPojo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class Cooldown{
    public ArrayList<Modifier> modifiers;
    public boolean affectedByCdr;
}
