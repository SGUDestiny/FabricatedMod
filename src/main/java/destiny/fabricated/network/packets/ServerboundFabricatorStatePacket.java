package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ServerboundFabricatorStatePacket
{
    public int state;
    public boolean isOpen;
    public BlockPos pos;
    public ServerboundFabricatorStatePacket(BlockPos pos, int state, boolean open)
    {
        this.pos = pos;
        this.state = state;
        this.isOpen = open;
    }

    public static void write(ServerboundFabricatorStatePacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.state);
        buffer.writeBoolean(packet.isOpen);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundFabricatorStatePacket read(FriendlyByteBuf buffer)
    {
        int state = buffer.readInt();
        boolean open = buffer.readBoolean();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundFabricatorStatePacket(pos, state, open);
    }

    public static void handle(ServerboundFabricatorStatePacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorStatePacket(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
