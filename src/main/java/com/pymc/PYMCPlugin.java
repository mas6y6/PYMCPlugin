package com.pymc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.potion.PotionType;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.print.DocFlavor.READER;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandSender.Spigot;
import org.bukkit.util.StringUtil;
import org.checkerframework.checker.units.qual.h;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.advancement.Advancement;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionBrewer;
import com.google.gson.JsonObject;

public class PYMCPlugin extends JavaPlugin {
    public String version = "1.0";
    private boolean isServerRunning = false;
    private FileConfiguration config;
    private WebSocketServer server;
    private int port;
    private String host;
    private boolean debug;
    private PotionEffectManager potionEffectManager;
    private HashMap<String, WebSocket> connectedClients = new HashMap<>();

    public static Player getPlayerByUUID(String uuidString) {
        try {
            // Convert the string to a UUID
            UUID uuid = UUID.fromString(uuidString);
            // Get the player by UUID
            Player player = Bukkit.getPlayer(uuid);
            return player;
        } catch (IllegalArgumentException e) {
            // Handle the case where the string is not a valid UUID
            System.out.println("Invalid UUID format: " + uuidString);
            return null;
        }
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        potionEffectManager = new PotionEffectManager();
        config = this.getConfig();
        host = config.getString("config.host", "localhost");
        port = config.getInt("config.port", 8290);
        debug = config.getBoolean("config.debuf", false);
        getLogger().info("PYMC Plugin enabled!");
        startWebSocketServer();
    }

