package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FabricatorCraftItemPacket
{
    public ItemStack stack;
    public BlockPos pos;

    public FabricatorCraftItemPacket(ItemStack item, BlockPos pos)
    {
        this.stack = item;
        this.pos = pos;
    }

    public static void write(FabricatorCraftItemPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeItem(packet.stack);
        buffer.writeBlockPos(packet.pos);
    }

    public static FabricatorCraftItemPacket read(FriendlyByteBuf buffer)
    {
        ItemStack stack = buffer.readItem();
        BlockPos pos = buffer.readBlockPos();

        return new FabricatorCraftItemPacket(stack, pos);
    }

    public static void handle(FabricatorCraftItemPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorCraftItem(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
