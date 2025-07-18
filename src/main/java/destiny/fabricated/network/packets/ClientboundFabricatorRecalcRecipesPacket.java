package destiny.fabricated.network.packets;

import destiny.fabricated.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundFabricatorRecalcRecipesPacket
{
    public ClientboundFabricatorRecalcRecipesPacket()
    {

    }

    public static void write(ClientboundFabricatorRecalcRecipesPacket packet, FriendlyByteBuf buffer)
    {

    }

    public static ClientboundFabricatorRecalcRecipesPacket read(FriendlyByteBuf buffer)
    {

        return new ClientboundFabricatorRecalcRecipesPacket();
    }

    public static void handle(ClientboundFabricatorRecalcRecipesPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ClientPacketHandler.handleFabricatorMenuChange(packet));
        context.get().setPacketHandled(true);
    }
}
