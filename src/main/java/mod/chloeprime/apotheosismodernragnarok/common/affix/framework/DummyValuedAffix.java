package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Set;

public class DummyValuedAffix extends AbstractValuedAffix {
    public static final Codec<DummyValuedAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    AffixDefinition.CODEC.fieldOf("definition").forGetter(Affix::definition),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    ExtraCodecs.GEM_BONUS_VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(inst, DummyValuedAffix::new));

    public DummyValuedAffix(AffixDefinition definition, Set<LootCategory> categories, Map<LootRarity, StepFunction> values) {
        super(definition, categories, values);
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        ItemStack stack = inst.stack();
        LootRarity rarity = inst.getRarity();
        float level = inst.level();
        var percent = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(AffixInstance inst, AttributeTooltipContext ctx) {
        ItemStack stack = inst.stack();
        LootRarity rarity = inst.getRarity();
        float level = inst.level();
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtAugmenting(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
