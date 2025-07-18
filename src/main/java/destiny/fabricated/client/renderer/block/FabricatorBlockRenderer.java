package destiny.fabricated.client.renderer.block;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.client.model.block.FabricatorModel;
import destiny.fabricated.client.renderer.FabricatorRenderTypes;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.cache.texture.GeoAbstractTexture;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class FabricatorBlockRenderer extends GeoBlockRenderer<FabricatorBlockEntity>
{
    private ItemRenderer itemRenderer;
    public FabricatorBlockRenderer()
    {
        super(new FabricatorModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this)
        {
            @Override
            protected RenderType getRenderType(FabricatorBlockEntity animatable)
            {
                return FabricatorRenderTypes.fabricatorGlow(GeoAbstractTexture.appendToPath(getTextureResource(animatable), "_glowmask"));
            }
        });
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FabricatorBlockEntity fabricator, BakedGeoModel model,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
                               float green, float blue, float alpha)
    {
        super.actuallyRender(poseStack, fabricator, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if(fabricator.craftStack.isEmpty())
            return;

        poseStack.pushPose();

        float animTime = fabricator.fabricatingTicker+partialTick;

        float timeUp = 15f;
        float timeDown = 62.5f;
        float speedUp = 0.328125f / timeUp;
        float speedDown = 0.328125f / (timeDown-timeUp);

        poseStack.translate(0, 0.3125, 0.09375);
        if(animTime <= timeUp)
        {
            poseStack.translate(0, speedUp * animTime, 0);
        }

        if(animTime > timeUp && animTime <= timeDown)
        {
            poseStack.translate(0, (speedUp * timeUp)-(speedDown*(animTime-timeUp)), 0);
        }

        if(!this.itemRenderer.getModel(fabricator.craftStack, fabricator.getLevel(), null, 0).isGui3d())
        {
            poseStack.translate(0,0.02, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(2f, 2f, 2f);
        }
        else poseStack.translate(0, 0.125, 0);

        poseStack.scale(0.25f, 0.25f, 0.25f);

        if(animTime > timeUp && animTime <= timeDown)
        {
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            float transparency = (animTime-15f)/(timeDown-15f);
            BakedModel bakedModel = itemRenderer.getModel(fabricator.craftStack, fabricator.getLevel(), null, 0);
            RenderType rt = FabricatorRenderTypes.fabricatingItem(TextureAtlas.LOCATION_BLOCKS);
            renderModel(poseStack.last(), bufferSource.getBuffer(rt), transparency, null, bakedModel, red, green, blue, 240, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, rt);
        }
        poseStack.popPose();
    }

    public static void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, float alpha, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_, ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, direction, randomsource, modelData, renderType), p_111075_, p_111076_);
        }

        randomsource.setSeed(42L);
        renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, (Direction) null, randomsource, modelData, renderType), p_111075_, p_111076_);
    }

    private static void renderQuadList(PoseStack.Pose p_111059_, VertexConsumer p_111060_, float p_111061_, float p_111062_, float p_111063_, float alpha, List<BakedQuad> p_111064_, int p_111065_, int p_111066_) {
        for (BakedQuad bakedquad : p_111064_) {
            float f;
            float f1;
            float f2;
            f = Mth.clamp(p_111061_, 0.0F, 1.0F);
            f1 = Mth.clamp(p_111062_, 0.0F, 1.0F);
            f2 = Mth.clamp(p_111063_, 0.0F, 1.0F);
            p_111060_.putBulkData(p_111059_, bakedquad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, f1, f2, alpha, new int[]{p_111065_, p_111065_, p_111065_, p_111065_}, p_111066_, false);
        }
    }
}
