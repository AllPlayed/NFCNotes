/*
 * Copyright (C) 2020  Kikisito (Kyllian)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.kikisito.nfcnotes;

import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NFCNote {
    // Attributes will be changed into Namespaces in a major update in the future.
    // A converter is also planned.

    private final ItemStack itemStack;
    private final String name;
    private final List<String> lore;
    private final Double value;

    public NFCNote(ItemStack itemStack){
        this.itemStack = itemStack;
        this.name = itemStack.getItemMeta().getDisplayName();
        this.lore = itemStack.getItemMeta().getLore();
        this.value = itemStack.getItemMeta().getAttributeModifiers(Attribute.GENERIC_FOLLOW_RANGE).iterator().next().getAmount();
    }

    public ItemStack getItemStack(){ return this.itemStack; }

    public String getDisplayName(){ return this.name; }

    public List<String> getLore(){ return this.lore; }

    public Double getValue(){ return this.value; }

    public static ItemStack createNFCNoteItem(String identifier, String name, List<String> lore, String material, String playername, DecimalFormat decimalFormat, Double money, Integer amount){
        String formattedMoney = decimalFormat.format(money);
        ItemStack is = new ItemStack(Material.valueOf(material.toUpperCase()), amount);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Utils.parseMessage(name).replace("{money}", formattedMoney).replace("{issuer}", playername));
        // Parse lore
        List<String> finallore = new ArrayList<>();
        for(String s : lore) finallore.add(Utils.parseMessage(s).replace("{money}", formattedMoney).replace("{issuer}", playername));
        im.setLore(finallore);
        // Note value is stored as an Attribute and then it's hidden, so its name and lore can be safely edited or removed
        im.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, new AttributeModifier(UUID.fromString(identifier), "noteValue", money, AttributeModifier.Operation.ADD_NUMBER));
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        // Custom Model Data for texture packs
        im.setCustomModelData(NFCConfig.NOTE_CUSTOM_MODEL_DATA_INTEGER.getInt());
        // Set ItemMeta
        is.setItemMeta(im);
        return is;
    }

    public static boolean isNFCNote(ItemStack itemStack){
        if(itemStack == null || !itemStack.hasItemMeta()) return false;
        ItemMeta im = itemStack.getItemMeta();
        if(!im.hasAttributeModifiers() || im.getAttributeModifiers(Attribute.GENERIC_FOLLOW_RANGE) == null || im.getAttributeModifiers(Attribute.GENERIC_FOLLOW_RANGE).iterator().next() == null) return false;
        return im.getAttributeModifiers(Attribute.GENERIC_FOLLOW_RANGE).iterator().next().getName().equalsIgnoreCase("noteValue");
    }
}
