package destiny.fabricated.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeTooltipComponent implements ClientTooltipComponent, TooltipComponent
{
    public int itemDisplayTick = 0;
    public Recipe<?> recipe;

    public RecipeTooltipComponent(Recipe<?> recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics)
    {
        itemDisplayTick++;
        for (int i = 0; i < recipe.getIngredients().size(); i++)
        {
            ItemStack[] items = recipe.getIngredients().get(i).getItems();
            ItemStack pickedItem = items[Math.min(itemDisplayTick/40, items.length)];

            graphics.renderItem(pickedItem, pX+(18*i), pY);
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
        return 18*recipe.getIngredients().size();
    }
}
