package destiny.fabricated.events;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.client.screen.FabricatorBrowserCraftScreen;
import destiny.fabricated.client.screen.FabricatorCraftScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FabricatedMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents
{
    @SubscribeEvent
    public static void pickupItem(PlayerEvent.ItemPickupEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null && minecraft.level.isClientSide())
        {
            if(minecraft.screen instanceof FabricatorCraftScreen screen)
                screen.recipeStuff(screen.selectedTypeKey);
            if(minecraft.screen instanceof FabricatorBrowserCraftScreen screen)
                screen.recipeStuff(screen.selectedTypeKey);
        }
    }
}
