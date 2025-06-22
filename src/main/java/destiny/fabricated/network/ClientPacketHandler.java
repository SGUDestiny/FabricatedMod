package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPacketHandler
{
    public static void handleFabricatorUpdateState(FabricatorUpdateStatePacket packet)
    {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.pos);
        if(blockEntity instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.state = packet.state;
        }
    }
}
