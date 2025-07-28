package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerboundFabricatorCraftItemPacket
{
    public ItemStack craftStack;
    public List<ItemStack> outputs;
    public List<ItemStack> ingredients;
    public BlockPos pos;
    public int batch;
    public ServerboundFabricatorCraftItemPacket(BlockPos pos, ItemStack craftStack, List<ItemStack> outputs, List<ItemStack> ingredients, int batch)
    {
        this.pos = pos;
        this.outputs = outputs;
        this.craftStack = craftStack;
        this.ingredients = ingredients;
        this.batch = batch;
    }

    public static void write(ServerboundFabricatorCraftItemPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeItem(packet.craftStack);
        buffer.writeCollection(packet.outputs, FriendlyByteBuf::writeItem);
        buffer.writeCollection(packet.ingredients, FriendlyByteBuf::writeItem);
        buffer.writeBlockPos(packet.pos);
        buffer.writeInt(packet.batch);
    }

    public static ServerboundFabricatorCraftItemPacket read(FriendlyByteBuf buffer)
    {
        ItemStack craftStack = buffer.readItem();
        List<ItemStack> outputs = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        List<ItemStack> ingrediensts = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        BlockPos pos = buffer.readBlockPos();
        int batch = buffer.readInt();

        return new ServerboundFabricatorCraftItemPacket(pos, craftStack, outputs, ingrediensts, batch);
    }

    public static void handle(ServerboundFabricatorCraftItemPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorCraftPrep(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
