package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.network.packets.*;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class ServerPacketHandler
{
    public static void handleFabricatorCraftItem(FabricatorCraftItemPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            ItemStack result = packet.result.copyWithCount(packet.result.getCount()*fabricator.batchValue);

            ItemEntity itemEntity = new ItemEntity(player.level(), fabricator.getBlockPos().getCenter().x, fabricator.getBlockPos().getCenter().y, fabricator.getBlockPos().getCenter().z, result);
            player.level().addFreshEntity(itemEntity);

            for (int i = 0; i < fabricator.batchValue; i++)
                ServerPacketHandler.consumeItemsFromInventory(player, packet.ingredients);
        }
    }

    public static void handleFabricatorStatePacket(ServerboundFabricatorStatePacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.state = packet.state;
            fabricator.isOpen = packet.isOpen;
            fabricator.setChanged();
        }
    }

    public static void handleFabricatorStepPacket(ServerboundFabricationStepPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.fabricationStep = packet.step;
        }
    }

    public static void handleFabricatorBatchPacket(ServerboundFabricationBatchPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.batchValue = packet.batch;
        }
    }

    public static void handleFabricatorMenuChange(ServerboundBrowserMenuPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            packet.getMenu(fabricator).ifPresent(menu -> NetworkHooks.openScreen(player,
                    new SimpleMenuProvider(menu, fabricator.getBlockState().getBlock().getName()),
                    fabricator.getBlockPos()));

            fabricator.state = 4;
            fabricator.isOpen = true;
            NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(fabricator.getBlockPos(), 4, true));
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
