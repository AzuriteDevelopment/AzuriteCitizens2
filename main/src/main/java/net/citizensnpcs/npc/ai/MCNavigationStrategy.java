package net.citizensnpcs.npc.ai;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import net.citizensnpcs.api.ai.AbstractPathStrategy;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.NMS;

public class MCNavigationStrategy extends AbstractPathStrategy {
    private final Entity handle;
    private final MCNavigator navigator;
    private final NavigatorParameters parameters;
    private final Location target;

    MCNavigationStrategy(final NPC npc, Iterable<Vector> path, NavigatorParameters params) {
        super(TargetType.LOCATION);
        List<Vector> list = Lists.newArrayList(path);
        this.target = list.get(list.size() - 1).toLocation(npc.getStoredLocation().getWorld());
        this.parameters = params;
        handle = npc.getEntity();
        this.navigator = NMS.getTargetNavigator(npc.getEntity(), list, params);
    }

    MCNavigationStrategy(final NPC npc, Location dest, NavigatorParameters params) {
        super(TargetType.LOCATION);
        this.target = dest;
        this.parameters = params;
        handle = npc.getEntity();
        this.navigator = NMS.getTargetNavigator(npc.getEntity(), dest, params);
    }

    @Override
    public Iterable<Vector> getPath() {
        return navigator.getPath();
    }

    @Override
    public Location getTargetAsLocation() {
        return target;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.LOCATION;
    }

    @Override
    public void stop() {
        navigator.stop();
    }

    @Override
    public String toString() {
        return "MCNavigationStrategy [target=" + target + "]";
    }

    @Override
    public boolean update() {
        if (navigator.getCancelReason() != null) {
            setCancelReason(navigator.getCancelReason());
        }
        if (getCancelReason() != null)
            return true;
        boolean wasFinished = navigator.update();
        parameters.run();
        Location loc = handle.getLocation(HANDLE_LOCATION);
        double dX = target.getBlockX() + 0.5 - loc.getX();
        double dZ = target.getBlockZ() + 0.5 - loc.getZ();
        double dY = target.getY() - loc.getY();
        double xzDistance = dX * dX + dZ * dZ;
        if ((dY * dY) < 1 && xzDistance <= parameters.distanceMargin()) {
            stop();
            return true;
        }
        if (navigator.getCancelReason() != null) {
            setCancelReason(navigator.getCancelReason());
            return true;
        }
        return wasFinished;
    }

    public static interface MCNavigator {
        CancelReason getCancelReason();

        Iterable<Vector> getPath();

        void stop();

        boolean update();
    }

    private static final Location HANDLE_LOCATION = new Location(null, 0, 0, 0);
}
