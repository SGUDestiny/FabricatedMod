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
    public boolean open;

    public FabricatorUpdateStatePacket(BlockPos pos, boolean open)
    {
        this.pos = pos;
        this.open = open;
    }

    public static void write(FabricatorUpdateStatePacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(packet.open);
        buffer.writeBlockPos(packet.pos);
    }

    public static FabricatorUpdateStatePacket read(FriendlyByteBuf buffer)
    {
        boolean open = buffer.readBoolean();
        BlockPos pos = buffer.readBlockPos();

        return new FabricatorUpdateStatePacket(pos, open);
    }

    public static void handle(FabricatorUpdateStatePacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ClientPacketHandler.handleFabricatorUpdateState(packet));
        context.get().setPacketHandled(true);
    }
}
