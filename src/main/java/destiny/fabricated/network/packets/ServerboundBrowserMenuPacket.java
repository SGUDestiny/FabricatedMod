package destiny.fabricated.network.packets;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.menu.FabricatorBrowserCraftingMenu;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.network.ServerPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerboundBrowserMenuPacket
{
    public boolean openBrowser;
    public int target;
    public int type;
    public BlockPos pos;

    public ServerboundBrowserMenuPacket(boolean openBrowser, int target, int type, BlockPos pos)
    {
        this.openBrowser = openBrowser;
        this.target = target;
        this.type = type;
        this.pos = pos;
    }

    public static void write(ServerboundBrowserMenuPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(packet.openBrowser);
        buffer.writeInt(packet.target);
        buffer.writeInt(packet.type);
        buffer.writeBlockPos(packet.pos);
    }

    public static ServerboundBrowserMenuPacket read(FriendlyByteBuf buffer)
    {
        boolean openBrowser = buffer.readBoolean();
        int target = buffer.readInt();
        int type = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();

        return new ServerboundBrowserMenuPacket(openBrowser, target, type, pos);
    }

    public static void handle(ServerboundBrowserMenuPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ServerPacketHandler.handleFabricatorMenuChange(packet, context.get().getSender()));
        context.get().setPacketHandled(true);
    }

    public Optional<MenuConstructor> getMenu(FabricatorBlockEntity fabricator)
    {
        if(this.openBrowser)
            return Optional.of((window, inventory, player) -> new FabricatorBrowserCraftingMenu(window, inventory, fabricator, this.type, this.target));
        else
            return Optional.of((window, inventory, player) -> new FabricatorCraftingMenu(window, inventory, fabricator, this.type, this.target));
    }
}
