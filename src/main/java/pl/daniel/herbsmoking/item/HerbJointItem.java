package pl.daniel.herbsmoking.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import pl.daniel.herbsmoking.herb.HerbType;
import java.util.Random;

public class HerbJointItem extends Item {
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
        return UseAnim.DRINK;
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
                net.minecraft.sounds.SoundEvents.FLINTANDSTEEL_USE, net.minecraft.sounds.SoundSource.PLAYERS, 0.4f, 1.3f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.PLAYER_BURP, net.minecraft.sounds.SoundSource.PLAYERS, 0.3f, 0.8f);

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