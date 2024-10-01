package com.pymc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
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
import org.bukkit.util.StringUtil;
import org.checkerframework.checker.units.qual.h;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.advancement.Advancement;
import net.md_5.bungee.api.chat.TextComponent;;

public class PYMCPlugin extends JavaPlugin {
    public String version = "1.0";
    private boolean isServerRunning = false;
    private FileConfiguration config;
    private WebSocketServer server;
    private int port;
    private String host;
    private Dictionary<String, WebSocket> connectedClients = new Hashtable<>();


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();
        host = config.getString("config.host", "localhost");
        port = config.getInt("config.port", 8290);
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

            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                getLogger().severe("Error from connection " + (conn != null ? conn.getRemoteSocketAddress() : "Unknown") + ": " + ex.getMessage());
            }

            @Override
            public void onStart() {
                isServerRunning = true;
                getLogger().info("WebSocket server started on port 8290");
            }
        };
        server.start();
    }
}
