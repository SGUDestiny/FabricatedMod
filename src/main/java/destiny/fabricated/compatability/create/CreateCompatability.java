package destiny.fabricated.compatability.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import destiny.fabricated.compatability.create.recipes.*;
import destiny.fabricated.init.FabricationInit;
import destiny.fabricated.recipes.FabricationType;
import destiny.fabricated.recipes.ShulkerBoxColoringFabricationType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static destiny.fabricated.init.FabricationInit.FABRICATION_MAP;
import static destiny.fabricated.init.FabricationInit.REGISTRY_NAME;

public class CreateCompatability
{
    public static final DeferredRegister<FabricationType<?>> FABRICATIONS = DeferredRegister.create(REGISTRY_NAME, Create.ID);

    public static final RegistryObject<FabricationType<CrushingRecipe>> CRUSHING =
            registerFabrication(CrushingRecipe.class, CrushingFabricationType::new, "crushing");
    public static final RegistryObject<FabricationType<MechanicalCraftingRecipe>> MECHANICAL_CRAFTING =
            registerFabrication(MechanicalCraftingRecipe.class, MechanicalCraftingFabricationType::new, "mechanical_crafting");
    public static final RegistryObject<FabricationType<MillingRecipe>> MILLING =
            registerFabrication(MillingRecipe.class, MillingFabricationType::new, "milling");
    public static final RegistryObject<FabricationType<PressingRecipe>> PRESSING =
            registerFabrication(PressingRecipe.class, PressingFabricationType::new, "pressing");
    public static final RegistryObject<FabricationType<CuttingRecipe>> CUTTING =
            registerFabrication(CuttingRecipe.class, CuttingFabricationType::new, "cutting");
    public static final RegistryObject<FabricationType<SandPaperPolishingRecipe>> POLISHING =
            registerFabrication(SandPaperPolishingRecipe.class, PolishingFabricationType::new, "polishing");

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
