package pl.daniel.herbsmoking.herb;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pl.daniel.herbsmoking.block.ModBlocks;
import pl.daniel.herbsmoking.item.ModItems;

@Mod.EventBusSubscriber(modid = HerbSmokingMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HerbRegistry {
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTab.TABS.get(net.minecraft.world.item.CreativeModeTabs.FOOD_AND_DRINKS)) {
            event.accept(ModItems.ROLLING_PAPER.get());
            event.accept(ModItems.HERB_BOWL.get());
            event.accept(ModItems.BONG_ITEM.get());

            for (HerbType type : HerbType.values()) {
                event.accept(ModItems.FRESH.get(type).get());
                event.accept(ModItems.DRIED.get(type).get());
                event.accept(ModItems.JOINTS.get(type).get());
                event.accept(ModBlocks.SEEDS.get(type).get());
            }
        }

        if (event.getTabKey() == CreativeModeTab.TABS.get(net.minecraft.world.item.CreativeModeTabs.NATURAL_BLOCKS)) {
            for (HerbType type : HerbType.values()) {
                event.accept(ModBlocks.CROPS.get(type).get());
            }
        }

        if (event.getTabKey() == CreativeModeTab.TABS.get(net.minecraft.world.item.CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            event.accept(ModItems.BONG_ITEM.get());
        }
    }
}