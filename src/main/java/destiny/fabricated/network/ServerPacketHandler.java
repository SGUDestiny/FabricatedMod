package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.ServerboundFabricatorStatePacket;
import destiny.fabricated.network.packets.ServerboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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

    public static void handleFabricatorStatePacket(ServerboundFabricatorStatePacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.state = packet.state;
            fabricator.isOpen = packet.isOpen;
        }
    }

    public static void handleSoundPacket(ServerboundSoundPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.getLevel().playSound(null, fabricator.getBlockPos(), packet.event, SoundSource.BLOCKS);
        }
    }
}
