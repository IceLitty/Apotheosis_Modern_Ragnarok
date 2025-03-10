package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FireDotEffect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;
import java.util.function.IntSupplier;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;
import static mod.chloeprime.apotheosismodernragnarok.common.ModContent.*;

@EventBusSubscriber
public class ElementalDamages {
    private ElementalDamages() {
    }

    private static final int TEN_SECONDS = 20 * 10;
    private static final int ONE_MINUTE = 20 * 60;
    private static final String PDKEY_LAST_FIRE_HIT_TICK = loc("last_fire_hit_timestamp").toString();
    private static final String PDKEY_LAST_FIRE_AMOUNT = loc("last_fire_hit_amount").toString();

    @SubscribeEvent
    public static void onPostHurt(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        var victim = event.getHurtEntity();
        if (shooter == null || victim == null || victim.getType().is(Tags.GUN_IMMUNE)) {
            return;
        }
        var bullet = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING).getDirectEntity();
        var fireDmg = (float) shooter.getAttributeValue(ALObjects.Attributes.FIRE_DAMAGE);
        var coldDmg = (float) shooter.getAttributeValue(ALObjects.Attributes.COLD_DAMAGE);
        if (fireDmg <= 0 && coldDmg <= 0) {
            return;
        }
        if (fireDmg > 0 && coldDmg > 0) {
            applyIceAndFireDamage(victim, fireDmg + coldDmg, bullet, shooter);
        } else if (fireDmg > 0) {
            applyFireDamage(victim, fireDmg, shooter.getMainHandItem(), () -> {
                var gun = shooter.getMainHandItem();
                var isBoltAction = LootCategory.forItem(gun) == LootCategories.BOLT_ACTION &&
                        AbstractAffix.isStillHoldingTheSameGun(gun, event.getGunId());
                return isBoltAction ? ONE_MINUTE : TEN_SECONDS;
            }, bullet, shooter);
        } else {
            applyIceDamage(victim, coldDmg, shooter.getMainHandItem(), () -> {
                var gun = shooter.getMainHandItem();
                var isBoltAction = LootCategory.forItem(gun) == LootCategories.BOLT_ACTION &&
                        AbstractAffix.isStillHoldingTheSameGun(gun, event.getGunId());
                return isBoltAction ? ONE_MINUTE : 3 * TEN_SECONDS;
            }, bullet, shooter);
        }
    }

    public static void applyFireDamage(Entity victim, float value, ItemStack gun, IntSupplier duration, Entity bullet, LivingEntity shooter) {
        if (victim.fireImmune()) {
            return;
        }
        victim.invulnerableTime = 0;
        var effective = victim.hurt(victim.damageSources().source(DamageTypes.BULLET_FIRE, bullet, shooter), value);
        if (!effective) {
            return;
        }
        if (victim instanceof LivingEntity living) {
            var pd = living.getPersistentData();
            var now = living.level().getGameTime();
            var lastHit = pd.getLong(PDKEY_LAST_FIRE_HIT_TICK);
            var lastAmount = pd.getFloat(PDKEY_LAST_FIRE_AMOUNT);
            var isShotgun = LootCategories.SHOTGUN.isValid(gun);
            if (now - lastHit <= (isShotgun ? 1 : 0)) {
                value += lastAmount;
            } else {
                pd.putLong(PDKEY_LAST_FIRE_HIT_TICK, now);
            }
            pd.putFloat(PDKEY_LAST_FIRE_AMOUNT, value);

            var amplifier = Mth.clamp((int) value - 1, 0, 126);
            living.addEffect(new MobEffectInstance(MobEffects.FIRE_DOT, duration.getAsInt(), amplifier), shooter);
        }
    }

    public static void applyIceDamage(Entity victim, float value, ItemStack gun, IntSupplier duration, Entity bullet, LivingEntity shooter) {
        victim.invulnerableTime = 0;
        var effective = victim.hurt(victim.damageSources().source(DamageTypes.BULLET_ICE, bullet, shooter), value);
        if (!effective) {
            return;
        }
        if (!(victim instanceof LivingEntity livingVictim)) {
            return;
        }
        // 20伤 = 100%触发
        var triggerRate = value * 0.05F;
        if (livingVictim.getRandom().nextFloat() > triggerRate) {
            return;
        }
        var effect = MobEffects.FREEZE;
        var amp = Optional.ofNullable(livingVictim.getEffect(effect))
                .map(MobEffectInstance::getAmplifier)
                .orElse(-1);
        var dur = duration.getAsInt();
        var newAmp = Mth.clamp(amp + 1, 0, 126);
        livingVictim.addEffect(new MobEffectInstance(effect, dur, newAmp));
    }

    public static void applyIceAndFireDamage(Entity victim, float value, Entity bullet, LivingEntity shooter) {
        victim.invulnerableTime = 0;
        var effective = victim.hurt(victim.damageSources().source(DamageTypes.BULLET_IAF, bullet, shooter), value);
        victim.invulnerableTime = 0;

        if (!effective) {
            return;
        }

        victim.playSound(SoundEvents.FIRE_EXTINGUISH);

        if (victim instanceof LivingEntity lv) {
            FireDotEffect.createSmoke(lv);
        }
    }
}
