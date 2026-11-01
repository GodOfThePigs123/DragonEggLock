package me.server.dragoneggprotect;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DragonEggLock extends JavaPlugin implements Listener {

    private static final String BYPASS = "dragoneggprotect.bypass";

    // true = nobody can mine a placed dragon egg (ops and bypass can). false = anyone can mine it.
    private static final boolean PROTECT_FROM_BREAKING = true;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DragonEggLock enabled.");
    }

    private static boolean isEgg(Block block) {
        return block.getType() == Material.DRAGON_EGG;
    }

    // The egg teleporting is a block-movement event with the egg as the source block.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEggTeleport(BlockFromToEvent event) {
        if (isEgg(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    // Entity explosions (creepers, TNT, wither, ender dragon...).
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(DragonEggLock::isEgg);
    }

    // Block explosions (beds, respawn anchors...).
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(DragonEggLock::isEgg);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isEgg(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isEgg(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // Stops the egg falling (like sand) when the block under it is removed,
    // and stops entities such as the Wither from changing or breaking it.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEggChange(EntityChangeBlockEvent event) {
        if (isEgg(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEggBreak(BlockBreakEvent event) {
        if (!PROTECT_FROM_BREAKING) return;
        if (!isEgg(event.getBlock())) return;
        if (event.getPlayer().hasPermission(BYPASS)) return;

        event.setCancelled(true);
        event.getPlayer().sendActionBar(Component.text("§cThis dragon egg is protected."));
    }
}
