package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPacketHandler
{
    public static void handleFabricatorUpdateState(FabricatorUpdateStatePacket packet)
    {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.pos);
        if(blockEntity instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.state = packet.state;
            if(packet.state == 3)
                fabricator.getLevel().playSound(Minecraft.getInstance().player, fabricator.getBlockPos(), SoundInit.FABRICATOR_FABRICATE.get(), SoundSource.BLOCKS);
        }
    }
}
