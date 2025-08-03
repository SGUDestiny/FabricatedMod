package destiny.fabricated;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import destiny.fabricated.compatability.create.CreateCompatability;
import destiny.fabricated.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FabricatedMod.MODID)
public class FabricatedMod
{
    public static final String MODID = "fabricated";

    private static final Logger LOGGER = LogUtils.getLogger();


    public FabricatedMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);
        ItemTabInit.register(modEventBus);
        BlockEntityInit.register(modEventBus);
        MenuInit.register(modEventBus);
        SoundInit.register(modEventBus);
        FabricationInit.register(modEventBus);

        if(ModList.get().isLoaded(Create.ID))
            CreateCompatability.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(NetworkInit::registerPackets);
    }
}
