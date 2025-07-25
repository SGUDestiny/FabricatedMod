package destiny.fabricated.events;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.client.renderer.block.FabricatorBlockRenderer;
import destiny.fabricated.client.screen.FabricatorBrowserCraftScreen;
import destiny.fabricated.client.screen.FabricatorCraftScreen;
import destiny.fabricated.client.screen.FabricatorUpgradeScreen;
import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.ItemInit;
import destiny.fabricated.init.MenuInit;
import destiny.fabricated.items.FabricatorBulkModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.tooltip.RecipeTooltipComponent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FabricatedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(BlockEntityInit.FABRICATOR.get(), context -> new FabricatorBlockRenderer());
    }

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(RecipeTooltipComponent.class, Function.identity());
    }

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event)
    {
        MenuScreens.register(MenuInit.FABRICATOR_UPGRADES.get(), FabricatorUpgradeScreen::new);
        MenuScreens.register(MenuInit.FABRICATOR_CRAFTING.get(), FabricatorCraftScreen::new);
        MenuScreens.register(MenuInit.FABRICATOR_BROWSER.get(), FabricatorBrowserCraftScreen::new);
    }

    @SubscribeEvent
    public static void creativeTabs(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
            event.accept(ItemInit.FABRICATOR.get());

        if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))
        {
            event.accept(FabricatorRecipeModuleItem.createCrafting(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
            event.accept(FabricatorRecipeModuleItem.createSmelting(ItemInit.FABRICATOR_RECIPE_MODULE.get()));

            event.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_1.get(), 16));
            event.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_2.get(), 32));
            event.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_3.get(), 64));
        }
    }
}
