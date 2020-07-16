package com.bwgy.abilities;

import com.bwgy.clansystem.EconomySystem;
import com.bwgy.main.Main;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.SpawnEgg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GUI implements Listener {
    private static boolean autosmelt_active=false;
    private static boolean fly_active=false;
    private static List<UUID> menu=new ArrayList<>();
    public org.bukkit.inventory.ItemStack changeSpawnEgg(org.bukkit.inventory.ItemStack egg, EntityType type)
    {
        Objects.requireNonNull(egg);
        Objects.requireNonNull(type);
        net.minecraft.server.v1_15_R1.ItemStack nms = CraftItemStack.asNMSCopy(egg);
        NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();
        NBTTagCompound entityTag = tag.hasKeyOfType("EntityTag", tag.getTypeId()) ? tag.getCompound("EntityTag") : new NBTTagCompound();
        entityTag.setString("id", type.getName()); // Ignore the fact that getName() is deprecated. We need it.
        tag.set("EntityTag", entityTag);
        nms.setTag(tag);
        return CraftItemStack.asCraftMirror(nms); // Or #asBukkitCopy - Your decision
    }
    public static void openGUI(Player p){
        menu.add(p.getUniqueId());
        ItemStack layout=new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta layout_meta=layout.getItemMeta();
        layout_meta.setDisplayName(" ");
        layout.setItemMeta(layout_meta);

        ItemStack mobtake=new ItemStack(Material.BAT_SPAWN_EGG);
        ItemMeta mobtake_meta=mobtake.getItemMeta();
        mobtake_meta.setDisplayName("§eMobTake");
        List<String> mobtake_lore=new ArrayList<String>();
        mobtake_lore.add("§aNehme einen Mob als Spawnegg mit!");
        mobtake_lore.add("§22000€");
        mobtake_meta.setLore(mobtake_lore);
        mobtake.setItemMeta(mobtake_meta);

        ItemStack autosmelt=new ItemStack(Material.BLAST_FURNACE);
        ItemMeta autosmelt_meta=autosmelt.getItemMeta();
        autosmelt_meta.setDisplayName("§eAutoSmelt");
        if(autosmelt_active){
            autosmelt_meta.setDisplayName("§4Bereits aktiv!");
        }
        List<String> autosmelt_lore=new ArrayList<String>();
        autosmelt_lore.add("§aSchmelze Erze direkt baim abbauen! (Für alle auf dem Server)");
        autosmelt_lore.add("§21500€");
        autosmelt_meta.setLore(autosmelt_lore);
        autosmelt.setItemMeta(autosmelt_meta);

        ItemStack cobble_farm=new ItemStack(Material.IRON_PICKAXE);
        ItemMeta cobble_meta=mobtake.getItemMeta();
        cobble_meta.setDisplayName("§eCobble Farm");
        List<String> cobble_lore=new ArrayList<String>();
        cobble_lore.add("§aBenutzt alle deine Pickaxes um Cobble zu farmen.");
        cobble_lore.add("§21500€");
        cobble_meta.setLore(cobble_lore);
        cobble_farm.setItemMeta(cobble_meta);

        ItemStack fly=new ItemStack(Material.FEATHER);
        ItemMeta fly_meta=mobtake.getItemMeta();
        fly_meta.setDisplayName("§eFly");
        List<String> fly_lore=new ArrayList<String>();
        fly_lore.add("§aAktiviere für 30 Minuten Fly auf dem Server!");
        fly_lore.add("§2100000€");
        fly_meta.setLore(fly_lore);
        fly.setItemMeta(fly_meta);

        Inventory inv= Bukkit.createInventory(null,27, "§9§lAbilities");

        //Menu generation

        for(int i=0; i<9; i++){
            inv.setItem(i,layout);
        }
        for(int i=18; i<27; i++){
            inv.setItem(i,layout);
        }
        inv.setItem(9,mobtake);
        inv.setItem(10,autosmelt);
        inv.setItem(11,fly);

        //Open inventory

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(menu.contains(e.getWhoClicked().getUniqueId())) {
            if (e.getSlot() < 9 || e.getSlot() > 17) {
                e.setCancelled(true);
            } else if (e.getSlot() == 9) {
                EconomySystem.removeMoney((Player) e.getWhoClicked(),2000);
                Config.addMobTaker(e.getWhoClicked().getUniqueId(),1);

            }else if(e.getSlot()==10) {
                if(!autosmelt_active){
                    EconomySystem.removeMoney((Player) e.getWhoClicked(),1500);
                    autosmelt_active=true;
                    Bukkit.broadcastMessage("§aDer Spieler §e"+e.getWhoClicked().getName()+"§a hat AutoSmelt für 5 Minuten aktiviert!");
                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            autosmelt_active=false;
                            Bukkit.broadcastMessage("§cAutoSmelt ist nun wieder inaktiv!");
                        }
                    },6000);
                }else{
                    ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_LAND,1,100);
                }
            }else if(e.getSlot()==11){
                Player p=(Player)e.getWhoClicked();
                if(!fly_active){
                    EconomySystem.removeMoney(p,10000);
                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            fly_active=false;
                            Bukkit.broadcastMessage("§cFly ist nun wieder inaktiv!");
                            for(Player pl:Bukkit.getOnlinePlayers()){
                                pl.setFlying(false);
                            }
                        }
                    },36000);
                    for(Player player:Bukkit.getOnlinePlayers()){
                        player.setFlying(true);
                        player.sendMessage("§e"+p.getName()+"§a hat die Fly für 30 Minuten aktiviert!");
                    }
                }else{
                    p.playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_LAND,1,100);
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!fly_active){
            e.getPlayer().setFlying(false);
        }else if(fly_active){
            e.getPlayer().setFlying(true);
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(fly_active){
            e.getPlayer().setFlying(true);
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if(e.getEntity().getKiller()!=null){
            if(e.getEntityType().isSpawnable()&&Config.getMobTakers(e.getEntity().getKiller().getUniqueId())>0){
                ItemStack drop=new ItemStack(Material.BAT_SPAWN_EGG);
                drop=changeSpawnEgg(drop,e.getEntityType());
                SpawnEggMeta drop_meta= (SpawnEggMeta) drop.getItemMeta();
                drop_meta.setDisplayName("§9"+e.getEntityType().toString());
                drop.setItemMeta(drop_meta);
                e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().getLocation(),drop);
                Config.removeMobTakers(e.getEntity().getKiller().getUniqueId(),1);
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(menu.contains(e.getPlayer().getUniqueId())){
            menu.remove(e.getPlayer().getUniqueId());
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(autosmelt_active){
            if(e.getBlock().getType()==Material.IRON_ORE){
                e.setDropItems(false);
                ItemStack drop=new ItemStack(Material.IRON_INGOT);
                e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),drop);
            }else if(e.getBlock().getType()==Material.GOLD_ORE){
                e.setDropItems(false);
                ItemStack drop=new ItemStack(Material.GOLD_INGOT);
                e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),drop);
            }
        }
    }
}
