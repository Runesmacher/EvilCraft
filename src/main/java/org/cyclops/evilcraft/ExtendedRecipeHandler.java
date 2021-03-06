package org.cyclops.evilcraft;

import com.google.common.collect.Iterables;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.init.RecipeHandler;
import org.cyclops.cyclopscore.recipe.event.IRecipeOutputObserver;
import org.cyclops.cyclopscore.recipe.event.ObservableShapedRecipe;
import org.cyclops.cyclopscore.recipe.xml.IRecipeConditionHandler;
import org.cyclops.cyclopscore.recipe.xml.IRecipeTypeHandler;
import org.cyclops.evilcraft.api.broom.IBroomPart;
import org.cyclops.evilcraft.block.*;
import org.cyclops.evilcraft.core.broom.BroomParts;
import org.cyclops.evilcraft.core.item.ItemBlockFluidContainer;
import org.cyclops.evilcraft.core.recipe.BloodExtractorCombinationRecipe;
import org.cyclops.evilcraft.core.recipe.BroomPartCombinationRecipe;
import org.cyclops.evilcraft.core.recipe.DisplayStandRecipe;
import org.cyclops.evilcraft.core.recipe.ItemBlockFluidContainerCombinationRecipe;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeComponent;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeProperties;
import org.cyclops.evilcraft.core.recipe.xml.BloodInfuserRecipeTypeHandler;
import org.cyclops.evilcraft.core.recipe.xml.EnvironmentalAccumulatorRecipeTypeHandler;
import org.cyclops.evilcraft.core.weather.WeatherType;
import org.cyclops.evilcraft.enchantment.EnchantmentPoisonTip;
import org.cyclops.evilcraft.enchantment.EnchantmentPoisonTipConfig;
import org.cyclops.evilcraft.fluid.Poison;
import org.cyclops.evilcraft.fluid.PoisonConfig;
import org.cyclops.evilcraft.item.*;

import java.util.Map;
import java.util.Set;

/**
 * An extended recipe handler.
 * @author rubensworks
 */
public class ExtendedRecipeHandler extends RecipeHandler {

    public ExtendedRecipeHandler(ModBase mod, String... fileNames) {
        super(mod, fileNames);
    }

    @Override
    protected void registerHandlers(Map<String, IRecipeTypeHandler> recipeTypeHandlers, Map<String, IRecipeConditionHandler> recipeConditionHandlers) {
        super.registerHandlers(recipeTypeHandlers, recipeConditionHandlers);
        recipeTypeHandlers.put("evilcraft:bloodinfuser", new BloodInfuserRecipeTypeHandler());
        recipeTypeHandlers.put("evilcraft:environmentalaccumulator",
                new EnvironmentalAccumulatorRecipeTypeHandler());
    }

    @Override
    protected void registerRecipeSorters() {
        super.registerRecipeSorters();
        RecipeSorter.register(Reference.MOD_ID + "containercombination", ItemBlockFluidContainerCombinationRecipe.class,
                RecipeSorter.Category.SHAPELESS, "after:forge:shapedore");
        RecipeSorter.register(Reference.MOD_ID + "bloodextractorcombination", BloodExtractorCombinationRecipe.class,
                RecipeSorter.Category.SHAPELESS, "after:forge:shapedore");
        RecipeSorter.register(Reference.MOD_ID + "broomcombination", BroomPartCombinationRecipe.class,
                RecipeSorter.Category.SHAPELESS, "after:forge:shapedore");
        RecipeSorter.register(Reference.MOD_ID + "displaystand", DisplayStandRecipe.class,
                RecipeSorter.Category.SHAPED, "after:forge:shapedore");
    }

