package mod.chloeprime.apotheosismodernragnarok.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FreezeEffect;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(Dist.CLIENT)
public class FreezeEffectVisuals {
    public static final Supplier<RenderType> MATERIAL = Suppliers.memoize(
            () -> RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/blue_ice.png"))
    );

    @SubscribeEvent
    public static void onLivingRender(RenderLivingEvent.Post<?, ?> event) {
        var attributes = event.getEntity().getAttributes();
        var isFrozen = Optional.ofNullable(attributes.getInstance(Attributes.MOVEMENT_SPEED))
                .map(instance -> instance.getModifier(FreezeEffect.SPEED_MODIFIER_UUID))
                .filter(modifier -> modifier.amount() <= -1)
                .isPresent();
        if (!isFrozen) {
            return;
        }
        var bb = event.getEntity().getBoundingBox();
        var scale = (float) (bb.getXsize() + bb.getZsize()) * 0.8F;
        var pose = event.getPoseStack();
        var builder = event.getMultiBufferSource().getBuffer(MATERIAL.get());

        pose.pushPose();
        {
            pose.translate(0, scale * 0.45, 0);
            pose.scale(scale, scale, scale);
            renderCube(pose, builder, event.getPackedLight());
        }
        pose.popPose();
    }

    public static void renderCube(PoseStack pos, VertexConsumer builder, int packedLight) {
        var mesh = MESH_DATA;
        var pose = pos.last().pose();
        var norm = pos.last().normal();
        for (int i = 0; i < mesh.length / 5; i++) {
            var x = mesh[i * 5] - 0.5F;
            var y = mesh[i * 5 + 1] - 0.5F;
            var z = mesh[i * 5 + 2] - 0.5F;
            var u = mesh[i * 5 + 3];
            var v = mesh[i * 5 + 4];
            var nx = normals[(i / 4) * 3];
            var ny = normals[(i / 4) * 3 + 1];
            var nz = normals[(i / 4) * 3 + 2];
            Vector3f transform = norm.transform(new Vector3f(nx, ny, nz));
            builder.addVertex(pose, x, y, z)
                    .setColor(255, 255, 255, 128)
                    .setUv(u, v)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(transform.x(), transform.y(), transform.z());
        }
    }

    // x, y, z, u, v
    private static final float[] MESH_DATA = {
            1, 1, 1, 0, 0,
            1, 1, 0, 1, 0,
            1, 0, 0, 1, 1,
            1, 0, 1, 0, 1,

            0, 1, 0, 0, 0,
            1, 1, 0, 1, 0,
            1, 1, 1, 1, 1,
            0, 1, 1, 0, 1,

            0, 1, 1, 0, 0,
            1, 1, 1, 1, 0,
            1, 0, 1, 1, 1,
            0, 0, 1, 0, 1,

            0, 1, 0, 0, 0,
            0, 1, 1, 1, 0,
            0, 0, 1, 1, 1,
            0, 0, 0, 0, 1,

            1, 0, 1, 0, 0,
            1, 0, 0, 0, 1,
            0, 0, 0, 1, 1,
            0, 0, 1, 1, 0,

            1, 1, 0, 0, 0,
            0, 1, 0, 1, 0,
            0, 0, 0, 1, 1,
            1, 0, 0, 0, 1,
    };

    private static final float[] normals = {
            +1, 0, 0,
            0, +1, 0,
            0, 0, +1,

            -1, 0, 0,
            0, -1, 0,
            0, 0, -1,
    };
}
