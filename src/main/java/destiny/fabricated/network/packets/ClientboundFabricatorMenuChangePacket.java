package destiny.fabricated.network.packets;

import destiny.fabricated.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundFabricatorMenuChangePacket
{
    public ClientboundFabricatorMenuChangePacket()
    {

    }

    public static void write(ClientboundFabricatorMenuChangePacket packet, FriendlyByteBuf buffer)
    {

    }

    public static ClientboundFabricatorMenuChangePacket read(FriendlyByteBuf buffer)
    {

        return new ClientboundFabricatorMenuChangePacket();
    }

    public static void handle(ClientboundFabricatorMenuChangePacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ClientPacketHandler.handleFabricatorMenuChange(packet));
        context.get().setPacketHandled(true);
    }
}
