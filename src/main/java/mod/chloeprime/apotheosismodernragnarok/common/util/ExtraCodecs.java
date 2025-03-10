package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.mojang.serialization.Codec;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;

import java.util.Map;
import java.util.Set;

public class ExtraCodecs {
    public static final Codec<Map<LootRarity, Double>> COEFFICIENT_BY_RARITY = Codec.unboundedMap(
            LootRarity.CODEC,
            Codec.doubleRange(0, Double.MAX_VALUE));

    public static final Codec<Map<LootCategory, Double>> COEFFICIENT_BY_CATEGORY = Codec.unboundedMap(
            LootCategory.CODEC,
            Codec.doubleRange(0, Double.MAX_VALUE));

    public static final Codec<Map<LootRarity, StepFunction>> GEM_BONUS_VALUES_CODEC = LootRarity.mapCodec(StepFunction.CODEC);;

    public static final Codec<AffixType> AFFIX_TYPE = PlaceboCodecs.enumCodec(AffixType.class);

    public static final Codec<Set<LootCategory>> LOOT_CATEGORY_SET = PlaceboCodecs.setOf(LootCategory.CODEC);
}
