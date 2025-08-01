package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.menu.FabricatorBrowserCraftingMenu;
import destiny.fabricated.menu.FabricatorUpgradesMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit
{
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FabricatedMod.MODID);

    public static final RegistryObject<MenuType<FabricatorUpgradesMenu>> FABRICATOR_UPGRADES =
            registerMenuType(FabricatorUpgradesMenu::new, "fabricator_upgrades");
    public static final RegistryObject<MenuType<FabricatorCraftingMenu>> FABRICATOR_CRAFTING =
            registerMenuType(FabricatorCraftingMenu::new, "fabricator_crafting");
    public static final RegistryObject<MenuType<FabricatorBrowserCraftingMenu>> FABRICATOR_BROWSER =
            registerMenuType(FabricatorBrowserCraftingMenu::new, "fabricator_paged_crafting");


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
            IContainerFactory<T> factory, String name)
    {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus bus)
    {
        MENUS.register(bus);
    }
}
