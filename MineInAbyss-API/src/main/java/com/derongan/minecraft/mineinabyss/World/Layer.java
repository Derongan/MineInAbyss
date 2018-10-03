package com.derongan.minecraft.mineinabyss.World;

import com.derongan.minecraft.deeperworld.world.section.Section;
import com.derongan.minecraft.mineinabyss.Ascension.Effect.AscensionEffectBuilder;

import java.util.List;

public interface Layer {
    /**
     * Gets the name of this Layer. This name may not match the worldname.
     * @return The name of the layer
     */
    String getName();

    /**
     * Get the sub header
     */
    String getSub();

    /**
     * Gets the index of this Layer. Higher layers have lower index
     * @return The index of this layer
     */
    int getIndex();

    /**
     * Gets the sections in this layer. This list is immutable.
     * @return An immutable list containing the sections of this layer
     */
    List<Section> getSections();


    /**
     * Returns whether or not this layer contains this section
     */
    boolean containsSection(Section section);


    /**
     * Gets the effects of ascending on this layer
     */
    List<AscensionEffectBuilder> getAscensionEffects();


    /**
     * Get custom death message suffix for this layer
     * @return The custom death suffix to use
     */
    default String getDeathMessage(){
        return " in the depths of the abyss";
    }
}
