package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.items.FabricatorBlockItem;
import destiny.fabricated.items.FabricatorChipItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FabricatedMod.MODID);

    public static final RegistryObject<FabricatorBlockItem> FABRICATOR = ITEMS.register("fabricator", () -> new FabricatorBlockItem(new Item.Properties()));
    public static final RegistryObject<FabricatorChipItem> FABRICATOR_CHIP = ITEMS.register("fabricator_chip", () -> new FabricatorChipItem(new Item.Properties()));

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }
}

