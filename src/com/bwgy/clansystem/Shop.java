package com.bwgy.clansystem;

import com.bwgy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.*;

public class Shop implements CommandExecutor, Listener{
    private static HashMap<UUID,String> menu_open=new HashMap<>();
    private static FileConfiguration config;
    private static List<UUID> menu=new ArrayList<>();
    public static File getConfigfile(String clan){
        clan=clan.toUpperCase();
        return new File(Main.getPlugin().getDataFolder(), clan.toUpperCase()+".shop");
    }
    public static FileConfiguration getConfig(String clan){
        File configfile;
        configfile = new File(Main.getPlugin().getDataFolder(), clan.toUpperCase()+".shop");

        config = YamlConfiguration.loadConfiguration(configfile);
        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("shop.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
        return config;
    }
    public static void openClanShop(Player p, String clan){
        menu.add(p.getUniqueId());
        if(menu_open.get(p.getUniqueId())==null||menu_open.get(p.getUniqueId()).equals("   ")) {
            menu_open.put(p.getUniqueId(), clan.toUpperCase());
        }
        Inventory inv= Bukkit.createInventory(null,45,"§c"+clan);
        ItemStack empty=new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta emptymeta=empty.getItemMeta();
        emptymeta.setDisplayName("§cSlot nicht belegt");
        empty.setItemMeta(emptymeta);
        ItemStack layout=new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta layoutmeta=layout.getItemMeta();
        layoutmeta.setDisplayName("    ");
        layout.setItemMeta(layoutmeta);
        //Layout generation
        inv.setItem(0, layout);
        inv.setItem(1, layout);
        inv.setItem(2, layout);
        inv.setItem(3, layout);
        inv.setItem(4, layout);
        inv.setItem(5, layout);
        inv.setItem(6, layout);
        inv.setItem(7, layout);
        inv.setItem(8, layout);
        inv.setItem(36, layout);
        inv.setItem(37, layout);
        inv.setItem(38, layout);
        inv.setItem(39, layout);
        inv.setItem(40, layout);
        inv.setItem(41, layout);
        inv.setItem(42, layout);
        inv.setItem(43, layout);
        inv.setItem(44, layout);
        //Shop Item loader
        for(ItemStack obj:getItems(clan)){
            inv.setItem(getItems(clan).indexOf(obj)+9,obj);
        }
        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String lbl, String[] args) {
        if(cmd.getName().equalsIgnoreCase("shop")) {
            if (sender instanceof Player) {
                if (args.length == 1) {
                    openClanShop((Player) sender, args[0]);
                } else {
                    sender.sendMessage("§cBenutze: §4/shop <Clan>");
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("sell")){
            if(sender instanceof Player){
                if(args.length==1){
                    if(Integer.valueOf(args[0])!=null){
                        if(ClanSystem.getClan(((Player)sender).getUniqueId())!=null){
                            if(!(((Player)sender).getItemInHand().getType() ==null)) {
                                Player p=(Player)sender;
                                Integer code=addItem(ClanSystem.getClan(p.getUniqueId()),p.getItemInHand().getType(),p.getItemInHand().getAmount(),Integer.valueOf(args[0]));
                                if (code==0){
                                    p.sendMessage("§aDas Item ist nun in deinem Clanshop verfügbar! ");
                                }else{
                                    p.sendMessage("§4[Error] §cWell, this is awkward. Error "+code+"§c happened!");
                                }
                            }else{
                                sender.sendMessage("§c");
                            }
                        }else{
                            sender.sendMessage("§cDu musst in einem Clan sein!");
                        }
                    }else{
                        sender.sendMessage("§cBenutze §4/sell <Preis>");
                    }
                }else{
                    sender.sendMessage("§cBenutze §4/sell <Preis>");
                }
            }
        }
        return false;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(menu.contains(e.getWhoClicked().getUniqueId())){
            Main.getPlugin().getLogger().info("Checking Slot...");
            if(e.getSlot()>8&&e.getSlot()<26){
                Main.getPlugin().getLogger().info("Buying item...");
                Main.getPlugin().getLogger().info("Getting the clan...");
                String clan=menu_open.get(((Player)e.getWhoClicked()).getUniqueId());
                Main.getPlugin().getLogger().info("Getting the item...");
                if(clan==null){
                    e.getWhoClicked().closeInventory();
                    ((Player) e.getWhoClicked()).sendTitle("§4Ein Fehler ist aufgetreten! (101)","§cMelde dich bei einem Admin!");
                }
                List items_= getConfig(clan).getList("items");
                if(items_!=null) {
                    List<String> items = new ArrayList<>();
                    for (Object obj : items_) {
                        items.add(String.valueOf(obj));
                    }
                    Material item = Material.valueOf(items.get(e.getSlot() - 9));
                    Main.getPlugin().getLogger().info("Buying...");
                    buyItem(item, clan, e.getWhoClicked().getUniqueId());
                }else{
                    e.getWhoClicked().closeInventory();
                    ((Player) e.getWhoClicked()).sendTitle("§4Ein Fehler ist aufgetreten! (101)","§cMelde dich bei einem Admin!");
                }

            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(menu.contains(e.getPlayer().getUniqueId())){
            menu.remove(e.getPlayer().getUniqueId());
            menu_open.replace(e.getPlayer().getUniqueId(),"   ");
        }
    }
    public static int addItem(String clan, Material material, int stock, int price){
        Integer code=100;

        clan=clan.toUpperCase();
        List<Object> items_= (List<Object>) getConfig(clan).getList("items");
        List<String> items=new ArrayList<>();
        for(Object obj:items_){
            Main.getPlugin().getLogger().info("Item found!");
            items.add(String.valueOf(obj));
        }
        List<Integer> prices=getConfig(clan).getIntegerList("prices");
        List<Integer> stocks=getConfig(clan).getIntegerList("stocks");
        if(items.size()==prices.size()&&items.size()==stocks.size()&&prices.size()==stocks.size()){
            if(items.size()<=36) {
                if(!items.contains(String.valueOf(material))) {
                    items.add(String.valueOf(material));
                    prices.add(price);
                    stocks.add(stock);
                    FileConfiguration tmp = getConfig(clan);
                    tmp.set("items", items); 
                    tmp.set("prices", prices);
                    tmp.set("stocks", stocks);

                    try {
                        tmp.save(getConfigfile(clan));

                    } catch (IOException e) {
                        e.printStackTrace();
                        code = 102;
                        return code;
                    }
                    code=0;
                }else{
                    code=102;
                    return code;
                }

            }else{
                code=101;
                return code;
            }
        }else{
            code=1;
            items.clear();
            prices.clear();
            stocks.clear();
            return code;
        }
        return code;
    }
    private static List<Integer> getStocks(String clan) {
        clan = clan.toUpperCase();
        return getConfig(clan).getIntegerList("stocks");
    }
    private static List<Integer> getPrices(String clan){
        clan=clan.toUpperCase();
        return getConfig(clan).getIntegerList("prices");
    }
    public static List<ItemStack> getItems(String clan){
        clan=clan.toUpperCase();
        List items_= getConfig(clan).getList("items");
        List<String> raw=new ArrayList<>();
        for(Object obj:items_){
            raw.add(String.valueOf(obj));
        }
        List<Material> materials=new ArrayList<Material>();
        List<ItemStack> value=new ArrayList<>();
        for(String obj:raw){
            if(Material.valueOf(obj)!=null){
                materials.add(Material.valueOf(obj));
            }
        }
        for(Material obj:materials){
            ItemStack stack=new ItemStack(obj);
            ItemMeta meta=stack.getItemMeta();
            List<String> lore=new ArrayList<>();
            lore.add("§cPreis: §e"+getPrices(clan).get(materials.indexOf(obj)));
            lore.add("§cAuf Lager: §e"+getStocks(clan).get(materials.indexOf(obj)));
            if(meta!=null){
                meta.setLore(lore);
            }
            stack.setItemMeta(meta);
            value.add(stack);
        }
        return value;
    }
    public static List<Material> getRawItems(String clan){
        clan=clan.toUpperCase();
        List items_= getConfig(clan).getList("items");
        List<String> raw=new ArrayList<>();
        for(Object obj:items_){
            raw.add(String.valueOf(obj));
        }
        if(raw==null){
            return new ArrayList<Material>();
        }
        List<Material> value=new ArrayList<>();
        for(String obj:raw){
            if(Material.valueOf(obj)!=null){
                value.add(Material.valueOf(obj));
            }
        }
        return value;

    }

    public static void buyItem(Material item, String clan, UUID player){
        Main.getPlugin().getLogger().info("Prepairing to sell item...");
        List items_= getConfig(clan).getList("items");
        List<String> rawitems=new ArrayList<>();
        for(Object obj:items_){
            rawitems.add(String.valueOf(obj));
        }
        List<ItemStack> itemstacks=getItems(clan);
        List<Material> materials=new ArrayList<>();
        for(ItemStack obj:itemstacks) {
            materials.add(obj.getType());
            Main.getPlugin().getLogger().info("Located item: "+obj.getType());
        }
        Main.getPlugin().getLogger().info("Getting values...");
        int index=materials.indexOf(item);
        int stock=getStocks(clan).get(index);
        List<Integer> stocks=getStocks(clan);
        List<Integer> prices=getPrices(clan);
        Main.getPlugin().getLogger().info("index:"+index);
        Main.getPlugin().getLogger().info("stock:"+stock);
        Main.getPlugin().getLogger().info("stocks:"+stocks.toString());
        Main.getPlugin().getLogger().info("prices:"+prices.toString());
        Main.getPlugin().getLogger().info("Removing money...");
        EconomySystem.removeMoney(Bukkit.getPlayer(player),prices.get(index));
        Main.getPlugin().getLogger().info("Removing stock...");
        stocks.set(index, stock-1);
        if(stocks.get(index)<=0) {
            rawitems.remove(index);
            materials.remove(index);
            stocks.remove(index);
            prices.remove(index);
        }
        Main.getPlugin().getLogger().info("Giving item...");
        boolean item_given=false;
        for(ItemStack stack:Bukkit.getPlayer(player).getInventory().getContents()){
            Main.getPlugin().getLogger().info("Getting Item...");
            if(stack!=null) {
                if (stack.getType().equals(Material.AIR) || (stack.getType() == item && stack.getMaxStackSize() > stack.getAmount())) {
                    Main.getPlugin().getLogger().info("Checking th other shit...");
                    if (stack.getType() == item && stack.getMaxStackSize() > stack.getAmount()) {
                        stack.setAmount(stack.getAmount() + 1);
                    } else {
                        stack = new ItemStack(item);
                    }
                    item_given = true;
                }
                if (item_given == false) {
                    ItemStack spawn = new ItemStack(item);
                    Bukkit.getPlayer(player).getLocation().getWorld().dropItemNaturally(Bukkit.getPlayer(player).getLocation(), spawn);
                    item_given = true;
                }
            }
        }
        //Convert back to Strings
        FileConfiguration tmp=getConfig(clan);
        tmp.set("items",rawitems);
        tmp.set("prices",prices);
        tmp.set("stocks",stocks);
        try {
            tmp.save(getConfigfile(clan));
        } catch (IOException e) {
            Bukkit.getPlayer(player).sendMessage("§4[Fehler] §cEin Fehler ist aufgetreten!");
        }
        openClanShop(Bukkit.getPlayer(player),menu_open.get(player));
    }

}