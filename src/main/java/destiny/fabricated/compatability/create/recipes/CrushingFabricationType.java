package destiny.fabricated.compatability.create.recipes;

import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import destiny.fabricated.recipes.Fabrication;
import destiny.fabricated.recipes.FabricationType;
import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CrushingFabricationType extends FabricationType<CrushingRecipe>
{

    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, CrushingRecipe recipe,
                                                  RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();
        if(!recipe.getFluidIngredients().isEmpty())
            return fabrications;
        if(!recipe.getFluidResults().isEmpty())
            return fabrications;
        List<ItemStack> inputs = MathUtil.ingredientsToStacks(container.getItems(), recipe.getIngredients());
        if(inputs.isEmpty())
            return fabrications;

        Consumer<Fabrication> modifier = fabrication -> fabrication.outputs = recipe.rollResults();
        fabrications.add(new Fabrication(recipe.rollResults(), inputs, modifier, recipe.getId()));

        return fabrications;
    }
}
