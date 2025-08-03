package destiny.fabricated.init;

import com.simibubi.create.Create;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.items.FabricatorBulkModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemTabInit
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FabricatedMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> BlockInit.FABRICATOR.get().asItem().getDefaultInstance())
                    .title(Component.translatable("tabs.fabricated.main"))
                    .displayItems((param, output) ->
                    {
                        output.accept(ItemInit.FABRICATOR.get());

                        output.accept(FabricatorRecipeModuleItem.createCrafting(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
                        output.accept(FabricatorRecipeModuleItem.createSmelting(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
                        if(ModList.get().isLoaded(Create.ID))
                        {
                            output.accept(FabricatorRecipeModuleItem.createMechanicalCrafting(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
                            output.accept(FabricatorRecipeModuleItem.createCrushing(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
                            output.accept(FabricatorRecipeModuleItem.createProcessing(ItemInit.FABRICATOR_RECIPE_MODULE.get()));
                        }

                        output.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_1.get(), 16));
                        output.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_2.get(), 32));
                        output.accept(FabricatorBulkModuleItem.create(ItemInit.FABRICATOR_BULK_MODULE_3.get(), 64));

                    })
                    .build());

    public static void register(IEventBus bus)
    {
        TABS.register(bus);
    }
}
