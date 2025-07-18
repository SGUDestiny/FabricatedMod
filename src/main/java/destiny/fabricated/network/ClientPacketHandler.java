package destiny.fabricated.network;

import destiny.fabricated.client.screen.FabricatorBrowserCraftScreen;
import destiny.fabricated.client.screen.FabricatorCraftScreen;
import destiny.fabricated.network.packets.ClientboundFabricatorRecalcRecipesPacket;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler
{
    public static void handleFabricatorMenuChange(ClientboundFabricatorRecalcRecipesPacket packet)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null && minecraft.player != null)
        {
            if(minecraft.screen instanceof FabricatorCraftScreen screen)
                screen.recipeStuff(screen.selectedTypeKey);
            if(minecraft.screen instanceof FabricatorBrowserCraftScreen screen)
                screen.recipeStuff(screen.selectedTypeKey);
        }
    }
}
