package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemTabInit
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FabricatedMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> Items.IRON_BLOCK.getDefaultInstance())
                    .title(Component.translatable("tabs.fabricated.main"))
                    .displayItems(((itemDisplayParameters, output) ->
                    {

                    })).build());

    public static void register(IEventBus bus)
    {
        TABS.register(bus);
    }
}
