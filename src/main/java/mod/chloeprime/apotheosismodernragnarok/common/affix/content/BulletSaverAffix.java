package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.*;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import mod.chloeprime.apotheosismodernragnarok.mixin.tacz.MixinModernKineticGunScriptAPI.BulletSaverAffixMixin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 射击时概率不消耗子弹。
 * <p/>
 * 类型名 apotheosis_modern_ragnarok:bullet_saver
 * 实例名 apotheosis_modern_ragnarok:frugality
 * <p/>
 * @see BulletSaverAffixMixin 实现
 */
public class BulletSaverAffix extends AbstractValuedAffix {

    public static final Codec<BulletSaverAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    AffixDefinition.CODEC.fieldOf("definition").forGetter(Affix::definition),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    ExtraCodecs.GEM_BONUS_VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(builder, BulletSaverAffix::new));

    public static final DynamicHolder<Affix> INSTANCE
            = AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc("frugality"));

    public BulletSaverAffix(
            AffixDefinition definition,
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values) {
        super(definition, categories, values);
    }

    public static boolean check(RandomSource context, ItemStack stack) {
        return Optional.ofNullable(AffixHelper.getAffixes(stack).get(ModContent.Affix.BULLET_SAVER))
                .map(instance -> instance.affix().get() instanceof BulletSaverAffix affix && affix.check(context, stack, instance))
                .orElse(false);
    }

    public boolean check(RandomSource context, ItemStack stack, AffixInstance instance) {
        return context.nextFloat() <= getValue(stack, instance.rarity().get(), instance.level());
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        ItemStack stack = inst.stack();
        LootRarity rarity = inst.getRarity();
        float level = inst.level();
        var rate = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmtPercent(rate)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(AffixInstance inst, AttributeTooltipContext ctx) {
        ItemStack stack = inst.stack();
        LootRarity rarity = inst.getRarity();
        float level = inst.level();
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtPercents(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
