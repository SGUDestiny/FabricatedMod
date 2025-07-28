package destiny.fabricated.tooltip;

import destiny.fabricated.recipes.Fabrication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

public class RecipeTooltipComponent implements ClientTooltipComponent, TooltipComponent
{
    public Fabrication recipe;

    public RecipeTooltipComponent(Fabrication recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics)
    {
        List<ItemStack> items = recipe.getInputs();
        for (int i = 0; i < items.size(); i++)
        {
            ItemStack stack = items.get(i);

            graphics.renderItem(stack, pX+(18*i), pY);
            graphics.renderItemDecorations(font, stack, pX+(18*i), pY);
        }
    }

    @Override
    public int getHeight()
    {
        return 18;
    }

    @Override
    public int getWidth(Font pFont)
    {
        return 18*recipe.getInputs().size();
    }
}
