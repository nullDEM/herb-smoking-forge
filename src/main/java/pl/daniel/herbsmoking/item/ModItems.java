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
import net.minecraft.world.item.CreativeModeTab;
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
import pl.daniel.herbsmoking.block.BongBlock;
import pl.daniel.herbsmoking.block.BongBlockEntity;
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
                    if (state.getBlock() instanceof BongBlock && !state.getValue(BongBlock.HAS_WATER)) {
                        serverLevel.setBlock(pos, state.setValue(BongBlock.HAS_WATER, true), 3);
                        context.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            net.minecraft.sound.SoundEvents.BUCKET_EMPTY, net.minecraft.sound.SoundSource.BLOCKS, 1.0f, 1.0f);
                        context.getItemInHand().shrink(1);
                    }
                }
                return result;
            }
        });

    public static final RegistryObject<Item> HERB_BOWL = ITEMS.register("herb_bowl",
        () -> new Item(new Item.Properties().stacksTo(16)) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.herbsmoking.herb_bowl.desc").withStyle(ChatFormatting.GRAY));
            }
        });

    static {
        Item.Properties foodFresh = new Item.Properties().stacksTo(64).food(new Item.Properties().food(
            new net.minecraft.world.food.FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build()));
        Item.Properties foodDried = new Item.Properties().stacksTo(64).food(new Item.Properties().food(
            new net.minecraft.world.food.FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build()));
        Item.Properties foodJoint = new Item.Properties().stacksTo(16).food(new Item.Properties().food(
            new net.minecraft.world.food.FoodProperties.Builder().nutrition(0).saturationMod(0f).build()));
    }

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

class HerbJointItem extends Item {
    private static final Random RANDOM = new Random();
    private final HerbType type;

    public HerbJointItem(HerbType type) {
        super(new Item.Properties().stacksTo(16).food(
            new net.minecraft.world.food.FoodProperties.Builder().nutrition(0).saturationMod(0f).build()));
        this.type = type;
    }

    public HerbType getType() { return type; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SMOKE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide) {
            var effect = type.getRandomEffect(RANDOM);
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect));

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sound.SoundEvents.FLINTANDSTEEL_USE, net.minecraft.sound.SoundSource.PLAYERS, 0.4f, 1.3f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sound.SoundEvents.PLAYER_BURP, net.minecraft.sound.SoundSource.PLAYERS, 0.3f, 0.8f);

            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == HerbType.CBD;
    }
}