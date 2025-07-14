package destiny.fabricated.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.*;

public class RecipeTooltipComponent implements ClientTooltipComponent, TooltipComponent
{
    public Recipe<?> recipe;
    public List<ItemStack> items;
    public float time;

    public RecipeTooltipComponent(Recipe<?> recipe)
    {
        this.recipe = recipe;
        this.items = new ArrayList<>();
        recipe.getIngredients().forEach(ingredient ->
        {
            ItemStack stack = ingredient.getItems().length == 0 ? ItemStack.EMPTY :  ingredient.getItems()[0];
            items.add(stack);
        });
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics)
    {
        for (int i = 0; i < getItems().size(); i++)
        {
            Item item = getItems().keySet().stream().toList().get(i);
            int count = getItems().get(item);

            ItemStack stack = new ItemStack(item, count);

            graphics.renderItem(stack, pX+(18*i), pY);
            graphics.renderItemDecorations(font, stack, pX+(18*i), pY);
        }
    }

    public HashMap<Item, Integer> getItems()
    {
        HashMap<Item, Integer> map = new HashMap<>();
        for(ItemStack stack : items)
        {
            if(stack.isEmpty())
                continue;

            boolean merged = false;
            for(Item key : map.keySet())
                if(stack.is(key))
                {
                    map.put(key, map.get(key) + stack.getCount());
                    merged = true;
                    break;
                }
            if(!merged)
                map.put(stack.getItem(), stack.getCount());
        }

        return map;
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
