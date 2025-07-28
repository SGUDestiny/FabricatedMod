package destiny.fabricated.recipes;

import destiny.fabricated.recipes.containers.FabricatorContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public abstract class FabricationType<R extends Recipe<?>>
{
    public FabricationType() {}

    public abstract List<Fabrication> assembleFabrications(FabricatorContainer container, R recipe, RegistryAccess registryAccess);
}
