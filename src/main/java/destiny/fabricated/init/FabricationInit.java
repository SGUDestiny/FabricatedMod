package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class FabricationInit
{
    public static final DeferredRegister<FabricationType<?>> FABRICATIONS = DeferredRegister.create(new ResourceLocation(FabricatedMod.MODID, "fabrication_type"), FabricatedMod.MODID);
    public static final Supplier<IForgeRegistry<FabricationType<?>>> FABRICATION = FABRICATIONS.makeRegistry(RegistryBuilder::new);

    public static final HashMap<Class<? extends Recipe<?>>, FabricationType<?>> FABRICATION_MAP = new HashMap<>();

    public static final RegistryObject<FabricationType<ShapedRecipe>> SHAPED_CRAFTING =
            registerFabrication(ShapedRecipe.class, ShapedFabricationType::new, "shaped_crafting");
    public static final RegistryObject<FabricationType<ShapelessRecipe>> SHAPELESS_CRAFTING =
            registerFabrication(ShapelessRecipe.class, ShapelessFabricationType::new, "shapeless_crafting");

    public static final RegistryObject<FabricationType<SmeltingRecipe>> SMELTING_RECIPE =
            registerFabrication(SmeltingRecipe.class, SmeltingFabricationType::new, "smelting_recipe");
    public static final RegistryObject<FabricationType<BlastingRecipe>> BLASTING_RECIPE =
            registerFabrication(BlastingRecipe.class, BlastingFabricationType::new, "blasting_recipe");
    public static final RegistryObject<FabricationType<SmokingRecipe>> SMOKING_RECIPE =
            registerFabrication(SmokingRecipe.class, SmokingFabricationType::new, "smoking_recipe");

    public static final RegistryObject<FabricationType<TippedArrowRecipe>> TIPPED_ARROW_CRAFTING =
            registerFabrication(TippedArrowRecipe.class, TippedArrowFabricationType::new, "tipped_arrow_crafting");
    public static final RegistryObject<FabricationType<ShulkerBoxColoring>> SHULKER_BOX_COLORING =
            registerFabrication(ShulkerBoxColoring.class, ShulkerBoxColoringFabricationType::new, "shulker_box_coloring");
    //public static final RegistryObject<FabricationType<FireworkRocketRecipe>> FIREWORK_ROCKET_CRAFTING =
    //        registerFabrication(FireworkRocketRecipe.class, FireworkRocketFabricationType::new, "firework_rocket_crafting");

    public static <R extends Recipe<?>> RegistryObject<FabricationType<R>> registerFabrication(Class<R> clazz, Supplier<FabricationType<R>> fabricationType, String id)
    {
        FABRICATION_MAP.put(clazz, fabricationType.get());
        return FABRICATIONS.register(id, fabricationType);
    }

    public static void register(IEventBus bus)
    {
        FABRICATIONS.register(bus);
    }
}
