package destiny.fabricated.network;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.menu.FabricatorBrowserCraftingMenu;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.network.packets.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
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
            fabricator.batchValue = packet.batch;
            if(ServerPacketHandler.consumeItemsFromInventory(player, packet.ingredients, packet.batch))
            {
                fabricator.craftStack = packet.craftStack;
                fabricator.outputs = packet.outputs;
            }
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
            if(packet.anim.equals("close") && !packet.closeAfterCraft)
                level.setBlock(pos, state.setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.CLOSED), 2);
            if(packet.anim.equals("fabricate"))
                level.setBlock(pos, state.setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.FABRICATING), 2);

            if(!packet.closeAfterCraft)
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
            if(player.containerMenu instanceof FabricatorCraftingMenu menu)
                menu.switching = true;
            if(player.containerMenu instanceof FabricatorBrowserCraftingMenu menu)
                menu.switching = true;

            packet.getMenu(fabricator).ifPresent(menu -> NetworkHooks.openScreen(player,
                    new SimpleMenuProvider(menu, fabricator.getBlockState().getBlock().getName()),
                    buf -> {
                buf.writeBlockPos(fabricator.getBlockPos());
                buf.writeInt(packet.type);
                buf.writeInt(packet.target);
                    }));

            NetworkInit.sendToTracking(player, new ClientboundFabricatorRecalcRecipesPacket(ItemStack.EMPTY));

            fabricator.triggerAnim("main", "open_idle");
            player.level().setBlock(packet.pos, fabricator.getBlockState().setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.OPEN), 2);
        }
    }

    public static void handleSoundPacket(ServerboundSoundPacket packet, ServerPlayer player)
    {
        if(player.level().getBlockEntity(packet.pos) instanceof FabricatorBlockEntity fabricator)
        {
            fabricator.getLevel().playSound(null, fabricator.getBlockPos(), packet.event, SoundSource.BLOCKS);
        }
    }

    public static boolean consumeItemsFromInventory(ServerPlayer player, List<ItemStack> requiredItems, int batchValue) {
        List<Boolean> allTaken = new ArrayList<>();
        if(requiredItems.isEmpty())
            return false;

        for (ItemStack stack : requiredItems) {
            if (stack.isEmpty()) continue;

            ItemStack required = stack.copyWithCount(stack.getCount() * batchValue);

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
