package net.citizensnpcs.trait;

import org.bukkit.entity.Ocelot;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.util.NMS;

/**
 * Persists Ocelot metadata.
 *
 * @see Ocelot
 */
@TraitName("ocelotmodifiers")
public class OcelotModifiers extends Trait {
    @Persist("sitting")
    private boolean sitting;
    @Persist("type")
    private Ocelot.Type type = Ocelot.Type.WILD_OCELOT;

    public OcelotModifiers() {
        super("ocelotmodifiers");
    }

    @Override
    public void onSpawn() {
        updateModifiers();
    }

    public void setSitting(boolean sit) {
        this.sitting = sit;
        updateModifiers();
    }

    public void setType(Ocelot.Type type) {
        this.type = type;
        updateModifiers();
    }

    public boolean supportsOcelotType() {
        return SUPPORTS_CAT_TYPE;
    }

    private void updateModifiers() {
        if (!(npc.getEntity() instanceof Ocelot))
            return;
        Ocelot ocelot = (Ocelot) npc.getEntity();
        NMS.setSitting(ocelot, sitting);
        if (!SUPPORTS_CAT_TYPE) {
            return;
        }
        try {
            ocelot.setCatType(type);
        } catch (UnsupportedOperationException ex) {
            SUPPORTS_CAT_TYPE = false;
        }
    }

    private static boolean SUPPORTS_CAT_TYPE = true;
}
