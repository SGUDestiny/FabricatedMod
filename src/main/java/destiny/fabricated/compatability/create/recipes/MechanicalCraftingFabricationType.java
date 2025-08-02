package destiny.fabricated.compatability.create.recipes;

import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import destiny.fabricated.recipes.Fabrication;
import destiny.fabricated.recipes.FabricationType;
import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MechanicalCraftingFabricationType extends FabricationType<MechanicalCraftingRecipe>
{

    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, MechanicalCraftingRecipe recipe,
                                                  RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();
        //TODO - MAKE THIS CHECK IF IT CAN CRAFT INSTEAD.

        fabrications.add(new Fabrication(List.of(recipe.getResultItem(registryAccess)), MathUtil.ingredientsToStacks(container.getItems(), recipe.getIngredients())));

        return fabrications;
    }
}
