package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundFabricationBatchPacket
{
    public int batch;
    public BlockPos pos;
    public ServerboundFabricationBatchPacket(BlockPos pos, int batch)
    {
        this.pos = pos;
        this.batch = batch;
    }

    public static void write(ServerboundFabricationBatchPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.batch);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundFabricationBatchPacket read(FriendlyByteBuf buffer)
    {
        int step = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundFabricationBatchPacket(pos, step);
    }

    public static void handle(ServerboundFabricationBatchPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorBatchPacket(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
