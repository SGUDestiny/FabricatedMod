package destiny.fabricated.recipes;

import com.mojang.blaze3d.platform.InputConstants;
import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ShulkerBoxColoringFabricationType extends FabricationType<ShulkerBoxColoring>
{
    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, ShulkerBoxColoring recipe, RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();

        List<ItemStack> dyes = container.getItems().stream().filter(stack -> stack.is(Tags.Items.DYES)).toList();
        List<ItemStack> shulkers = MathUtil.findStacks(Items.SHULKER_BOX, container.getItems(), false);
        if(shulkers.isEmpty())
            return fabrications;

        ItemStack shulkerBox = shulkers.get(0);
        if(shulkerBox != ItemStack.EMPTY)
            for(ItemStack dyeStack : dyes)
            {
                if(fabrications.stream().anyMatch(fabrication -> fabrication.getOutputs().contains(dyeStack.copyWithCount(1))))
                    continue;

                DyeColor color = DyeColor.getColor(dyeStack);
                ItemStack coloredBox = ShulkerBoxBlock.getColoredItemStack(color);

                if(shulkerBox.hasTag())
                    coloredBox.setTag(shulkerBox.getTag().copy());

                if(fabrications.stream().noneMatch(fabrication -> fabrication.getOutputs().contains(coloredBox)))
                    fabrications.add(new Fabrication(List.of(coloredBox), List.of(dyeStack.copyWithCount(1), shulkerBox)));
            }

        return fabrications;
    }
}
