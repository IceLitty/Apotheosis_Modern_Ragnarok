package mod.chloeprime.apotheosismodernragnarok.common;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyCoefficientAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummySpecialAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.*;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GemInjectionRegistry;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FireDotEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FreezeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.ModContainer;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.MOD_ID;
import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static final Integer TICKS_PER_SECOND = 20;

    public static final class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
        public static final DeferredHolder<Item, SalvageItem> ANCIENT_MATERIAL = REGISTRY.register(
                "izanagi_object",
                () -> new SalvageItem(RarityRegistry.INSTANCE.holder(Apotheosis.loc("ancient")), new Item.Properties())
        );

        private Items() {}
    }

    public static final class LootCategories extends ExtraLootCategories {
        private LootCategories() {}
    }

    public static final class Affix {
        public static final DynamicHolder<ArmorSquashAffix>         ARMOR_SQUASH = holder("all_gun/special/armor_squash");
        public static final DynamicHolder<BulletSaverAffix>         BULLET_SAVER = holder("all_gun/special/frugality");
        public static final DynamicHolder<ExplosionOnHeadshotAffix> HEAD_EXPLODE = holder("all_gun/special/head_explode");
        public static final DynamicHolder<MagicalShotAffix>         MAGICAL_SHOT = holder("all_gun/special/magical_shot");
//        public static final DynamicHolder<DummyCoefficientAffix>    SPECTRAL_BULLET = holder("all_gun/special/spectral");

        private static <T extends dev.shadowsoffire.apotheosis.affix.Affix> DynamicHolder<T> holder(String path) {
            return (DynamicHolder<T>) AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc(path));
        }

        private Affix() {}
    }

    public static final class MobEffects {
        private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MOD_ID);
        public static final DeferredHolder<MobEffect, FireDotEffect> FIRE_DOT = REGISTRY.register("fire_dot", FireDotEffect::create);
        public static final DeferredHolder<MobEffect, FreezeEffect> FREEZE = REGISTRY.register("freeze", FreezeEffect::create);

        private MobEffects() {
        }
    }

    public static final class Tags {
        public static final TagKey<EntityType<?>> GUN_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, loc("gun_immune"));
    }

    public static final class DamageTypes {
        public static final ResourceKey<DamageType> BULLET_ICE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_ice"));
        public static final ResourceKey<DamageType> BULLET_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_fire"));
        public static final ResourceKey<DamageType> BULLET_IAF = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_iceandfire"));
    }

    public static final class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MOD_ID);
        public static final DeferredHolder<SoundEvent, SoundEvent> ARMOR_CRACK = registerSound("affix.armor_break");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHOTGUN = registerSound("affix.magical_shot.shotgun");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SEMIAUTO = registerSound("affix.magical_shot.semi_auto");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_FULLAUTO = registerSound("affix.magical_shot.full_auto");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_BOLT_ACTION = registerSound("affix.magical_shot.bolt_action");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_FIREBALL = registerSound("affix.magical.fireball");
        public static final DeferredHolder<SoundEvent, SoundEvent> HEAD_EXPLOSION = registerSound("affix.head_explosion");
        public static final DeferredHolder<SoundEvent, SoundEvent> CRITICAL_HIT = registerSound("critical_hit");

        private Sounds() {}
    }

    public static void setup() {
//        ExtraLootCategories.init(); // move to newer version
        GemInjectionRegistry.INSTANCE.registerToBus();
        AffixRegistry.INSTANCE.registerCodec(loc("bullet_saver"), BulletSaverAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("armor_squash"), ArmorSquashAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("explode_on_headshot"), ExplosionOnHeadshotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_rated"), RatedPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_ads"), AdsPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("magical_shot"), MagicalShotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_valued"), DummyValuedAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_coefficient"), DummyCoefficientAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_special"), DummySpecialAffix.CODEC);
    }

    public static void init0(IEventBus bus) {
        Items.REGISTRY.register(bus);
        MobEffects.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
    }

    public static void init1(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String path) {
        return Sounds.REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ApotheosisModernRagnarok.loc(path)));
    }

    private ModContent() {}
}
