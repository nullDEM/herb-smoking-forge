package pl.daniel.herbsmoking;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.daniel.herbsmoking.block.ModBlocks;
import pl.daniel.herbsmoking.item.ModItems;
import pl.daniel.herbsmoking.effect.ModEffects;
import pl.daniel.herbsmoking.herb.HerbRegistry;
import pl.daniel.herbsmoking.worldgen.ModWorldGen;

@Mod(HerbSmokingMod.MOD_ID)
public class HerbSmokingMod {
    public static final String MOD_ID = "herbsmoking";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public HerbSmokingMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEffects.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        HerbRegistry.registerAll();
        ModWorldGen.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Herb Smoking Mod (Forge 1.20.1) loaded! 3 variants + bong + joints");
    }

    public static net.minecraft.resources.ResourceLocation id(String path) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}