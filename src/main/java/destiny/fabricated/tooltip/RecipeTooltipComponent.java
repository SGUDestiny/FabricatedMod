package destiny.fabricated.tooltip;

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
        List<ItemStack> items = getItems(Minecraft.getInstance().player.getInventory(), recipe.getIngredients());
        for (int i = 0; i < items.size(); i++)
        {
            ItemStack stack = items.get(i);

            graphics.renderItem(stack, pX+(18*i), pY);
            graphics.renderItemDecorations(font, stack, pX+(18*i), pY);
        }
    }

    public static List<ItemStack> getItems(Inventory inventory, List<Ingredient> ingredients) {
        List<ItemStack> result = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            boolean matched = false;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack invStack = inventory.getItem(i);

                if (!invStack.isEmpty() && ingredient.test(invStack)) {
                    ItemStack copy = invStack.copy();
                    copy.setCount(1);
                    result.add(copy);
                    matched = true;
                    break;
                }
            }

            if (!matched)
                continue;
        }

        Map<ItemStack, Integer> combined = new HashMap<>();

        outer:
        for (ItemStack stack : result) {
            for (ItemStack key : combined.keySet()) {
                if (ItemStack.isSameItemSameTags(key, stack)) {
                    combined.put(key, combined.get(key) + 1);
                    continue outer;
                }
            }
            combined.put(stack.copyWithCount(1), 1);
        }

        List<ItemStack> combinedResult = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : combined.entrySet()) {
            ItemStack stack = entry.getKey().copy();
            stack.setCount(entry.getValue());
            combinedResult.add(stack);
        }
        combinedResult.sort(Comparator.comparing(stack -> stack.getDisplayName().getString()));

        return combinedResult;
    }


    @Override
    public int getHeight()
    {
        return 18;
    }

    @Override
    public int getWidth(Font pFont)
    {
        return 18*getItems(Minecraft.getInstance().player.getInventory(), recipe.getIngredients()).size();
    }
}
