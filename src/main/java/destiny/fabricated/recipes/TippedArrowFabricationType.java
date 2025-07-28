package destiny.fabricated.recipes;

import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;

import java.util.ArrayList;
import java.util.List;

public class TippedArrowFabricationType extends FabricationType<TippedArrowRecipe>
{
    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, TippedArrowRecipe recipe, RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();

        List<ItemStack> potions = container.getItems().stream().filter(stack -> stack.is(Items.LINGERING_POTION)).toList();
        int arrowCount = MathUtil.matchStacks(MathUtil.mergeItemStacks(container.getItems()), List.of(new ItemStack(Items.ARROW, 8)));

        boolean hasArrows = arrowCount >= 1;
        if(hasArrows)
            for(ItemStack potion : potions)
            {
                ItemStack arrow = new ItemStack(Items.TIPPED_ARROW, 8);
                PotionUtils.setPotion(arrow, PotionUtils.getPotion(potion));
                PotionUtils.setCustomEffects(arrow, PotionUtils.getCustomEffects(potion));

                if(fabrications.stream().noneMatch(fabrication -> fabrication.getOutputs().stream().anyMatch(stack -> PotionUtils.getPotion(potion).equals(PotionUtils.getPotion(stack)))))
                    fabrications.add(new Fabrication(List.of(arrow), List.of(potion.copyWithCount(1), new ItemStack(Items.ARROW, 8))));
            }

        return fabrications;
    }
}
