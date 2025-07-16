package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundFabricationStepPacket
{
    public int step;
    public BlockPos pos;
    public ServerboundFabricationStepPacket(BlockPos pos, int step)
    {
        this.pos = pos;
        this.step = step;
    }

    public static void write(ServerboundFabricationStepPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.step);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundFabricationStepPacket read(FriendlyByteBuf buffer)
    {
        int step = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundFabricationStepPacket(pos, step);
    }

    public static void handle(ServerboundFabricationStepPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorStepPacket(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
