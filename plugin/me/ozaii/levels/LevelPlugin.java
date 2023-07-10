/*     */ package me.ozaii.levels;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import me.ozaii.levels.enums.EnumAllLevels;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ 
/*     */ public class LevelPlugin
/*     */   extends JavaPlugin
/*     */   implements CommandExecutor
/*     */ {
/*     */   private Connection connection;
/*     */   private Map<String, Integer> playerLevels;
/*     */   
/*     */   public void onEnable() {
/*  32 */     saveDefaultConfig();
/*  33 */     connectToDatabase();
/*  34 */     createPlayerLevelsTable();
/*     */ 
/*     */     
/*  37 */     getCommand("level").setExecutor(this);
/*     */ 
/*     */     
/*  40 */     this.playerLevels = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  41 */     loadPlayerLevels();
/*  42 */     if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
/*  43 */       (new LevelPlaceholderExpansion(this)).register();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void createPlayerLevelsTable() {
/*     */     try {
/*  50 */       Statement statement = this.connection.createStatement();
/*  51 */       String query = "CREATE TABLE IF NOT EXISTS player_levels (player_name VARCHAR(255) PRIMARY KEY,level INT NOT NULL)";
/*     */       
/*  53 */       statement.executeUpdate(query);
/*  54 */       statement.close();
/*  55 */     } catch (SQLException e) {
/*  56 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  63 */     disconnectFromDatabase();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connectToDatabase() {
/*  71 */     String host = getConfig().getString("mysql.host");
/*  72 */     int port = getConfig().getInt("mysql.port");
/*  73 */     String database = getConfig().getString("mysql.database");
/*  74 */     String username = getConfig().getString("mysql.username");
/*  75 */     String password = getConfig().getString("mysql.password");
/*     */ 
/*     */     
/*     */     try {
/*  79 */       this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, 
/*  80 */           password);
/*  81 */       Bukkit.getLogger().info("Successfully connected to the MySQL database.");
/*  82 */     } catch (SQLException e) {
/*  83 */       Bukkit.getLogger().severe("Failed to connect to the MySQL database: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnectFromDatabase() {
/*     */     try {
/*  90 */       if (this.connection != null && !this.connection.isClosed()) {
/*  91 */         this.connection.close();
/*  92 */         Bukkit.getLogger().info("Disconnected from the MySQL database.");
/*     */       } 
/*  94 */     } catch (SQLException e) {
/*  95 */       Bukkit.getLogger().severe("Failed to disconnect from the MySQL database: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadPlayerLevels() {
/*     */     try {
/* 101 */       Statement statement = this.connection.createStatement();
/* 102 */       ResultSet resultSet = statement.executeQuery("SELECT player_name, level FROM player_levels");
/*     */       
/* 104 */       while (resultSet.next()) {
/* 105 */         String playerName = resultSet.getString("player_name");
/* 106 */         int level = resultSet.getInt("level");
/* 107 */         this.playerLevels.put(playerName, Integer.valueOf(level));
/*     */       } 
/*     */       
/* 110 */       resultSet.close();
/* 111 */       statement.close();
/* 112 */     } catch (SQLException e) {
/* 113 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void savePlayerLevel(String playerName, int level) {
/*     */     try {
/* 119 */       PreparedStatement statement = this.connection.prepareStatement("INSERT INTO player_levels (player_name, level) VALUES (?, ?) ON DUPLICATE KEY UPDATE level = ?");
/*     */       
/* 121 */       statement.setString(1, playerName);
/* 122 */       statement.setInt(2, level);
/* 123 */       statement.setInt(3, level);
/*     */       
/* 125 */       statement.executeUpdate();
/* 126 */       statement.close();
/* 127 */     } catch (SQLException e) {
/* 128 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sendLevelUpMessage(Player player, int currentLevel, int nextLevel) {
/* 133 */     String message = ChatColor.GREEN + "Tebrikler Seviye Atladın! Yeni Seviyen: " + currentLevel + ", Next level: " + 
/* 134 */       ChatColor.GOLD + nextLevel;
/* 135 */     player.sendMessage(message);
/*     */   }
/*     */   
/*     */   public void sendTopPlayersMessage(Player player) {
/* 139 */     player.sendMessage(ChatColor.YELLOW + "Top 10 Players:");
/*     */     
/* 141 */     List<Map.Entry<String, Integer>> topPlayers = getTopPlayers(10);
/* 142 */     int rank = 1;
/* 143 */     for (Map.Entry<String, Integer> entry : topPlayers) {
/* 144 */       String playerName = entry.getKey();
/* 145 */       int level = ((Integer)entry.getValue()).intValue();
/* 146 */       String message = ChatColor.GRAY + "#" + rank + ": " + ChatColor.WHITE + playerName + " - Level " + level;
/* 147 */       player.sendMessage(message);
/* 148 */       rank++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private List<Map.Entry<String, Integer>> getTopPlayers(int count) {
/* 153 */     List<Map.Entry<String, Integer>> topPlayers = new ArrayList<>(this.playerLevels.entrySet());
/* 154 */     topPlayers.sort(Comparator.comparingInt(Map.Entry::getValue).reversed()
/* 155 */         .thenComparing(Map.Entry::getKey));
/*     */     
/* 157 */     if (topPlayers.size() > count) {
/* 158 */       topPlayers = topPlayers.subList(0, count);
/*     */     }
/*     */     
/* 161 */     return topPlayers;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/* 166 */     if (!(sender instanceof Player)) {
/* 167 */       sender.sendMessage(ChatColor.RED + "Only players can use this command.");
/* 168 */       return true;
/*     */     } 
/*     */     
/* 171 */     Player player = (Player)sender;
/*     */     
/* 173 */     if (args.length == 0) {
/*     */       
/* 175 */       int playerLevel = getPlayerLevel(player.getName());
/* 176 */       String levelName = getPlayerLevelName(playerLevel);
/* 177 */       sendLevelMessage(player, playerLevel, levelName);
/* 178 */       return true;
/* 179 */     }  if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
/*     */       
/* 181 */       sendAllLevelsMessage(player);
/* 182 */       return true;
/* 183 */     }  if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
/*     */       
/* 185 */       String targetPlayer = args[1];
/* 186 */       int targetLevel = getPlayerLevel(targetPlayer);
/* 187 */       String levelName = getPlayerLevelName(targetLevel);
/* 188 */       sendLevelMessage(player, targetLevel, levelName);
/* 189 */       return true;
/* 190 */     }  if (args.length == 3 && args[0].equalsIgnoreCase("up")) {
/*     */       
/* 192 */       String targetPlayer = args[1];
/* 193 */       int increment = Integer.parseInt(args[2]);
/* 194 */       int currentLevel = getPlayerLevel(targetPlayer);
/* 195 */       int nextLevel = Math.min(currentLevel + increment, 100);
/* 196 */       setPlayerLevel(targetPlayer, nextLevel);
/* 197 */       sendLevelUpMessage(player, currentLevel, nextLevel);
/* 198 */       return true;
/* 199 */     }  if (args.length == 3 && args[0].equalsIgnoreCase("down")) {
/*     */       
/* 201 */       String targetPlayer = args[1];
/* 202 */       int decrement = Integer.parseInt(args[2]);
/* 203 */       int currentLevel = getPlayerLevel(targetPlayer);
/* 204 */       int nextLevel = Math.max(currentLevel - decrement, 0);
/* 205 */       setPlayerLevel(targetPlayer, nextLevel);
/* 206 */       player.sendMessage(ChatColor.YELLOW + targetPlayer + "'s level has been reduced to " + nextLevel);
/* 207 */       return true;
/* 208 */     }  if (args.length == 1 && args[0].equalsIgnoreCase("top")) {
/*     */       
/* 210 */       sendTopPlayersMessage(player);
/* 211 */       loadPlayerLevels();
/* 212 */       return true;
/* 213 */     }  if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
/*     */       
/* 215 */       if (!player.hasPermission("level.set")) {
/* 216 */         player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
/* 217 */         return true;
/*     */       } 
/*     */       
/* 220 */       String targetPlayer = args[1];
/* 221 */       int level = Integer.parseInt(args[2]);
/* 222 */       level = Math.min(Math.max(level, 0), 100);
/* 223 */       setPlayerLevel(targetPlayer, level);
/* 224 */       player.sendMessage(ChatColor.YELLOW + "You have set " + targetPlayer + "'s level to " + level);
/* 225 */       return true;
/*     */     } 
/*     */     
/* 228 */     return false;
/*     */   }
/*     */   
/*     */   public int getPlayerLevel(String playerName) {
/* 232 */     return ((Integer)this.playerLevels.getOrDefault(playerName, Integer.valueOf(0))).intValue(); } public String getPlayerLevelName(int level) {
/*     */     byte b;
/*     */     int i;
/*     */     EnumAllLevels[] arrayOfEnumAllLevels;
/* 236 */     for (i = (arrayOfEnumAllLevels = EnumAllLevels.values()).length, b = 0; b < i; ) { EnumAllLevels enumLevel = arrayOfEnumAllLevels[b];
/* 237 */       if (enumLevel.getLevel() == level)
/* 238 */         return enumLevel.name(); 
/*     */       b++; }
/*     */     
/* 241 */     return "Unknown";
/*     */   }
/*     */   
/*     */   public void setPlayerLevel(String playerName, int level) {
/* 245 */     this.playerLevels.put(playerName, Integer.valueOf(level));
/* 246 */     savePlayerLevel(playerName, level);
/*     */   }
/*     */   
/*     */   public void sendLevelMessage(Player player, int level, String levelName) {
/* 250 */     String message = ChatColor.YELLOW + "Your level: " + levelName + " (" + level + ")";
/* 251 */     player.sendMessage(message);
/*     */   }
/*     */   
/*     */   public void sendAllLevelsMessage(Player player) {
/* 255 */     player.sendMessage(ChatColor.YELLOW + "All Levels:"); byte b; int i;
/*     */     EnumAllLevels[] arrayOfEnumAllLevels;
/* 257 */     for (i = (arrayOfEnumAllLevels = EnumAllLevels.values()).length, b = 0; b < i; ) { EnumAllLevels level = arrayOfEnumAllLevels[b];
/* 258 */       int levelIndex = level.getLevel();
/* 259 */       String levelName = level.name();
/* 260 */       String message = getLevelMessage(levelIndex, levelName);
/* 261 */       player.sendMessage(message);
/*     */       b++; }
/*     */   
/*     */   }
/*     */   public String getLevelMessage(int levelIndex, String levelName) {
/* 266 */     ChatColor levelColor = getLevelColor(levelIndex);
/* 267 */     return levelColor + levelName + " - Level " + levelIndex;
/*     */   }
/*     */   
/*     */   public ChatColor getLevelColor(int levelIndex) {
/* 271 */     if (levelIndex <= 4)
/* 272 */       return ChatColor.GRAY; 
/* 273 */     if (levelIndex <= 8)
/* 274 */       return ChatColor.WHITE; 
/* 275 */     if (levelIndex <= 12)
/* 276 */       return ChatColor.GOLD; 
/* 277 */     if (levelIndex <= 16)
/* 278 */       return ChatColor.AQUA; 
/* 279 */     if (levelIndex <= 20)
/* 280 */       return ChatColor.GREEN; 
/* 281 */     if (levelIndex <= 24)
/* 282 */       return ChatColor.LIGHT_PURPLE; 
/* 283 */     if (levelIndex <= 30)
/* 284 */       return ChatColor.DARK_PURPLE; 
/* 285 */     if (levelIndex <= 40)
/* 286 */       return ChatColor.BLUE; 
/* 287 */     if (levelIndex <= 50)
/* 288 */       return ChatColor.YELLOW; 
/* 289 */     if (levelIndex <= 60)
/* 290 */       return ChatColor.DARK_GREEN; 
/* 291 */     if (levelIndex <= 70)
/* 292 */       return ChatColor.DARK_RED; 
/* 293 */     if (levelIndex <= 80)
/* 294 */       return ChatColor.DARK_AQUA; 
/* 295 */     if (levelIndex <= 90)
/* 296 */       return ChatColor.DARK_BLUE; 
/* 297 */     if (levelIndex >= 100) {
/* 298 */       return ChatColor.GOLD;
/*     */     }
/* 300 */     return ChatColor.RED;
/*     */   }
/*     */ }


/* Location:              C:\Users\ozaii1337\Desktop\masaüstü\Rutex\RutexLobby\plugins\Levelapi.jar!\me\ozaii\levels\LevelPlugin.class
 * Java compiler version: 18 (62.0)
 * JD-Core Version:       1.1.3
 */