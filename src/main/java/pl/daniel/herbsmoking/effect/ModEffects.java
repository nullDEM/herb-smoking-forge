package pl.daniel.herbsmoking.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final String MOD_ID = "herbsmoking";
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    public static final RegistryObject<MobEffect> RELAXATION = MOB_EFFECTS.register("relaxation",
        () -> new HerbEffect(MobEffectCategory.BENEFICIAL, 0x4B8B3B)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "herb_relaxation_speed", -0.15, AttributeModifier.Operation.ADDITION)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "herb_relaxation_attack", -0.2, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> MUNCHIES = MOB_EFFECTS.register("munchies",
        () -> new HerbEffect(MobEffectCategory.NEUTRAL, 0xFF8C00)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "herb_munchies_speed", -0.05, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> SPEED_HERB = MOB_EFFECTS.register("speed_herb",
        () -> new HerbEffect(MobEffectCategory.BENEFICIAL, 0x7FC24D)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "herb_speed_boost", 0.2, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> LEVITATION_HERB = MOB_EFFECTS.register("levitation_herb",
        () -> new HerbEffect(MobEffectCategory.NEUTRAL, 0xE0E0E0)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "herb_levitation_speed", 0.0, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> NIGHT_VISION_HERB = MOB_EFFECTS.register("night_vision_herb",
        () -> new HerbEffect(MobEffectCategory.BENEFICIAL, 0x00FFFF));

    public static void register(net.minecraftforge.eventbus.api.IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }

    public static class HerbEffect extends MobEffect {
        protected HerbEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}