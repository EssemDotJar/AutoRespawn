package me.essem.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

	@Override
	public void onEnable() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(this, this);

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (label.equalsIgnoreCase("setrespawn")) { // Command used to set respawn point
			Location location = player.getLocation();

			getConfig().set("respawnpoint.world", location.getWorld().getName());
			getConfig().set("respawnpoint.x", location.getX());
			getConfig().set("respawnpoint.y", location.getY());
			getConfig().set("respawnpoint.z", location.getZ());
			getConfig().set("respawnpoint.yaw", location.getYaw());
			getConfig().set("respawnpoint.pitch", location.getPitch());
			
			saveConfig();
		}

		return false;
	}

	/*
	* EVENTS:
	*/
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();

		if ((player.getHealth() - event.getFinalDamage()) <= 0) { // Check if damage is fatal
			event.setCancelled(true); // Make sure the player does not die

			player.setHealth(20); // Max health
			player.setFoodLevel(20); // Max saturation
			player.getInventory().clear(); // Clear inventory
			for (PotionEffect effect : player.getActivePotionEffects()) { // Clear potion effects
				player.removePotionEffect(effect.getType());
			}

			Location location = new Location(getWorld(), 
							 getXYZ("x"), 
							 getXYZ("y"), 
							 getXYZ("z"), 
							 getYawPitch("yaw"), 
							 getYawPitch("pitch")); // Get respawn point from config
			player.teleport(location); // Teleport player to respawn point
		}
	}

	/*
	* GETTERS:
	*/
	
	public World getWorld() {
		return Bukkit.getWorld(getConfig().get("respawnpoint.world").toString());
	}

	public double getXYZ(String xyz) {
		return (double) getConfig().get("respawnpoint." + xyz);
	}

	public float getYawPitch(String yawPitch) {
		return (float) getConfig().get("respawnpoint." + yawPitch);
	}
}
