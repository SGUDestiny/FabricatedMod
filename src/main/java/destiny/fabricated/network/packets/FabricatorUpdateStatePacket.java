package destiny.fabricated.network.packets;

import destiny.fabricated.network.ClientPacketHandler;
import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FabricatorUpdateStatePacket
{
    public BlockPos pos;
    public int state;
    public boolean open;

    public FabricatorUpdateStatePacket(BlockPos pos, int state, boolean open)
    {
        this.pos = pos;
        this.state = state;
        this.open = open;
    }

    public static void write(FabricatorUpdateStatePacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.state);
        buffer.writeBoolean(packet.open);
        buffer.writeBlockPos(packet.pos);
    }

    public static FabricatorUpdateStatePacket read(FriendlyByteBuf buffer)
    {
        int open = buffer.readInt();
        boolean opened = buffer.readBoolean();
        BlockPos pos = buffer.readBlockPos();

        return new FabricatorUpdateStatePacket(pos, open, opened);
    }

    public static void handle(FabricatorUpdateStatePacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ClientPacketHandler.handleFabricatorUpdateState(packet));
        context.get().setPacketHandled(true);
    }
}
