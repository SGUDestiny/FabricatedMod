package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.items.FabricatorBlockItem;
import destiny.fabricated.items.FabricatorBulkModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FabricatedMod.MODID);

    public static final RegistryObject<FabricatorBlockItem> FABRICATOR = ITEMS.register("fabricator", () -> new FabricatorBlockItem(new Item.Properties()));
    public static final RegistryObject<FabricatorRecipeModuleItem> FABRICATOR_RECIPE_MODULE = ITEMS.register("fabricator_recipe_module", () -> new FabricatorRecipeModuleItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<FabricatorBulkModuleItem> FABRICATOR_BULK_MODULE_1 = ITEMS.register("fabricator_bulk_module_1", () -> new FabricatorBulkModuleItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FabricatorBulkModuleItem> FABRICATOR_BULK_MODULE_2 = ITEMS.register("fabricator_bulk_module_2", () -> new FabricatorBulkModuleItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FabricatorBulkModuleItem> FABRICATOR_BULK_MODULE_3 = ITEMS.register("fabricator_bulk_module_3", () -> new FabricatorBulkModuleItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MOD_LOGO = ITEMS.register("mod_logo", () -> new Item(new Item.Properties()));

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }
}

