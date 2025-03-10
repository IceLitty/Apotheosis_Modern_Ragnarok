package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.EffectHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;

public class FreezeEffect extends MobEffect {
    public static final ResourceLocation SPEED_MODIFIER_UUID = ResourceLocation.fromNamespaceAndPath(ApotheosisModernRagnarok.MOD_ID, "speed_modifier");
    public static final ResourceLocation DAMAGE_MODIFIER_UUID = ResourceLocation.fromNamespaceAndPath(ApotheosisModernRagnarok.MOD_ID, "damage_modifier");
    public static final int FROZEN_THRESHOLD = 5;
    public static final String PDKEY_NO_AI_BEFORE = ApotheosisModernRagnarok.loc("no_ai_before").toString();

    public FreezeEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 2 == 0;
    }

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity owner, int amplifier) {
        if (amplifier < FROZEN_THRESHOLD) {
            if (owner.isOnFire()) {
                owner.extinguishFire();
                owner.removeEffect(ModContent.MobEffects.FREEZE);
                return false;
            }
            var rate = (amplifier + 1) / 5F;
            if (!owner.level().isClientSide && owner.getRandom().nextFloat() <= rate) {
                EffectHelper.createSurroundingParticles(ParticleTypes.SNOWFLAKE, owner, 1, Vec3.ZERO);
            }
        } else {
            if (owner.isOnFire()) {
                owner.extinguishFire();
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onAdded(MobEffectEvent.Added event) {
        if (event.getEntity().isOnFire()) {
            event.getEntity().extinguishFire();
        }
        var instance = event.getEffectInstance();
        if (instance.getEffect() != this || instance.getAmplifier() < FROZEN_THRESHOLD) {
            return;
        }
        if (event.getEntity() instanceof Mob mob) {
            if (!mob.getPersistentData().contains(PDKEY_NO_AI_BEFORE)) {
                mob.getPersistentData().putBoolean(PDKEY_NO_AI_BEFORE, mob.isNoAi());
            }
            mob.setNoAi(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() != this) {
            return;
        }
        onRemovedOrExpired(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExpired(MobEffectEvent.Expired event) {
        if (Objects.requireNonNull(event.getEffectInstance()).getEffect() != this) {
            return;
        }
        onRemovedOrExpired(event);
    }

    private void onRemovedOrExpired(MobEffectEvent event) {
        var owner = event.getEntity();
        if (owner instanceof Mob mob && owner.getPersistentData().contains(PDKEY_NO_AI_BEFORE, Tag.TAG_BYTE)) {
            mob.setNoAi(owner.getPersistentData().getBoolean(PDKEY_NO_AI_BEFORE));
            mob.getPersistentData().remove(PDKEY_NO_AI_BEFORE);
        }
    }

    public static FreezeEffect create() {
        return (FreezeEffect) new FreezeEffect(MobEffectCategory.HARMFUL, new Color(128, 255, 255, 255).getRGB())
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID, -0.19, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_UUID, -0.19, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
