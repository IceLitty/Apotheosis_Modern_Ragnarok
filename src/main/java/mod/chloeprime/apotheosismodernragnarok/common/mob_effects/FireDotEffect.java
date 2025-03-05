package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import com.google.common.collect.ImmutableList;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.EffectHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class FireDotEffect extends MobEffect {
    public static final List<EffectCure> CURES = ImmutableList.of();

    public FireDotEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        NeoForge.EVENT_BUS.register(this);
    }

    public static FireDotEffect create() {
        return new FireDotEffect(MobEffectCategory.HARMFUL, new Color(255, 128, 0, 255).getRGB());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % 5 == 0;
    }

    @SubscribeEvent
    public void onEffectApplied(MobEffectEvent.Added event) {
        var instance = event.getEffectInstance();
        if (instance.getEffect() != this) {
            return;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        event.getEntity().setRemainingFireTicks(fireTicks);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        cures.addAll(CURES);
    }

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity owner, int pAmplifier) {
        if (!owner.level().isClientSide) {
            if (owner.fireImmune()) {
                owner.removeEffect(ModContent.MobEffects.FIRE_DOT);
                return false;
            }
            var freeze = ModContent.MobEffects.FREEZE;
            if (owner.hasEffect(freeze)) {
                owner.removeEffect(ModContent.MobEffects.FIRE_DOT);
                owner.removeEffect(freeze);
                owner.playSound(SoundEvents.FIRE_EXTINGUISH);
                createSmoke(owner);
                return false;
            }
        }
        var instance = owner.getEffect(ModContent.MobEffects.FIRE_DOT);
        if (instance == null) {
            return false;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        owner.setRemainingFireTicks(fireTicks);
        return true;
    }

    public static final Vec3 SMOKE_MOTION = new Vec3(0, 0.125, 0);
    public static void createSmoke(LivingEntity owner) {
        EffectHelper.createSurroundingParticles(ParticleTypes.LARGE_SMOKE, owner, 8, SMOKE_MOTION);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide || !event.getSource().is(DamageTypes.ON_FIRE)) {
            return;
        }
        var instance = event.getEntity().getEffect(ModContent.MobEffects.FIRE_DOT);
        if (instance == null) {
            return;
        }
        event.setNewDamage(event.getOriginalDamage() * (instance.getAmplifier() + 1));
    }
}
