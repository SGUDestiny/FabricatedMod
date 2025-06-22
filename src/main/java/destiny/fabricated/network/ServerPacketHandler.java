package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

public class ServerPacketHandler
{
    public static void handleFabricatorCraftItem(FabricatorCraftItemPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            ItemEntity itemEntity = new ItemEntity(player.level(), fabricator.getBlockPos().getCenter().x, fabricator.getBlockPos().getCenter().y, fabricator.getBlockPos().getCenter().z, packet.stack);
            player.level().addFreshEntity(itemEntity);
        }
    }
}
