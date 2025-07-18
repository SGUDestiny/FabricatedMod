package destiny.fabricated.events;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.client.screen.FabricatorBrowserCraftScreen;
import destiny.fabricated.client.screen.FabricatorCraftScreen;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.network.packets.ClientboundFabricatorRecalcRecipesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FabricatedMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents
{
    @SubscribeEvent
    public static void pickupItem(PlayerEvent.ItemPickupEvent event)
    {
        if (!event.getEntity().level().isClientSide())
            NetworkInit.sendTo((ServerPlayer) event.getEntity(), new ClientboundFabricatorRecalcRecipesPacket(event.getStack()));
    }
}
