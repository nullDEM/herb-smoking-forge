package pl.daniel.herbsmoking.herb;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
            new EffectEntry(ModEffects.RELAXATION, 600, 1),
            new EffectEntry(ModEffects.LEVITATION_HERB, 60, 0),
            new EffectEntry(ModEffects.NIGHT_VISION_HERB, 300, 0),
            new EffectEntry(ModEffects.MUNCHIES, 900, 1)
        )
    ),
    SATIVA(
        "sativa",
        0x7FC24D,
        "Sativa",
        "Energetyczna, kreatywna",
        List.of(
            new EffectEntry(ModEffects.SPEED_HERB, 300, 1),
            new EffectEntry(ModEffects.NIGHT_VISION_HERB, 180, 0),
            new EffectEntry(ModEffects.RELAXATION, 120, 0),
            new EffectEntry(ModEffects.LEVITATION_HERB, 20, 1)
        )
    ),
    CBD(
        "cbd",
        0xA8D5BA,
        "CBD",
        "Medycyna, bez psychoaktywności",
        List.of(
            new EffectEntry(ModEffects.RELAXATION, 1200, 0),
            new EffectEntry(ModEffects.NIGHT_VISION_HERB, 600, 0),
            new EffectEntry(ModEffects.SPEED_HERB, 60, 0)
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
        return new MobEffectInstance(entry.effect, entry.durationSeconds * 20, entry.amplifier);
    }

    public record EffectEntry(MobEffect effect, int durationSeconds, int amplifier) {}

    public String itemSuffix() { return "_" + id; }
    public String blockId() { return "herb_crop_" + id; }
    public String seedsId() { return "herb_seeds_" + id; }
    public String freshId() { return "fresh_herb_" + id; }
    public String driedId() { return "dried_herb_" + id; }
    public String cigaretteId() { return "herb_joint_" + id; }
    public String bongId() { return "bong_" + id; }
}