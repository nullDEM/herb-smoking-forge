package pl.daniel.herbsmoking.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pl.daniel.herbsmoking.herb.HerbType;
import pl.daniel.herbsmoking.item.ModItems;
import java.util.EnumMap;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HerbSmokingMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HerbSmokingMod.MOD_ID);

    public static final Map<HerbType, RegistryObject<HerbCropBlock>> CROPS = new EnumMap<>(HerbType.class);
    public static final Map<HerbType, RegistryObject<Item>> SEEDS = new EnumMap<>(HerbType.class);

    public static final RegistryObject<BongBlock> BONG = BLOCKS.register("bong",
        () -> new BongBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .noOcclusion()));

    public static final RegistryObject<BlockEntityType<BongBlockEntity>> BONG_ENTITY = BLOCK_ENTITIES.register("bong",
        () -> BlockEntityType.Builder.of(BongBlockEntity::new, BONG.get()).build(null));

    public static void register(IEventBus bus) {
        for (HerbType type : HerbType.values()) {
            RegistryObject<HerbCropBlock> crop = BLOCKS.register(type.blockId(),
                () -> new HerbCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion(), type));
            CROPS.put(type, crop);

            RegistryObject<Item> seed = ModItems.ITEMS.register(type.seedsId(),
                () -> new BlockItem(crop.get(), new Item.Properties()));
            SEEDS.put(type, seed);
        }

        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}