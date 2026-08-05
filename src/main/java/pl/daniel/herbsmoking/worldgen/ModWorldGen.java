package pl.daniel.herbsmoking.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pl.daniel.herbsmoking.HerbSmokingMod;
import pl.daniel.herbsmoking.block.ModBlocks;
import pl.daniel.herbsmoking.herb.HerbType;

import java.util.List;

public class ModWorldGen {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
        DeferredRegister.create(ForgeRegistries.CONFIGURED_FEATURES, HerbSmokingMod.MOD_ID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
        DeferredRegister.create(ForgeRegistries.PLACED_FEATURES, HerbSmokingMod.MOD_ID);

    public static final Map<HerbType, RegistryObject<ConfiguredFeature<?, ?>>> PATCH_FEATURES = new java.util.EnumMap<>(HerbType.class);
    public static final Map<HerbType, RegistryObject<PlacedFeature>> PLACED_PATCHES = new java.util.EnumMap<>(HerbType.class);

    static {
        for (HerbType type : HerbType.values()) {
            String patchName = type.id + "_patch";
            String placedName = type.id + "_placed";

            PATCH_FEATURES.put(type, CONFIGURED_FEATURES.register(patchName,
                () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                    new RandomPatchConfiguration(4, 3, 2,
                        BlockStateProvider.simple(ModBlocks.CROPS.get(type).get().defaultBlockState())))));

            PLACED_PATCHES.put(type, PLACED_FEATURES.register(placedName,
                () -> new PlacedFeature(
                    net.minecraft.core.registries.Registries.CONFIGURED_FEATURE.getOrThrow(
                        ResourceKey.create(Registries.CONFIGURED_FEATURE,
                            ResourceLocation.fromNamespaceAndPath(HerbSmokingMod.MOD_ID, patchName))),
                    List.of(
                        PlacementModifier.of(net.minecraft.world.level.levelgen.placement.CountPlacementModifier.of(getCount(type))),
                        PlacementModifier.of(net.minecraft.world.level.levelgen.placement.SquarePlacementModifier.of()),
                        PlacementModifier.of(net.minecraft.world.level.levelgen.placement.RandomOffsetPlacementModifier.vertically(getVerticalOffset(type))),
                        PlacementModifier.of(net.minecraft.world.level.levelgen.placement.SurfaceThresholdFilterPlacementModifier.of(getSurfaceThreshold(type))),
                        PlacementModifier.of(net.minecraft.world.level.levelgen.placement.BlockFilterPlacementModifier.of(
                            net.minecraft.block.Blocks.GRASS_BLOCK,
                            net.minecraft.block.Blocks.DIRT,
                            net.minecraft.block.Blocks.PODZOL,
                            type == HerbType.SATIVA ? net.minecraft.block.Blocks.MYCELIUM : net.minecraft.block.Blocks.COARSE_DIRT))
                    )
                )));
        }
    }

    private static int getCount(HerbType type) {
        return switch (type) {
            case INDICA -> 2;
            case SATIVA -> 4;
            case CBD -> 6;
        };
    }

    private static int getVerticalOffset(HerbType type) {
        return switch (type) {
            case INDICA -> 1;
            case SATIVA -> 2;
            case CBD -> 1;
        };
    }

    private static float getSurfaceThreshold(HerbType type) {
        return switch (type) {
            case INDICA -> 0.5f;
            case SATIVA -> 0.3f;
            case CBD -> 0.4f;
        };
    }

    public static void register(IEventBus bus) {
        CONFIGURED_FEATURES.register(bus);
        PLACED_FEATURES.register(bus);

        bus.addListener(event -> {
            if (event.getRegistries().containsKey(Registries.BIOME)) {
                HolderGetter<net.minecraft.world.level.biome.Biome> biomes = event.getRegistries().getOrThrow(Registries.BIOME);
                for (HerbType type : HerbType.values()) {
                    ForgeBiomeModifiers.addSpawns(
                        net.minecraftforge.common.ForgeMod.OVERWORLD_BIOMES.get(),
                        PLACED_PATCHES.get(type).getKey().orElseThrow(),
                        net.minecraftforge.common.Tags.Biomes.IS_OVERWORLD);
                }
            }
        });
    }
}