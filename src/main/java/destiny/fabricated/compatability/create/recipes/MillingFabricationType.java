package destiny.fabricated.compatability.create.recipes;

import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import destiny.fabricated.recipes.Fabrication;
import destiny.fabricated.recipes.FabricationType;
import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MillingFabricationType extends FabricationType<MillingRecipe>
{

    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, MillingRecipe recipe,
                                                  RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();
        if(!recipe.getFluidIngredients().isEmpty())
            return fabrications;
        if(!recipe.getFluidResults().isEmpty())
            return fabrications;

        Consumer<Fabrication> modifier = fabrication -> fabrication.outputs = recipe.rollResults();
        fabrications.add(new Fabrication(recipe.rollResults(), MathUtil.ingredientsToStacks(container.getItems(), recipe.getIngredients()), modifier));

        return fabrications;
    }
}