    @Override
    protected void loadPredefineds(Map<String, ItemStack> predefinedItems, Set<String> predefinedValues) {
        super.loadPredefineds(predefinedItems, predefinedValues);
        if(Configs.isEnabled(EnchantmentPoisonTipConfig.class)) {
            ItemStack poisonTipEnchant = new ItemStack(Items.ENCHANTED_BOOK);
            Enchantment enchant = EnchantmentPoisonTip.getInstance();
            Items.ENCHANTED_BOOK.addEnchantment(poisonTipEnchant, new EnchantmentData(enchant,
                    enchant.getMinLevel()));
            predefinedItems.put("evilcraft:enchanted_book_poisonTip", poisonTipEnchant);
        }

        if(Configs.isEnabled(WeatherContainerConfig.class)) {
            ItemStack lightningWeatherContainer = new ItemStack(WeatherContainer.getInstance(), 1,
                    WeatherContainer.WeatherContainerTypes.LIGHTNING.ordinal());
            predefinedItems.put("evilcraft:lightningWeatherContainer",
                    lightningWeatherContainer);
        }

        if(Configs.isEnabled(InvertedPotentiaConfig.class)) {
            ItemStack invertedPotentiaEmpowered = new ItemStack(InvertedPotentia.getInstance(),
                    1, InvertedPotentia.EMPOWERED_META);
            predefinedItems.put("evilcraft:invertedPotentiaEmpowered",
                    invertedPotentiaEmpowered);
        }

        if(Configs.isEnabled(VengeancePickaxeConfig.class)) {
            ItemStack vengeancePickaxeFortune = VengeancePickaxe.createCraftingResult();
            predefinedItems.put("evilcraft:vengeancePickaxeFortune",
                    vengeancePickaxeFortune);
        }

        if(Configs.isEnabled(BoxOfEternalClosureConfig.class)) {
            predefinedItems.put("evilcraft:boxOfEternalClosureFilled",
                    BoxOfEternalClosure.boxOfEternalClosureFilled);
        }

        if(Configs.isEnabled(InvertedPotentiaConfig.class)) {
            ItemStack empoweredInvertedPotentia = new ItemStack(InvertedPotentia.getInstance());
            InvertedPotentia.getInstance().empower(empoweredInvertedPotentia);
            predefinedItems.put("evilcraft:empoweredInvertedPotentia",
                    empoweredInvertedPotentia);
        }

        if(Configs.isEnabled(DarkTankConfig.class)) {
            ItemStack darkTankx9 = new ItemStack(DarkTank.getInstance());
            ItemBlockFluidContainer item = ((ItemBlockFluidContainer) darkTankx9.getItem());
            item.setCapacity(darkTankx9, item.getCapacity(darkTankx9) * 9);
            predefinedItems.put("evilcraft:darkTankx9", darkTankx9);
        }

        if(PotentiaSphereConfig.enderPearlRecipe) {
            predefinedValues.add("evilcraft:enderPearlRecipe");
        }

        if(Configs.isEnabled(VeinSwordConfig.class)) {
            ItemStack veinSwordLooting = VeinSword.createCraftingResult();
            predefinedItems.put("evilcraft:veinSwordLooting",
                    veinSwordLooting);
        }

        if(WeatherContainerConfig.shapelessRecipes) {
            predefinedValues.add("evilcraft:shapelessRecipes");
        }

        if(Configs.isEnabled(BroomConfig.class)) {
            for (IBroomPart broomPart : BroomParts.REGISTRY.getParts()) {
                String id = String.format("%s:%s:%s",
                        broomPart.getId().getResourceDomain(), "broompart", broomPart.getId().getResourcePath());
                ItemStack itemStack = Iterables.getFirst(BroomParts.REGISTRY.getItemsFromPart(broomPart), null);
                if (itemStack != null) {
                    predefinedItems.put(id, itemStack);
                }
            }

        }

        predefinedItems.put("evilcraft:potion_weakness", PotionUtils.addPotionToItemStack(
                new ItemStack(Items.POTIONITEM), PotionType.getPotionTypeForName("weakness")));

        if(Configs.isEnabled(PoisonConfig.class)) {
            ItemStack poisonBucket = new ItemStack(Items.BUCKET);
            IFluidHandler fluidHandler = FluidUtil.getFluidHandler(poisonBucket);
            fluidHandler.fill(new FluidStack(Poison.getInstance(), Fluid.BUCKET_VOLUME), true);
            predefinedItems.put("evilcraft:bucketPoison", poisonBucket);
        }

        ItemStack potionPoison = new ItemStack(Items.POTIONITEM);
        PotionUtils.addPotionToItemStack(potionPoison, PotionType.getPotionTypeForName("poison"));
        predefinedItems.put("minecraft:potionPoison", potionPoison);
    }

