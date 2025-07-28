package destiny.fabricated.recipes;

import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.List;

public class SmeltingFabricationType extends FabricationType<SmeltingRecipe>
{
    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, SmeltingRecipe recipe, RegistryAccess registryAccess)
    {
        if(recipe.getId().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ingot_from_smelting_raw_iron")))
            System.out.println("IRON!");
        List<ItemStack> inputs = MathUtil.ingredientsToStacks(container.getItems(), recipe.getIngredients());

        if(inputs.isEmpty())
            return new ArrayList<>();

        return List.of(new Fabrication(List.of(recipe.getResultItem(registryAccess)), inputs));
    }
}
