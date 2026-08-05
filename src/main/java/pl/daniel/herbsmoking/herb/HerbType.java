package pl.daniel.herbsmoking.herb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum HerbType {
    INDICA(
        "indica",
        0x4B8B3B,
        "Indica",
        "Relaksująca, usypiająca",
        List.of(
            new EffectEntry("herbsmoking:relaxation", 600, 1),
            new EffectEntry("herbsmoking:levitation_herb", 60, 0),
            new EffectEntry("herbsmoking:night_vision_herb", 300, 0),
            new EffectEntry("herbsmoking:munchies", 900, 1)
        )
    ),
    SATIVA(
        "sativa",
        0x7FC24D,
        "Sativa",
        "Energetyczna, kreatywna",
        List.of(
            new EffectEntry("herbsmoking:speed_herb", 300, 1),
            new EffectEntry("herbsmoking:night_vision_herb", 180, 0),
            new EffectEntry("herbsmoking:relaxation", 120, 0),
            new EffectEntry("herbsmoking:levitation_herb", 20, 1)
        )
    ),
    CBD(
        "cbd",
        0xA8D5BA,
        "CBD",
        "Medycyna, bez psychoaktywności",
        List.of(
            new EffectEntry("herbsmoking:relaxation", 1200, 0),
            new EffectEntry("herbsmoking:night_vision_herb", 600, 0),
            new EffectEntry("herbsmoking:speed_herb", 60, 0)
        )
    );

    public final String id;
    public final int color;
    public final String displayName;
    public final String description;
    public final List<EffectEntry> effectPool;

    HerbType(String id, int color, String displayName, String description, List<EffectEntry> effectPool) {
        this.id = id;
        this.color = color;
        this.displayName = displayName;
        this.description = description;
        this.effectPool = effectPool;
    }

    public static HerbType byId(String id) {
        return Arrays.stream(values()).filter(t -> t.id.equals(id)).findFirst().orElse(INDICA);
    }

    public MobEffectInstance getRandomEffect(Random random) {
        EffectEntry entry = effectPool.get(random.nextInt(effectPool.size()));
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(entry.effectId()));
        if (effect != null) {
            return new MobEffectInstance(effect, entry.durationSeconds * 20, entry.amplifier);
        }
        return null;
    }

    public record EffectEntry(String effectId, int durationSeconds, int amplifier) {}

    public String itemSuffix() { return "_" + id; }
    public String blockId() { return "herb_crop_" + id; }
    public String seedsId() { return "herb_seeds_" + id; }
    public String freshId() { return "fresh_herb_" + id; }
    public String driedId() { return "dried_herb_" + id; }
    public String cigaretteId() { return "herb_joint_" + id; }
    public String bongId() { return "bong_" + id; }
}