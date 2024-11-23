package net.citizensnpcs.trait.versioned;

import org.bukkit.entity.Villager;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("villagertrait")
public class VillagerTrait extends Trait {
    public VillagerTrait() {
        super("villagertrait");
    }

    @Override
    public void run() {
        if (!(npc.getEntity() instanceof Villager))
            return;
    }

}
