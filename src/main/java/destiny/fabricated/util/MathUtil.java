package destiny.fabricated.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.stream.Stream;

public class MathUtil {
    public static VoxelShape buildShape(VoxelShape... from) {
        return Stream.of(from).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    public static List<ItemStack> ingredientsToStacks(List<ItemStack> stored, List<Ingredient> inputs)
    {
        List<ItemStack> stuff = new ArrayList<>();
        for(Ingredient ingredient : inputs)
        {
            boolean found = false;
            for(ItemStack stack : stored)
            {
                if(ingredient.test(stack))
                {
                    stuff.add(stack.copyWithCount(1));
                    found = true;
                    break;
                }
            }
            if(!found)
                return new ArrayList<>();
        }

        return mergeItemStacks(stuff);
    }

    public static List<ItemStack> mergeItemStacks(List<ItemStack> stacks) {
        Map<ItemStack, Integer> combinedMap = new LinkedHashMap<>();

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            boolean found = false;

            for (Map.Entry<ItemStack, Integer> entry : combinedMap.entrySet()) {
                ItemStack key = entry.getKey();
                if (ItemStack.isSameItemSameTags(key, stack)) {
                    combinedMap.put(key, entry.getValue() + stack.getCount());
                    found = true;
                    break;
                }
            }

            if (!found) {
                ItemStack keyCopy = stack.copy();
                combinedMap.put(keyCopy, stack.getCount());
                //keyCopy.setCount(0);
            }
        }

        List<ItemStack> merged = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : combinedMap.entrySet()) {
            ItemStack result = entry.getKey().copy();
            result.setCount(entry.getValue());
            merged.add(result);
        }

        return merged;
    }

    public static int matchStacks(List<ItemStack> stored, List<ItemStack> inputs)
    {
        HashMap<Item, Integer> map = new HashMap<>();
        for(ItemStack stack : stored)
        {
            ItemStack storedStack = stack.copy();
            for(ItemStack input : inputs)
            {
                int amount = 0;
                if (ItemStack.isSameItem(storedStack, input))
                {
                    while (storedStack.getCount() >= input.getCount())
                    {
                        amount++;
                        storedStack.setCount(storedStack.getCount() - input.getCount());
                    }
                    map.put(input.getItem(), amount);
                }
            }
        }
        List<Map.Entry<Item, Integer>> entries = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
        if(entries.isEmpty())
            return 1;
        return entries.get(0).getValue();
    }

    public static List<ItemStack> findStacks(ItemLike item, List<ItemStack> stored, boolean mergeAmounts)
    {
        List<ItemStack> stuff = new ArrayList<>();
        for(ItemStack itemStack : stored)
        {
            if(itemStack.is(item.asItem()))
                stuff.add(itemStack.copy());
        }

        if(mergeAmounts)
            return mergeItemStacks(stuff);

        return stuff;
    }
}
