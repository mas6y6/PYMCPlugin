package com.pymc;

import org.bukkit.potion.PotionEffectType;

public class PotionEffectManager {

    public static PotionEffectType getPotionEffectType(String effectName) {
        effectName = effectName.toUpperCase();

        switch (effectName) {
            case "SPEED": return PotionEffectType.SPEED;
            case "SLOWNESS": return PotionEffectType.SLOWNESS;
            case "HASTE": return PotionEffectType.HASTE;
            case "MINING_FATIGUE": return PotionEffectType.MINING_FATIGUE;
            case "STRENGTH": return PotionEffectType.STRENGTH;
            case "INSTANT_HEALTH": return PotionEffectType.INSTANT_HEALTH;
            case "INSTANT_DAMAGE": return PotionEffectType.INSTANT_DAMAGE;
            case "JUMP_BOOST": return PotionEffectType.JUMP_BOOST;
            case "NAUSEA": return PotionEffectType.NAUSEA;
            case "REGENERATION": return PotionEffectType.REGENERATION;
            case "RESISTANCE": return PotionEffectType.RESISTANCE;
            case "FIRE_RESISTANCE": return PotionEffectType.FIRE_RESISTANCE;
            case "WATER_BREATHING": return PotionEffectType.WATER_BREATHING;
            case "INVISIBILITY": return PotionEffectType.INVISIBILITY;
            case "BLINDNESS": return PotionEffectType.BLINDNESS;
            case "NIGHT_VISION": return PotionEffectType.NIGHT_VISION;
            case "HUNGER": return PotionEffectType.HUNGER;
            case "WEAKNESS": return PotionEffectType.WEAKNESS;
            case "POISON": return PotionEffectType.POISON;
            case "WITHER": return PotionEffectType.WITHER;
            case "HEALTH_BOOST": return PotionEffectType.HEALTH_BOOST;
            case "ABSORPTION": return PotionEffectType.ABSORPTION;
            case "SATURATION": return PotionEffectType.SATURATION;
            case "GLOWING": return PotionEffectType.GLOWING;
            case "LEVITATION": return PotionEffectType.LEVITATION;
            case "LUCK": return PotionEffectType.LUCK;
            case "UNLUCK": return PotionEffectType.UNLUCK;
            case "SLOW_FALLING": return PotionEffectType.SLOW_FALLING;
            case "CONDUIT_POWER": return PotionEffectType.CONDUIT_POWER;
            case "DOLPHINS_GRACE": return PotionEffectType.DOLPHINS_GRACE;
            case "BAD_OMEN": return PotionEffectType.BAD_OMEN;
            case "HERO_OF_THE_VILLAGE": return PotionEffectType.HERO_OF_THE_VILLAGE;
            case "DARKNESS": return PotionEffectType.DARKNESS;
            case "TRIAL_OMEN": return PotionEffectType.TRIAL_OMEN;
            case "RAID_OMEN": return PotionEffectType.RAID_OMEN;
            case "WIND_CHARGED": return PotionEffectType.WIND_CHARGED;
            case "WEAVING": return PotionEffectType.WEAVING;
            case "OOZING": return PotionEffectType.OOZING;
            case "INFESTED": return PotionEffectType.INFESTED;
            default: return null; // Handle invalid effect type case
        }
    }
}
