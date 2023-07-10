/*    */ package me.ozaii.levels;
/*    */ 
/*    */ import me.clip.placeholderapi.expansion.PlaceholderExpansion;
/*    */ import org.bukkit.OfflinePlayer;
/*    */ 
/*    */ public class LevelPlaceholderExpansion
/*    */   extends PlaceholderExpansion {
/*    */   private final LevelPlugin plugin;
/*    */   
/*    */   public LevelPlaceholderExpansion(LevelPlugin plugin) {
/* 11 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIdentifier() {
/* 16 */     return "levelplugin";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAuthor() {
/* 21 */     return "ozaii";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 26 */     return this.plugin.getDescription().getVersion();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canRegister() {
/* 31 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean persist() {
/* 36 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String onRequest(OfflinePlayer player, String identifier) {
/* 41 */     if (player == null) {
/* 42 */       return "";
/*    */     }
/*    */     
/* 45 */     if (identifier.equals("current_level")) {
/* 46 */       int level = this.plugin.getPlayerLevel(player.getName());
/* 47 */       return String.valueOf(level);
/* 48 */     }  if (identifier.equals("current_level_name")) {
/* 49 */       int level = this.plugin.getPlayerLevel(player.getName());
/* 50 */       String levelName = this.plugin.getPlayerLevelName(level);
/* 51 */       return levelName;
/* 52 */     }  if (identifier.equals("next_level")) {
/* 53 */       int level = this.plugin.getPlayerLevel(player.getName());
/* 54 */       int nextLevel = level + 1;
/* 55 */       return String.valueOf(nextLevel);
/* 56 */     }  if (identifier.equals("next_level_name")) {
/* 57 */       int level = this.plugin.getPlayerLevel(player.getName());
/* 58 */       if (level >= 100) {
/* 59 */         int i = level;
/* 60 */         String str = this.plugin.getPlayerLevelName(i);
/* 61 */         return str;
/*    */       } 
/* 63 */       int nextLevel = level + 1;
/* 64 */       String levelName = this.plugin.getPlayerLevelName(nextLevel);
/* 65 */       return levelName;
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 70 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\ozaii1337\Desktop\masaüstü\Rutex\RutexLobby\plugins\Levelapi.jar!\me\ozaii\levels\LevelPlaceholderExpansion.class
 * Java compiler version: 18 (62.0)
 * JD-Core Version:       1.1.3
 */