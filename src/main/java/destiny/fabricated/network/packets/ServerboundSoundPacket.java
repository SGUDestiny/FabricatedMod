package destiny.fabricated.network.packets;

import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ServerboundSoundPacket
{
    public SoundEvent event;
    public BlockPos pos;
    public ServerboundSoundPacket(BlockPos pos, SoundEvent event)
    {
        this.pos = pos;
        this.event = event;
    }

    public static void write(ServerboundSoundPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeRegistryId(ForgeRegistries.SOUND_EVENTS, packet.event);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundSoundPacket read(FriendlyByteBuf buffer)
    {
        SoundEvent event = buffer.readRegistryId();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundSoundPacket(pos, event);
    }

    public static void handle(ServerboundSoundPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleSoundPacket(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
