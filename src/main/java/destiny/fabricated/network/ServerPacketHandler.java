package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.ServerboundFabricatorStatePacket;
import destiny.fabricated.network.packets.ServerboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ServerPacketHandler
{
    public static void handleFabricatorCraftItem(FabricatorCraftItemPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            ItemEntity itemEntity = new ItemEntity(player.level(), fabricator.getBlockPos().getCenter().x, fabricator.getBlockPos().getCenter().y, fabricator.getBlockPos().getCenter().z, packet.result);
            player.level().addFreshEntity(itemEntity);

            ServerPacketHandler.consumeItemsFromInventory(player, packet.ingredients);
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

    public static void consumeItemsFromInventory(ServerPlayer player, List<ItemStack> requiredItems) {
        for (ItemStack required : requiredItems) {
            if (required.isEmpty()) continue;

            int remaining = required.getCount();

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);

                if (!invStack.isEmpty() && ItemStack.isSameItem(invStack, required)) {
                    int toRemove = Math.min(remaining, invStack.getCount());

                    invStack.shrink(toRemove);
                    if (invStack.isEmpty()) {
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                    }

                    remaining -= toRemove;
                    if (remaining <= 0) break;
                }
            }
        }
    }
}
