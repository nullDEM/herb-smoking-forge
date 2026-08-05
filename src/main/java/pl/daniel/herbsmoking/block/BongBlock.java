package pl.daniel.herbsmoking.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import pl.daniel.herbsmoking.herb.HerbType;
import pl.daniel.herbsmoking.item.HerbJointItem;
import java.util.Random;

public class BongBlock extends BaseEntityBlock {
    public static final BooleanProperty HAS_WATER = BooleanProperty.create("has_water");
    public static final BooleanProperty IS_LIT = BooleanProperty.create("is_lit");

    public BongBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_WATER, false).setValue(IS_LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_WATER, IS_LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BongBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> ((BongBlockEntity) be).tick();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BongBlockEntity bong) {
                bong.drops();
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}

class BongBlockEntity extends BlockEntity implements net.minecraft.world.Container {
    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
    private int smokeTime = 0;
    private int maxSmokeTime = 0;
    private HerbType currentHerb = null;
    private final Random random = new Random();

    public BongBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BONG_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (hasWater() && hasHerb() && !isLit()) {
            startSmoking();
        }

        if (isLit()) {
            smokeTime--;
            if (smokeTime <= 0) {
                finishSmoking();
            }
            setChanged();
        }
    }

    private boolean hasWater() {
        return getBlockState().getValue(BongBlock.HAS_WATER);
    }

    private boolean isLit() {
        return getBlockState().getValue(BongBlock.IS_LIT);
    }

    private boolean hasHerb() {
        return slots.get(0).getItem() instanceof HerbJointItem;
    }

    private void startSmoking() {
        HerbJointItem joint = (HerbJointItem) slots.get(0).getItem();
        currentHerb = joint.getType();
        maxSmokeTime = 100;
        smokeTime = maxSmokeTime;
        level.setBlock(worldPosition, getBlockState().setValue(BongBlock.IS_LIT, true), 3);
    }

    private void finishSmoking() {
        if (currentHerb != null && level instanceof ServerLevel serverLevel) {
            var effect = currentHerb.getRandomEffect(random);
            for (Player player : level.players()) {
                if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 16) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect));
                }
            }

            level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW, net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.2f);
        }

        slots.set(0, ItemStack.EMPTY);
        level.setBlock(worldPosition, getBlockState().setValue(BongBlock.IS_LIT, false), 3);
        currentHerb = null;
        setChanged();
    }

    public void setWater(boolean hasWater) {
        level.setBlock(worldPosition, getBlockState().setValue(BongBlock.HAS_WATER, hasWater), 3);
    }

    @Override
    public int getContainerSize() {
        return slots.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : slots) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return slots.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(slots, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(slots, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        slots.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        slots.clear();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, slots);
        tag.putInt("smokeTime", smokeTime);
        tag.putInt("maxSmokeTime", maxSmokeTime);
        if (currentHerb != null) tag.putString("herb", currentHerb.id);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, slots);
        smokeTime = tag.getInt("smokeTime");
        maxSmokeTime = tag.getInt("maxSmokeTime");
        if (tag.contains("herb")) currentHerb = HerbType.byId(tag.getString("herb"));
    }

    public void drops() {
        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) {
                net.minecraft.world.item.ItemStack itemStack = stack.copy();
                net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemStack);
            }
        }
    }
}