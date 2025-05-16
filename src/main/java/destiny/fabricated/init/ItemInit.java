package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FabricatedMod.MODID);

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }
}

