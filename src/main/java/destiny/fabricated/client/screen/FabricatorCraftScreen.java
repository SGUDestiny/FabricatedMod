package destiny.fabricated.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.network.packets.ServerboundBrowserMenuPacket;
import destiny.fabricated.tooltip.RecipeTooltipComponent;
import destiny.fabricated.util.RenderBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

import static destiny.fabricated.client.screen.FabricatorBrowserCraftScreen.filterCraftableRecipes;

public class FabricatorCraftScreen extends AbstractContainerScreen<FabricatorCraftingMenu>
{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_icon_bg.png");

    public static final ResourceLocation ARROW_UP_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_up.png");
    public static final ResourceLocation ARROW_DOWN_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_down.png");

    public static final ResourceLocation RECIPE_BROWSER_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_recipe_browser.png");

    public List<Recipe<Container>> recipes;
    public RecipeType<?> selectedTypeKey;
    public boolean hasSelected;
    public int scrollAmount;
    public int selectedType;

    public FabricatorCraftScreen(FabricatorCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.recipes = new ArrayList<>();
        this.selectedType = pMenu.type;
        this.scrollAmount = pMenu.item;

        this.hasSelected = selectedType != -1;
        if(this.hasSelected)
        {
            this.selectedTypeKey = ForgeRegistries.RECIPE_TYPES.getValue(menu.blockEntity.getRecipeTypes().get(selectedType).key.location());
            selectedType += 1;
            scrollAmount -= 1;
        }
    }

    @Override
    protected void init()
    {
        super.init();
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        int i = 0;
        for(RecipeData data : this.menu.recipeTypes)
        {
            i++;
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            this.addWidget(this.createButton(i, x, y, 18, 18, data));
        }

        {
            int y = baseY+(4*22)+(selectedType*22)+32;
            int x = baseX+140;
            this.addWidget(this.createCraftButton(4, x, y, 18, 18));
        }
        {
            int x = baseX+140+20;
            int y = baseY+(4*22)+(selectedType*22)+30;
            this.addWidget(this.createBatchButton(x, y, 10, 10, 1));
        }
        {
            int x = baseX+140+20;
            int y = baseY+(4*22)+(selectedType*22)+32+9;
            this.addWidget(this.createBatchButton(x, y, 10, 10, -1));
        }
        {
            int x = baseX+122;
            int y = baseY+(this.menu.recipeTypes.size()*22)+140;
            this.addWidget(this.createRecipeBrowserButton(x, y, 10, 10));
        }

        for(int o = 0; o<10; o++)
        {
            if(o == 4)
                continue;

            int y = baseY+(o*22)+(selectedType*22)+32;
            int x = baseX+140;
            this.addWidget(this.createScrollButton(o-4, x, y, 18, 18));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        if(menu.blockEntity.state == 3)
            return;

        PoseStack pose = graphics.pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        int o = 0;
        for(RecipeData data : this.menu.recipeTypes)
        {
            o++;
            int x = baseX+118;
            int y = baseY+(o*22)+120;

            pose.pushPose();

            if(pMouseX > x && pMouseX < x+18)
                if(pMouseY > y && pMouseY < y+18)
                    graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(data.nameComponent), pMouseX, pMouseY);

            pose.translate(x, y, 0);

            pose.pushPose();
            pose.scale(0.07f, 0.07f, 0.07f);
            RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);
            pose.popPose();

            ItemStack stack = data.getItem().getDefaultInstance();

            graphics.renderItem(stack, 1, 1);
            pose.popPose();
        }