    @Override
    public void onDisable() {
        if (server != null && isServerRunning) {
            try {
                server.stop();
                getLogger().info("WebSocket server stopped.");
            } catch (InterruptedException e) {
                getLogger().severe("Server shutdown interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // Re-interrupt the thread if it's been interrupted.
            } catch (Exception e) {
                getLogger().severe("Error stopping WebSocket server: " + e.getMessage());
            }
        }
        getLogger().info("PYMC Plugin disabled!");
    }
    public String bukkitcommand_handler(List args) {
        String returndata = "none";
        if (args.get(0).equals("test")) {
            returndata = "bukkit.return*" +ChatColor.AQUA.toString();
        } else if (args.get(0).equals("getallonlineplayers")) {
            List<String> playerNames = new ArrayList<>();
            Gson gson = new Gson();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                String displayname = player.getDisplayName();
                String uuid = player.getUniqueId().toString();
                playerNames.add(name + "*" + displayname + "*" + player.getCustomName() + "*" + uuid + "*" + player.getGameMode().toString() + "*" + player.getHealth() + "*" + player.getHealthScale() + "*" +player.getExp() + "*" + player.getExpToLevel() + "*" + player.getLevel() + "*" + player.getExhaustion() + "*" + player.getFoodLevel() + "*" + player.getSaturation() + "*" + player.getStarvationRate() + "*" + player.getFreezeTicks() + "*" + player.getRemainingAir() + "*" + player.getPlayerTime() + "*" + player.getLastPlayed() + "*" + player.getSleepTicks() + "*" + player.getFlySpeed() + "*" + player.getWalkSpeed() + "*" + player.getFireTicks() + "*" + player.getAddress() + "*" + player.getPing());
            }

            String jsonPlayers = gson.toJson(playerNames);

            returndata = jsonPlayers;
        } else if (args.get(0).equals("getallworlds")) {
            List<String> playerNames = new ArrayList<>();
            Gson gson = new Gson();
            for (World world : Bukkit.getWorlds()) {
                playerNames.add(world.getName()+"*"+world.getEnvironment().toString()+"*"+world.getMinHeight()+"*"+world.getMaxHeight()+"*"+world.getSeed()+"*"+world.getDifficulty().toString()+"*"+world.getLogicalHeight());
            }

            String jsonPlayers = gson.toJson(playerNames);

            returndata = jsonPlayers;
        } else if (args.get(0).equals("getallofflineplayers")) {
            List<String> playerNames = new ArrayList<>();
            Gson gson = new Gson();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();
                String uuid = player.getUniqueId().toString();
                playerNames.add(name + "*" + uuid + "*" + player.getFirstPlayed() + "*" + player.getLastPlayed());
            }

            String jsonPlayers = gson.toJson(playerNames);

            returndata = jsonPlayers;
        }
        return returndata;
    }

    public String locationcommand_handler(List args) {
        String returndata = "none";
        String world = args.get(0).toString();
        args.remove(0);
        String env = args.get(0).toString();
        args.remove(0);
        int x = Integer.parseInt(args.get(0).toString());
        args.remove(0);
        int y = Integer.parseInt(args.get(0).toString());
        args.remove(0);
        int z = Integer.parseInt(args.get(0).toString());
        args.remove(0);
        int pitch = Integer.parseInt(args.get(0).toString());
        args.remove(0);
        int direction = Integer.parseInt(args.get(0).toString());
        args.remove(0);
        int yaw = Integer.parseInt(args.get(0).toString());
        args.remove(0);

        if (args.get(0).toString().equals("getblock")) {
            Block block = Bukkit.getWorld(world).getBlockAt(x,y,z);
            String blockdata = block.getBlockData().getAsString();
            String xb = String.valueOf(block.getX());
            String yb = String.valueOf(block.getY());
            String zb = String.valueOf(block.getZ());
            String blocktype = block.getType().toString();
            returndata = xb+"*"+yb+"*"+zb+"*"+block.getBiome().toString()+"*"+blocktype+"*"+blockdata;
        } else if (args.get(0).toString().equals("getlocation")) {

        }
        return returndata;
    }

    public String worldcommand_handler(List args) {
        String returndata = "none";
        World world = Bukkit.getWorld(args.get(0).toString());
        if (world.equals(null)){
            returndata = "worlddoesnotexist";
        }
        args.remove(0);

        if (args.get(0).equals("getworld")) {
            returndata = "world.return*"+world.getName()+"*"+world.getEnvironment().toString()+"*"+world.getMinHeight()+"*"+world.getMaxHeight()+"*"+world.getSeed()+"*"+world.getDifficulty()+"*"+world.getLogicalHeight();
        } else if (args.get(0).equals("setweather")) {
            Bukkit.getScheduler().runTask(this, () -> {
                if (args.get(1).equals("true")) {
                    world.setStorm(true);
                } else if (args.get(1).equals("false")) {
                    world.setStorm(false);
                }
        
                if (args.get(2).equals("true")) {
                    world.setThundering(true);
                } else if (args.get(2).equals("false")) {
                    world.setThundering(false);
                }
            });
        } 

        return returndata;
    }

    public String playercommand_handler(List args) {
        String returndata = "none";

        if (args.isEmpty()) {
            return "No player specified.";
        }

        Player player = getPlayerByUUID(args.get(0).toString());
        if (player == null) {
            return "nonexist";
        }
        args.remove(0);

        if (args.get(0).equals("getplayer")) {
            String name = player.getName();
            String displayname = player.getDisplayName();
            String uuid = player.getUniqueId().toString();
            returndata = name + "*" + displayname + "*" + player.getCustomName() + "*" + uuid + "*" + player.getGameMode().toString() + "*" + player.getHealth() + "*" + player.getHealthScale() + "*" +player.getExp() + "*" + player.getExpToLevel() + "*" + player.getLevel() + "*" + player.getExhaustion() + "*" + player.getFoodLevel() + "*" + player.getSaturation() + "*" + player.getStarvationRate() + "*" + player.getFreezeTicks() + "*" + player.getRemainingAir() + "*" + player.getPlayerTime() + "*" + player.getLastPlayed() + "*" + player.getSleepTicks() + "*" + player.getFlySpeed() + "*" + player.getWalkSpeed() + "*" + player.getFireTicks() + "*" + player.getAddress() + "*" + player.getPing();
        } else if (args.get(0).equals("giveitem")){
            String itemid = args.get(1).toString().toUpperCase();
            int quantity = Integer.parseInt(args.get(2).toString());
            Material m_item = Material.getMaterial(itemid);
            ItemStack item = new ItemStack(m_item,quantity);
            player.getInventory().addItem(item);
            returndata = "success";
        } else if (args.get(0).equals("removeitem")){
                String itemid = args.get(1).toString().toUpperCase();
                int quantity = Integer.parseInt(args.get(2).toString());
                Material m_item = Material.getMaterial(itemid);
                ItemStack item = new ItemStack(m_item,quantity);
                player.getInventory().removeItem(item);
                returndata = "success";
        } else if (args.get(0).equals("getloc")) {
            String d = "UNKNOWN";
            if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                d = "NORMAL";
            } else if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                d = "NETHER";
            } else if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
                d = "THE_END";
            } else {
                d = "UNKNOWN";
            }
            String x = String.format("%.2f", player.getLocation().getX());
            String y = String.format("%.2f", player.getLocation().getY());
            String z = String.format("%.2f", player.getLocation().getZ());
            String pitch = String.format("%.2f", player.getLocation().getPitch());
            String direction = String.format("%.2f", player.getLocation().getDirection());
            String yaw = String.format("%.2f", player.getLocation().getYaw());
            returndata = d+"*"+player.getWorld().getName()+"*"+x+"*"+y+"*"+z+"*"+direction+"*"+pitch+"*"+yaw;
        } else if (args.get(0).equals("setgamemode")) {
            if (args.get(1).equals("s")) {
                player.setGameMode(GameMode.SURVIVAL);
                returndata = "SURVIVAL";
            } else if (args.get(1).equals("c")) {
                player.setGameMode(GameMode.CREATIVE);
                returndata = "CREATIVE";
            } else if (args.get(1).equals("a")) {
                player.setGameMode(GameMode.ADVENTURE);
                returndata = "ADVENTURE";
            } else if (args.get(1).equals("sp")) {
                player.setGameMode(GameMode.SPECTATOR);
                returndata = "gSPECTATOR";
            }
        } else if (args.get(0).equals("setcustomname")) {
            player.setCustomName(args.get(1).toString());
            returndata = "success";
        } else if (args.get(0).equals("setdisplayname")) {
            player.setDisplayName(args.get(1).toString());
            returndata = "uccess";
        } else if (args.get(0).equals("sendmessage")) {
            player.sendMessage(args.get(1).toString());
            returndata = "success";
        } else if (args.get(0).equals("sendtitle")) {
            int fadein = Integer.parseInt(args.get(3).toString());
            int stay = Integer.parseInt(args.get(4).toString());
            int fadeout = Integer.parseInt(args.get(5).toString());
            player.sendTitle(args.get(1).toString(),args.get(2).toString(),fadein,stay,fadeout);
            returndata = "success";
        } else if (args.get(0).equals("addeffect")) {
            PotionEffectType effectType = PotionEffectManager.getPotionEffectType(args.get(1).toString().toUpperCase());
            int duration = Integer.parseInt(args.get(2).toString());
            int amplifier = Integer.parseInt(args.get(3).toString());

            if (effectType != null) {
                PotionEffect effect = new PotionEffect(effectType, duration, amplifier);
                player.addPotionEffect(effect);
                returndata = "success";
            } else {
                returndata = "potionnotfound";
            }
        } else if (args.get(0).equals("sendactionbar")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(args.get(1).toString()));
                returndata = "success";
        } else if (args.get(0).equals("cleareffects")) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }            
            returndata = "success";
        } else if (args.get(0).equals("removeeffect")) {
            PotionEffectType effectType = PotionEffectManager.getPotionEffectType(args.get(1).toString().toUpperCase());
            player.removePotionEffect(effectType);
            returndata = "success";
        }
        return returndata;
    }

    public void startWebSocketServer() {
        server = new WebSocketServer(new InetSocketAddress(host, port)) {

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                getLogger().info("New connection from " + conn.getRemoteSocketAddress().toString());
                connectedClients.put(conn.getLocalSocketAddress().toString(),conn);
                conn.send("pymc_version="+version+"*ip="+conn.getLocalSocketAddress().toString());
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                connectedClients.remove(conn.getLocalSocketAddress().toString());
                getLogger().info("Closed connection to " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                getLogger().info("Message from " + conn.getRemoteSocketAddress() + ": " + message);
                JsonObject returndata = new JsonObject();
                
                String argumentsraw1 = message.substring(1);
                String[] argumentsraw2 = argumentsraw1.split("\\*");
                List<String> argument = new ArrayList<>(Arrays.asList(argumentsraw2));
                String id = argument.get(0);
                argument.remove(0);
                
                returndata.addProperty("id", id);
                returndata.addProperty("status", "error");
                returndata.addProperty("return", "Invalid command");
            
                getLogger().info("Full arguments: " + argument);
            
                try {
                    if (message.length() > 0 && message.charAt(0) == '!') {
                        getLogger().info("Arguments after removal of ID: " + argument);
                        String command = argument.get(0);
                        getLogger().info("Command: " + command);
                        argument.remove(0);
                        
                        if (command.equals("player")) {
                            getLogger().info("Handling 'player' command");
                            returndata.addProperty("return", playercommand_handler(argument).toString());
                            returndata.addProperty("status", "success");
                        } else if (command.equals("bukkit")) {
                            getLogger().info("Handling 'bukkit' command");
                            returndata.addProperty("return", bukkitcommand_handler(argument).toString());
                            returndata.addProperty("status", "success");
                        } else if (command.equals("world")) {
                            getLogger().info("Handling 'world' command");
                            returndata.addProperty("return", worldcommand_handler(argument).toString());
                            returndata.addProperty("status", "success");
                        } else {
                            getLogger().info("Unknown command: " + command);
                            returndata.addProperty("return", "Unknown command: " + command);
                        }
                    }
                    getLogger().info("Final returndata: " + returndata.toString());
                    conn.send(returndata.toString());
                } catch (Exception e) {
                    getLogger().warning("Error during message processing: " + e.toString());
                    returndata.addProperty("return", e.toString());
                    returndata.addProperty("status", "error");
                    getLogger().info("Error returndata: " + returndata.toString());
                    conn.send(returndata.toString());
                }
            }
            

            @Override
            public void onError(WebSocket conn, Exception ex) {
                getLogger().severe("Error from connection " + (conn != null ? conn.getRemoteSocketAddress() : "Unknown") + ": " + ex.getMessage());
            }

            @Override
            public void onStart() {
                isServerRunning = true;
                getLogger().info("PyMC "+host+":"+port);
            }
        };
        server.start();
    }
}
