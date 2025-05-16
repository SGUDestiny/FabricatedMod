package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FabricatedMod.MODID);

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
    }
}
