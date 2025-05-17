package destiny.fabricated.events;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.client.renderer.block.FabricatorBlockRenderer;
import destiny.fabricated.init.BlockEntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FabricatedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityInit.FABRICATOR.get(), context -> new FabricatorBlockRenderer());
    }
}
