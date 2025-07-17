package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.network.packets.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketHandler
{
    public static void handleFabricatorCraftPrep(ServerboundFabricatorCraftItemPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            if(ServerPacketHandler.consumeItemsFromInventory(player, packet.ingredients, fabricator.batchValue))
                fabricator.craftStack = packet.craftStack;
        }
    }

    public static void handleFabricatorAnimPacket(ServerboundFabricatorAnimPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            Level level = player.level();
            BlockState state = fabricator.getBlockState();
            BlockPos pos = fabricator.getBlockPos();

            if(packet.anim.equals("open"))
                level.setBlock(pos, state.setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.OPEN), 2);
            if(packet.anim.equals("close"))
                level.setBlock(pos, state.setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.CLOSED), 2);
            if(packet.anim.equals("fabricate"))
                level.setBlock(pos, state.setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.FABRICATING), 2);

            fabricator.triggerAnim("main", packet.anim);
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
                    buf -> {
                buf.writeBlockPos(fabricator.getBlockPos());
                buf.writeInt(packet.type);
                buf.writeInt(packet.target);
                    }));
        }
    }

    public static void handleSoundPacket(ServerboundSoundPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.getLevel().playSound(null, fabricator.getBlockPos(), packet.event, SoundSource.BLOCKS);
        }
    }

    public static boolean consumeItemsFromInventory(ServerPlayer player, List<ItemStack> requiredItems, int batch) {
        List<Boolean> allTaken = new ArrayList<>();
        for (ItemStack required : requiredItems) {
            if (required.isEmpty()) continue;

            int remaining = required.getCount()*batch;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);

                if (!invStack.isEmpty() && ItemStack.isSameItem(invStack, required)) {
                    int toRemove = Math.min(remaining, invStack.getCount());

                    invStack.shrink(toRemove);
                    if (invStack.isEmpty()) {
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                    }

                    remaining -= toRemove;
                    if (remaining <= 0)
                    {
                        allTaken.add(true);
                        break;
                    }
                }
            }
        }
        return allTaken.stream().allMatch(bool -> bool);
    }
}
