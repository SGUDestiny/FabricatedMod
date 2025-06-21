package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

public class ServerPacketHandler
{
    public static void handleFabricatorCraftItem(FabricatorCraftItemPacket packet, ServerPlayer player)
    {
        ItemEntity item = new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z,
                packet.stack);
        player.level().addFreshEntity(item);

        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.fabricate(player.level(), packet.pos, fabricator);
        }
    }

    public static void handleFabricatorStateUpdate(FabricatorUpdateStatePacket packet, ServerPlayer player)
    {

    }
}
