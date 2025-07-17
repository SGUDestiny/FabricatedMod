package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundFabricatorAnimPacket
{
    public String anim;
    public BlockPos pos;
    public ServerboundFabricatorAnimPacket(BlockPos pos, String anim)
    {
        this.pos = pos;
        this.anim = anim;
    }

    public static void write(ServerboundFabricatorAnimPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeUtf(packet.anim);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundFabricatorAnimPacket read(FriendlyByteBuf buffer)
    {
        String anim = buffer.readUtf();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundFabricatorAnimPacket(pos, anim);
    }

    public static void handle(ServerboundFabricatorAnimPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorAnimPacket(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
