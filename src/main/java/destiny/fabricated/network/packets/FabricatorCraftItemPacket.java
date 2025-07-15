package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FabricatorCraftItemPacket
{
    public ItemStack result;
    public List<ItemStack> ingredients;
    public BlockPos pos;

    public FabricatorCraftItemPacket(ItemStack item, List<ItemStack> ingredients, BlockPos pos)
    {
        this.result = item;
        this.ingredients = ingredients;
        this.pos = pos;
    }

    public static void write(FabricatorCraftItemPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeItem(packet.result);
        buffer.writeCollection(packet.ingredients, FriendlyByteBuf::writeItem);
        buffer.writeBlockPos(packet.pos);
    }

    public static FabricatorCraftItemPacket read(FriendlyByteBuf buffer)
    {
        ItemStack stack = buffer.readItem();
        List<ItemStack> ingredients = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        BlockPos pos = buffer.readBlockPos();

        return new FabricatorCraftItemPacket(stack, ingredients, pos);
    }

    public static void handle(FabricatorCraftItemPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorCraftItem(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
