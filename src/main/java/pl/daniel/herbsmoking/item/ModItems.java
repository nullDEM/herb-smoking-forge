package pl.daniel.herbsmoking.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pl.daniel.herbsmoking.HerbSmokingMod;
import pl.daniel.herbsmoking.block.ModBlocks;
import pl.daniel.herbsmoking.herb.HerbType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HerbSmokingMod.MOD_ID);

    public static final Map<HerbType, RegistryObject<Item>> FRESH = new EnumMap<>(HerbType.class);
    public static final Map<HerbType, RegistryObject<Item>> DRIED = new EnumMap<>(HerbType.class);
    public static final Map<HerbType, RegistryObject<HerbJointItem>> JOINTS = new EnumMap<>(HerbType.class);

    public static final RegistryObject<Item> ROLLING_PAPER = ITEMS.register("rolling_paper",
        () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BONG_ITEM = ITEMS.register("bong",
        () -> new BlockItem(ModBlocks.BONG.get(), new Item.Properties().stacksTo(1)) {
            @Override
            public InteractionResult useOn(UseOnContext context) {
                InteractionResult result = super.useOn(context);
                if (result.consumesAction() && context.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                    BlockState state = serverLevel.getBlockState(pos);
                    if (state.getBlock() instanceof pl.daniel.herbsmoking.block.BongBlock && !state.getValue(pl.daniel.herbsmoking.block.BongBlock.HAS_WATER)) {
                        serverLevel.setBlock(pos, state.setValue(pl.daniel.herbsmoking.block.BongBlock.HAS_WATER, true), 3);
                        context.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            net.minecraft.sounds.SoundEvents.BUCKET_EMPTY, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                        context.getItemInHand().shrink(1);
                    }
                }
                return result;
            }
        });

    public static final RegistryObject<Item> HERB_BOWL = ITEMS.register("herb_bowl",
        () -> new Item(new Item.Properties().stacksTo(16)));

    public static void register(IEventBus bus) {
        for (HerbType type : HerbType.values()) {
            FRESH.put(type, ITEMS.register(type.freshId(),
                () -> new Item(new Item.Properties().stacksTo(64).food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build()))));

            DRIED.put(type, ITEMS.register(type.driedId(),
                () -> new Item(new Item.Properties().stacksTo(64).food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build()))));

            JOINTS.put(type, ITEMS.register(type.cigaretteId(),
                () -> new HerbJointItem(type)));
        }

        ITEMS.register(bus);
    }
}