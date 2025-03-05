package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.affix.effect.MobEffectAffix;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.PotionAffixBase;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Map;
import java.util.Set;

public class RatedPotionAffix extends PotionAffixBase {

    public static final Codec<RatedPotionAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    AffixDefinition.CODEC.fieldOf("definition").forGetter(Affix::definition),
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    Target.getCodec().fieldOf("target").forGetter(a -> a.target),
                    LootRarity.mapCodec(EffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(Codec.FLOAT, "rate", 0F).forGetter(a -> a.rate),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
            .apply(inst, RatedPotionAffix::new));

    protected final float rate;

    public RatedPotionAffix(AffixDefinition definition, Holder<MobEffect> effect, Target target, Map<LootRarity, EffectData> values, float rate, Set<LootCategory> types, boolean stackOnReapply) {
        super(definition, effect, target, values, types, stackOnReapply);
        this.rate = rate;
    }

    @Override
    public MutableComponent getDescription(AffixInstance _inst, AttributeTooltipContext ctx) {
        LootRarity rarity = _inst.getRarity();
        float level = _inst.level();
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        var rate = getTriggerRate(rarity, level);
        MutableComponent comp = this.target.toComponent("%.2f%%".formatted(100 * rate), MobEffectAffix.toComponent(inst, level));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Component getAugmentingText(AffixInstance _inst, AttributeTooltipContext ctx) {
        LootRarity rarity = _inst.getRarity();
        float level = _inst.level();
        var rate = this.getTriggerRate(rarity, level);
        var minRate = this.getTriggerRate(rarity, 0);
        var maxRate = this.getTriggerRate(rarity, 1);

        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(fmtPercents(rate, minRate, maxRate), MobEffectAffix.toComponent(inst, level));

        MobEffectInstance min = this.values.get(rarity).build(this.effect, 0);
        MobEffectInstance max = this.values.get(rarity).build(this.effect, 1);

        if (min.getAmplifier() != max.getAmplifier()) {
            // Vanilla ships potion.potency.0 as an empty string, so we have to fix that here
            Component minComp = min.getAmplifier() == 0 ? Component.literal("I") : Component.translatable("potion.potency." + min.getAmplifier());
            Component maxComp = Component.translatable("potion.potency." + max.getAmplifier());
            comp.append(valueBounds(minComp, maxComp));
        }

        if (!this.effect.value().isInstantenous() && min.getDuration() != max.getDuration()) {
            Component minComp = MobEffectUtil.formatDuration(min, 1, ModContent.TICKS_PER_SECOND);
            Component maxComp = MobEffectUtil.formatDuration(max, 1, ModContent.TICKS_PER_SECOND);
            comp.append(valueBounds(minComp, maxComp));
        }

        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }

        return comp;
    }

    protected float getTriggerRate(LootRarity rarity, float level) {
        return this.values.get(rarity).rate().get(level);
    }

    public void applyEffect(LivingEntity target, LootRarity rarity, float level) {
        if (target.level().isClientSide()) {
            super.applyEffect(target, rarity, level);
            return;
        }

        // 概率检定
        if (target.getRandom().nextFloat() > getTriggerRate(rarity, level)) {
            return;
        }

        super.applyEffect(target, rarity, level);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

}