        {
            int x = baseX+122;
            int y = baseY+(this.menu.recipeTypes.size()*22)+140;

            pose.pushPose();
            pose.translate(x, y, 0);
            RenderBlitUtil.blit(RECIPE_BROWSER_BUTTON_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
            pose.popPose();
        }

        if(hasSelected)
        {
            if(recipes.isEmpty())
            {
                recipeStuff(selectedTypeKey);
                return;
            }
            if(menu.blockEntity.fabricationStep == 2)
                recipeStuff(selectedTypeKey);

            if(minecraft.level.getGameTime() % 2 == 0)
            {
                if(menu.blockEntity.batchValue > menu.blockEntity.maxBatch())
                    menu.blockEntity.batchValue = menu.blockEntity.maxBatch();

                int maxBatch = getMaxCraft(recipes.get(scrollAmount), minecraft.player.getInventory());
                if(maxBatch < menu.blockEntity.batchValue)
                    menu.blockEntity.batchValue = maxBatch;
            }

            if(selectedType != -1)
            {
                pose.pushPose();
                pose.translate(baseX+140, baseY+(4*22)+(selectedType*22)+32, 0);

                pose.pushPose();
                pose.translate(20, -2, 0);
                //pose.scale(1/0.07f, 1/0.07f, 1/0.07f);
                RenderBlitUtil.blit(ARROW_UP_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
                pose.popPose();

                pose.pushPose();
                pose.translate(20, 9, 0);
                //pose.scale(1/0.07f, 1/0.07f, 1/0.07f);
                RenderBlitUtil.blit(ARROW_DOWN_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
                pose.popPose();

                pose.pushPose();
                pose.translate(32, 4, 0);
                //pose.scale(1/0.07f, 1/0.07f, 1/0.07f);
                font.drawInBatch(String.valueOf(menu.blockEntity.batchValue), 0, 0, 0x5CB8FF, true, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
                pose.popPose();

                pose.popPose();
            }

            if(scrollAmount >= recipes.size())
                scrollAmount = recipes.size()-1;
            if(scrollAmount < 0)
                scrollAmount = 0;

            int baseI = 2;
            int iO = 3;
            int recipeI = -1;
            int amountToScroll = org.joml.Math.clamp(0, recipes.size(), scrollAmount);
            if(scrollAmount == 1)
            {
                iO -= 1;
                amountToScroll -= 1;
            }
            if(scrollAmount == 2)
            {
                iO -= 2;
                amountToScroll -= 2;
            }
            if(scrollAmount == 3)
            {
                iO -= 3;
                amountToScroll -= 3;
            }
            if(scrollAmount >= 4)
            {
                iO -= 4;
                amountToScroll -= 4;
            }

            int recipeListSize = 9;
            for (int i = baseI; i < baseI+recipeListSize; i++)
            {

                recipeI++;
                iO++;

                float alpha = 1F;
                if(scrollAmount == 3)
                    recipeListSize = 8;
                if(scrollAmount == 2)
                    recipeListSize = 7;
                if(scrollAmount == 1)
                    recipeListSize = 6;
                if(scrollAmount == 0)
                    recipeListSize = 5;

                if(i == baseI && scrollAmount >= 3)
                {
                    alpha = 0.25f;
                }
                if(i == baseI && scrollAmount == 2)
                {
                    alpha = 0.5f;

                }
                if(i == baseI && scrollAmount == 1)
                {
                    alpha = 0.75f;
                }

                if(i == baseI+1 && scrollAmount >= 3)
                {
                    alpha = 0.5f;
                }
                if(i == baseI+1 && scrollAmount == 2)
                {
                    alpha = 0.75f;
                }

                if(i == baseI+2 && scrollAmount >= 3)
                    alpha = 0.75f;

                if(i == baseI+recipeListSize-1)
                    alpha = 0.25f;
                if(i == baseI+recipeListSize-2)
                    alpha = 0.5f;
                if(i == baseI+recipeListSize-3)
                    alpha = 0.75f;

                try
                {
                    Recipe<Container> recipe = recipes.get(recipeI+amountToScroll);
                    if(recipe != null)
                    {
                        int x = baseX+140;
                        int y = baseY+(iO*22)+(selectedType*22)+32;

                        pose.pushPose();
                        ItemStack stack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());

                        pose.pushPose();
                        if(pMouseX > x && pMouseX < x+18)
                            if(pMouseY > y && pMouseY < y+18)
                                graphics.renderTooltip(Minecraft.getInstance().font, List.of(stack.getHoverName()), Optional.of(new RecipeTooltipComponent(recipe)) ,pMouseX, pMouseY);

                        pose.translate(x, y, 0);

                        pose.pushPose();

                        pose.scale(0.07f, 0.07f, 0.07f);
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
                        RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);

                        RenderSystem.disableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                        pose.popPose();

                        graphics.renderItem(stack, 1, 1);
                        pose.popPose();
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    break;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        //renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY)
    {

    }

    public AbstractButton createButton(int number, int x, int y, int width, int height, RecipeData data)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                recipeStuff(ForgeRegistries.RECIPE_TYPES.getValue(data.getKey().location()));
                selectedTypeKey = ForgeRegistries.RECIPE_TYPES.getValue(data.getKey().location());
                selectedType = id;
                hasSelected = true;
                scrollAmount = 0;
                rebuildWidgets();
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public AbstractButton createCraftButton(int number, int x, int y, int width, int height)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                if (!hasSelected) return;
                if (menu.blockEntity.state == 3) return;
                if (recipes.isEmpty()) return;

                List<Ingredient> ingredients = recipes.get(scrollAmount).getIngredients();
                Inventory inventory = minecraft.player.getInventory();

                ItemStack stackToCraft = recipes.get(scrollAmount).getResultItem(Minecraft.getInstance().level.registryAccess());
                if(hasRequiredItems(minecraft.player.getInventory(), fancyGetItems(inventory, ingredients), menu.blockEntity.batchValue))
                    menu.blockEntity.fabricate(menu.level, menu.blockEntity.getBlockPos(), menu.blockEntity, stackToCraft, fancyGetItems(inventory, ingredients));
            }

            @Override
            public void playDownSound(SoundManager pHandler) {}
        };
    }

    public AbstractButton createScrollButton(int number, int x, int y, int width, int height)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                if(!hasSelected)
                    return;
                if(recipes.isEmpty())
                    return;

                if(id == -1 && scrollAmount < 1)
                    return;
                if(id == -2 && scrollAmount < 2)
                    return;
                if(id == -3 && scrollAmount < 3)
                    return;

                scrollAmount = scrollAmount + id;
                if(scrollAmount >= recipes.size())
                    scrollAmount = recipes.size()-1;
                if(scrollAmount < 0)
                    scrollAmount = 0;
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public AbstractButton createRecipeBrowserButton(int x, int y, int width, int height)
    {
        return new AbstractButton(x, y, width, height, Component.empty()) {
            @Override
            public void onPress()
            {
                ServerboundBrowserMenuPacket packet = new ServerboundBrowserMenuPacket(true, 0, 0, menu.blockEntity.getBlockPos());
                NetworkInit.sendToServer(packet);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }
        };
    }

    public AbstractButton createBatchButton(int x, int y, int width, int height, int increment)
    {
        return new AbstractButton(x, y, width, height, Component.empty()) {
            @Override
            public void onPress()
            {


                int usedIncrement = increment;
                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT))
                    usedIncrement *= 16;

                if(recipes.isEmpty())
                {
                    menu.blockEntity.incrementBatch(usedIncrement);
                    return;
                }
                int maxBatch = getMaxCraft(recipes.get(scrollAmount), minecraft.player.getInventory());
                if(maxBatch < menu.blockEntity.batchValue && maxBatch < menu.blockEntity.batchValue+usedIncrement)
                    return;
                if(maxBatch >= menu.blockEntity.batchValue && maxBatch < menu.blockEntity.batchValue+usedIncrement)
                {
                    menu.blockEntity.incrementBatch(maxBatch-menu.blockEntity.batchValue);
                    return;
                }

                menu.blockEntity.incrementBatch(usedIncrement);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public <T extends Recipe<?>> void recipeStuff(RecipeType<T> recipeType)
    {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<Recipe<Container>> recipes = recipeManager.getAllRecipesFor(((RecipeType<Recipe<Container>>) recipeType));

        this.recipes = recipes.stream().sorted(Comparator.comparing(recipe -> recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getDisplayName().getString())).toList();
        this.recipes = this.recipes.stream().filter(entry -> !entry.getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty()).toList();
        this.recipes = this.recipes.stream().filter(entry -> !(entry.isSpecial())).toList();

        this.recipes = filterCraftableRecipes(this.recipes, minecraft.player.getInventory());
    }

    public static int getMaxCraft(Recipe<?> recipe, Inventory playerInventory) {
        List<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients.isEmpty())
            return 0;

        List<ItemStack> available = new ArrayList<>();
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (!stack.isEmpty()) {
                available.add(stack.copy());
            }
        }

        int craftCount = 0;

        while (true) {
            List<ItemStack> tempInventory = available.stream().map(ItemStack::copy).collect(Collectors.toList());
            boolean canCraft = true;

            for (Ingredient ingredient : ingredients) {
                boolean matched = false;
                if(ingredient.isEmpty())
                    continue;

                for (ItemStack stack : tempInventory) {
                    if (ingredient.test(stack) && stack.getCount() > 0) {
                        stack.shrink(1);
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    canCraft = false;
                    break;
                }
            }

            if (canCraft) {
                craftCount++;
                available = tempInventory;
            } else {
                break;
            }
        }

        return craftCount;
    }

    public static boolean hasRequiredItems(Inventory inventory, List<ItemStack> requiredItems, int batchValue) {
        List<ItemStack> simulatedInventory = inventory.items.stream()
                .map(ItemStack::copy)
                .toList();

        for (ItemStack required : requiredItems) {
            if (required.isEmpty()) continue;

            required.setCount(required.getCount()*batchValue);

            int remaining = required.getCount();

            for (ItemStack invStack : simulatedInventory) {
                if (!invStack.isEmpty() && ItemStack.isSameItemSameTags(invStack, required)) {
                    int used = Math.min(remaining, invStack.getCount());
                    remaining -= used;
                    invStack.shrink(used);

                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public List<ItemStack> getItems(Recipe<Container> recipe)
    {
        List<ItemStack> items = new ArrayList<>();
        recipe.getIngredients().forEach(ingredient ->
        {
            ItemStack stack = ingredient.getItems().length == 0 ? ItemStack.EMPTY :  ingredient.getItems()[0];
            items.add(stack);
        });

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

        List<ItemStack> result = new ArrayList<>();
        map.forEach((item, count) -> result.add(new ItemStack(item, count)));

        return result;
    }

    public static List<ItemStack> fancyGetItems(Inventory inventory, List<Ingredient> ingredients) {
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

        return combinedResult;
    }

}
