package destiny.fabricated.recipes;

import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShapelessFabricationType extends FabricationType<ShapelessRecipe>
{
    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, ShapelessRecipe recipe, RegistryAccess registryAccess)
    {
        List<ItemStack> inputs = MathUtil.ingredientsToStacks(container.getItems(), recipe.getIngredients());

        if(inputs.isEmpty())
            return new ArrayList<>();

        return List.of(new Fabrication(List.of(recipe.getResultItem(registryAccess)), inputs));
    }
}