    @Override
    protected void registerCustomRecipes() {
        super.registerCustomRecipes();
        // Entangled Chalice unique id
        if(Configs.isEnabled(EntangledChaliceConfig.class)
                && Configs.isEnabled(DarkGemConfig.class)
                && Configs.isEnabled(CorruptedTearConfig.class)) {
            Item tear = CorruptedTearConfig._instance.getItemInstance();
            GameRegistry.addRecipe(new ObservableShapedRecipe(3, 3, new ItemStack[]{
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(tear), new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(DarkGem.getInstance()), new ItemStack(Items.GOLD_INGOT), new ItemStack(DarkGem.getInstance()),
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT)
            }, new ItemStack(Item.getItemFromBlock(EntangledChalice.getInstance()), 2), new IRecipeOutputObserver() {
                @Override
                public ItemStack getRecipeOutput(InventoryCrafting craftingGrid, ItemStack output) {
                    ItemStack newStack = output.copy();
                    EntangledChaliceItem item = (EntangledChaliceItem) Item.getItemFromBlock(EntangledChalice.getInstance());
                    item.setNextTankID(newStack);
                    return newStack;
                }
            }));

            GameRegistry.addRecipe(new ObservableShapedRecipe(3, 3, new ItemStack[]{
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(tear), new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(DarkGem.getInstance()), new ItemStack(Item.getItemFromBlock(EntangledChalice.getInstance()), 1, OreDictionary.WILDCARD_VALUE), new ItemStack(DarkGem.getInstance()),
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT)
            }, new ItemStack(Item.getItemFromBlock(EntangledChalice.getInstance()), 2), new IRecipeOutputObserver() {
                @Override
                public ItemStack getRecipeOutput(InventoryCrafting craftingGrid, ItemStack output) {
                    ItemStack newStack = output.copy();
                    EntangledChaliceItem item = (EntangledChaliceItem) Item.getItemFromBlock(EntangledChalice.getInstance());
                    String tankID = item.getTankID(craftingGrid.getStackInSlot(4));
                    item.setTankID(newStack, tankID);
                    return newStack;
                }
            }));
        }

        // Dark tank upgrades
        if(Configs.isEnabled(DarkTankConfig.class)) {
            for(int i = 1; i < 9; i++) {
                ItemBlockFluidContainer tankItem = (ItemBlockFluidContainer) Item.getItemFromBlock(DarkTank.getInstance());
                GameRegistry.addRecipe(new ItemBlockFluidContainerCombinationRecipe(i, tankItem));
            }
        }

        // Blood Extractor upgrades
        if(Configs.isEnabled(BloodExtractorConfig.class) && Configs.isEnabled(DarkTankConfig.class)) {
            for(int i = 1; i < 9; i++) {
                GameRegistry.addRecipe(new BloodExtractorCombinationRecipe(i));
            }
        }

        // Broom crafting
        if(Configs.isEnabled(BroomConfig.class)) {
            for(int i = 1; i < 9; i++) {
                GameRegistry.addRecipe(new BroomPartCombinationRecipe(i));
            }
        }

        if (Configs.isEnabled(EnvironmentalAccumulatorConfig.class)) {
            ItemStack outputStack = null;
            String recipeName = null;

            // Add the different weather container recipes
            if (Configs.isEnabled(WeatherContainerConfig.class)) {
                ItemStack emptyContainer = WeatherContainer.createItemStack(WeatherContainer.WeatherContainerTypes.EMPTY, 1);
                WeatherType[] weatherInputs = {WeatherType.CLEAR, WeatherType.LIGHTNING, WeatherType.RAIN};
                WeatherType[] weatherOutputs = {WeatherType.RAIN, WeatherType.RAIN, WeatherType.CLEAR};

                for (int i=0; i < weatherInputs.length; ++i) {
                    recipeName = "WeatherContainer" + weatherInputs[i].getClass().getSimpleName();
                    outputStack = WeatherContainer.createItemStack(
                            WeatherContainer.WeatherContainerTypes.getWeatherContainerType(weatherInputs[i]), 1);

                    EnvironmentalAccumulator.getInstance().getRecipeRegistry().registerRecipe(
                            recipeName,
                            new EnvironmentalAccumulatorRecipeComponent(
                                    emptyContainer,
                                    weatherInputs[i]
                            ),
                            new EnvironmentalAccumulatorRecipeComponent(
                                    outputStack,
                                    weatherOutputs[i]
                            ),
                            new EnvironmentalAccumulatorRecipeProperties()
                    );
                }
            }

            // Add biome extract recipes
            if(Configs.isEnabled(BiomeExtractConfig.class) && BiomeExtractConfig.hasRecipes) {
                ItemStack emptyContainer = new ItemStack(BiomeExtract.getInstance());
                ItemStack filledContainer = BiomeExtract.getInstance().createItemStack(null, 1); // Still dummy!
                filledContainer.setItemDamage(OreDictionary.WILDCARD_VALUE);
                EnvironmentalAccumulator.getInstance().getRecipeRegistry().registerRecipe(
                        recipeName,
                        new EnvironmentalAccumulatorRecipeComponent(
                                emptyContainer,
                                WeatherType.ANY
                        ),
                        new EnvironmentalAccumulatorRecipeComponent(
                                filledContainer,
                                WeatherType.ANY
                        ),
                        new EnvironmentalAccumulatorRecipeProperties(1000, BiomeExtractConfig.envirAccCooldownTime, -1.0D, null, new EnvironmentalAccumulatorRecipeProperties.IEAResultOverride() {
                            @Override
                            public ItemStack getResult(IBlockAccess world, BlockPos pos, ItemStack originalResult) {
                                World worldSafe = (World) world;
                                Biome biome = worldSafe.getBiome(pos);
                                if(BiomeExtractConfig._instance.isCraftingBlacklisted(Biome.getIdForBiome(biome))) {
                                    return BiomeExtract.getInstance().createItemStack(null, 1);
                                } else {
                                    return BiomeExtract.getInstance().createItemStack(biome, 1);
                                }
                            }
                        })
                );
            }

            // Display Stand crafting
            if(Configs.isEnabled(DisplayStandConfig.class)) {
                getTaggedOutput().put("displayStands", DisplayStand.getInstance().
                        getTypedDisplayStandItem(Blocks.PLANKS.getDefaultState()));
                GameRegistry.addRecipe(new DisplayStandRecipe(OreDictionary.getOres(Reference.DICT_WOODPLANK)));
            }
        }
    }
}
