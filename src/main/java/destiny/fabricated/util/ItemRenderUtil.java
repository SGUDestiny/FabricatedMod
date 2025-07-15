package destiny.fabricated.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.List;

import static net.minecraft.client.renderer.entity.ItemRenderer.*;

public class ItemRenderUtil
{
    private static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.vanilla("trident", "inventory");
    public static final ModelResourceLocation TRIDENT_IN_HAND_MODEL = ModelResourceLocation.vanilla("trident_in_hand", "inventory");
    private static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.vanilla("spyglass", "inventory");
    public static final ModelResourceLocation SPYGLASS_IN_HAND_MODEL = ModelResourceLocation.vanilla("spyglass_in_hand", "inventory");


    public ItemRenderer itemRenderer;
    public ItemRenderUtil()
    {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    public void renderStatic(@Nullable LivingEntity pEntity, ItemStack pItemStack, ItemDisplayContext pDiplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, @Nullable Level pLevel, int pCombinedLight, int pCombinedOverlay, int pSeed, float alpha) {
        if (!pItemStack.isEmpty()) {
            BakedModel bakedmodel = itemRenderer.getModel(pItemStack, pLevel, pEntity, pSeed);
            this.render(pItemStack, pDiplayContext, pLeftHand, pPoseStack, pBuffer, pCombinedLight, pCombinedOverlay, bakedmodel, alpha);
        }
    }

    public void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay, BakedModel pModel, float alpha) {
        if (!pItemStack.isEmpty()) {
            pPoseStack.pushPose();
            boolean flag = pDisplayContext == ItemDisplayContext.GUI || pDisplayContext == ItemDisplayContext.GROUND || pDisplayContext == ItemDisplayContext.FIXED;
            if (flag) {
                if (pItemStack.is(Items.TRIDENT)) {
                    pModel = itemRenderer.getItemModelShaper().getModelManager().getModel(TRIDENT_MODEL);
                } else if (pItemStack.is(Items.SPYGLASS)) {
                    pModel = itemRenderer.getItemModelShaper().getModelManager().getModel(SPYGLASS_MODEL);
                }
            }

            pModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pPoseStack, pModel, pDisplayContext, pLeftHand);
            pPoseStack.translate(-0.5F, -0.5F, -0.5F);
            if (!pModel.isCustomRenderer() && (!pItemStack.is(Items.TRIDENT) || flag)) {
                boolean flag1;
                if (pDisplayContext != ItemDisplayContext.GUI && !pDisplayContext.firstPerson() && pItemStack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem)pItemStack.getItem()).getBlock();
                    flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }
                for (var model : pModel.getRenderPasses(pItemStack, flag1)) {
                    for (var rendertype : model.getRenderTypes(pItemStack, flag1)) {
                        VertexConsumer vertexconsumer;
                        if (hasAnimatedTexture(pItemStack) && pItemStack.hasFoil()) {
                            pPoseStack.pushPose();
                            PoseStack.Pose posestack$pose = pPoseStack.last();
                            if (pDisplayContext == ItemDisplayContext.GUI) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
                            } else if (pDisplayContext.firstPerson()) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
                            }

                            if (flag1) {
                                vertexconsumer = getCompassFoilBufferDirect(pBuffer, rendertype, posestack$pose);
                            } else {
                                vertexconsumer = getCompassFoilBuffer(pBuffer, rendertype, posestack$pose);
                            }

                            pPoseStack.popPose();
                        } else if (flag1) {
                            vertexconsumer = getFoilBufferDirect(pBuffer, rendertype, true, pItemStack.hasFoil());
                        } else {
                            vertexconsumer = getFoilBuffer(pBuffer, rendertype, true, pItemStack.hasFoil());
                        }

                        this.renderModelLists(model, pItemStack, pCombinedLight, pCombinedOverlay, pPoseStack, vertexconsumer, alpha);
                    }
                }
            } else {
                net.minecraftforge.client.extensions.common.IClientItemExtensions.of(pItemStack).getCustomRenderer().renderByItem(pItemStack, pDisplayContext, pPoseStack, pBuffer, pCombinedLight, pCombinedOverlay);
            }

            pPoseStack.popPose();
        }
    }

    public void renderModelLists(BakedModel pModel, ItemStack pStack, int pCombinedLight, int pCombinedOverlay, PoseStack pPoseStack, VertexConsumer pBuffer, float alpha) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            this.renderQuadList(pPoseStack, pBuffer, pModel.getQuads((BlockState)null, direction, randomsource), pStack, pCombinedLight, pCombinedOverlay, alpha);
        }

        randomsource.setSeed(42L);
        this.renderQuadList(pPoseStack, pBuffer, pModel.getQuads((BlockState)null, (Direction)null, randomsource), pStack, pCombinedLight, pCombinedOverlay, alpha);
    }

    public void renderQuadList(PoseStack pPoseStack, VertexConsumer pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay, float alpha) {
        boolean flag = !pItemStack.isEmpty();
        PoseStack.Pose posestack$pose = pPoseStack.last();

        for(BakedQuad bakedquad : pQuads) {
            int i = -1;

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            pBuffer.putBulkData(posestack$pose, bakedquad, f, f1, f2, alpha, pCombinedLight, pCombinedOverlay, true);
        }

    }

    private static boolean hasAnimatedTexture(ItemStack pStack) {
        return pStack.is(ItemTags.COMPASSES) || pStack.is(Items.CLOCK);
    }
}
