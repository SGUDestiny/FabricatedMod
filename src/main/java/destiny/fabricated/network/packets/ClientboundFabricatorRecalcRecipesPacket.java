package destiny.fabricated.network.packets;

import destiny.fabricated.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundFabricatorRecalcRecipesPacket
{
    public ItemStack pickedUp;

    public ClientboundFabricatorRecalcRecipesPacket(ItemStack stack)
    {
        this.pickedUp = stack;
    }

    public static void write(ClientboundFabricatorRecalcRecipesPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeItem(packet.pickedUp);
    }

    public static ClientboundFabricatorRecalcRecipesPacket read(FriendlyByteBuf buffer)
    {
        ItemStack stack = buffer.readItem();
        return new ClientboundFabricatorRecalcRecipesPacket(stack);
    }

    public static void handle(ClientboundFabricatorRecalcRecipesPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ClientPacketHandler.handleFabricatorMenuChange(packet));
        context.get().setPacketHandled(true);
    }
}
