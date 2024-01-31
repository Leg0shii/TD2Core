package de.legoshi.td2core.util;

import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class CustomHeads {

    private static final boolean newStorageSystem;
    
    public static String headString = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViOTVkYTEyODE2NDJkYWE1ZDAyMmFkYmQzZTdjYjY5ZGMwOTQyYzgxY2Q2M2JlOWMzODU3ZDIyMmUxYzhkOSJ9fX0=";
    public static ItemStack helpHead = create(headString);
    
    public static String leaderboardString = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM0YTU5MmE3OTM5N2E4ZGYzOTk3YzQzMDkxNjk0ZmMyZmI3NmM4ODNhNzZjY2U4OWYwMjI3ZTVjOWYxZGZlIn19fQ==";
    public static ItemStack leaderboardHead = create(leaderboardString);
    
    public static ItemStack section1 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg1MTlkNDUwNWY0Y2VlNGMwOWI2OGRiMzUwYmIxMTNlNzUzZDdhMGVmNjFkN2U0YzQwNDhiZWU4NGExNTdhNSJ9fX0=");
    public static ItemStack section2 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc2MzhjMzhkMzlhNjdkODk2MjY3NDNmMDZiOWM3YmU1YzUwY2Y4MzM1ZDJlOGYzOWViMWRhZDBkZjBmNzNkNiJ9fX0=");
    public static ItemStack section3 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZlNGE0OWUwODA0MDVhYTgxM2FmZmMxMWYyMDNlMmQ0Y2NmMzk3OGMyN2ZmODYxOTVkNzc2NjZiOGE4NWYzOSJ9fX0=");
    public static ItemStack section5 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDliMzAzMDNmOTRlN2M3ODVhMzFlNTcyN2E5MzgxNTM1ZGFmNDc1MzQ0OWVhNDFkYjc0NmUxMjM0ZTlkZDJiNSJ9fX0=");
    public static ItemStack section6 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjExNzZjNGQ2Mzk1ZmY1NzY3YTc0YTM2OWZlMzg2ZDA2Y2M2MGEyMDk3YmM1YTUzYmQwMDVlYWRkMGE3Y2JkNCJ9fX0=");
    public static ItemStack section7 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA2NzA2ZWVjYjJkNTU4YWNlMjdhYmRhMGIwYjdiODAxZDM2ZDE3ZGQ3YTg5MGE5NTIwZGJlNTIyMzc0ZjhhNiJ9fX0=");
    public static ItemStack section8 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBiYWRiZmIwNmVlOTc3MTRkZDU1ZTE2MmQwY2U2NzQyZGEwMzk2YTZkMjkxMzQxZTA0M2QwZWRiNDBjY2JmMSJ9fX0=");
    public static ItemStack section9 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIzYmFlNDFkMTMzMzQyNmI2MjVlMTQxZDlkNDgwNmI2ZjQ2NDFkNjc1NGQ3ZTE5ZTRiYmVmYTM3ZmU1MTMifX19");
    public static ItemStack section10 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNmYmVhMjg3YjNhNjcyZWUzMjRjNzIwZTc3YWY4ZjczMGY4NTFkMjBkYWQ5ZmYxZmExYzA1MWVkZTViYzgxMyJ9fX0=");
    public static ItemStack section11 = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTViNTJhNWJhNDdiNDg3YTRlYTcyM2NjZjQwNGIzM2FjOWVkODA0MjhjNjI2YzA5OWViZWU0YmI3ZTZmNjM2MyJ9fX0=");
    public static ItemStack checkMark = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=");
    public static ItemStack crossMark = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQwYTE0MjA4NDRjZTIzN2E0NWQyZTdlNTQ0ZDEzNTg0MWU5ZjgyZDA5ZTIwMzI2N2NmODg5NmM4NTE1ZTM2MCJ9fX0=");
    public static ItemStack indexEnabled = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZhNWRhNWE0ZDJiODIxM2IyZTk4ZTJjNWJlNWJkODlhNWM3OTE0OTJjYTdjNDZjMTc0ZDlhMTM3NmYwNTAzZCJ9fX0=");
    public static ItemStack indexDisabled = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzEwNTkxZTY5MDllNmEyODFiMzcxODM2ZTQ2MmQ2N2EyYzc4ZmEwOTUyZTkxMGYzMmI0MWEyNmM0OGMxNzU3YyJ9fX0=");
    
    static {
        String versionString = Bukkit.getBukkitVersion();
        int[] version = Arrays.stream(versionString.substring(0, versionString.indexOf('-')).split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
        newStorageSystem = version[0] > 1
                || (version[0] == 1 && version[1] > 16)
                || (version[0] == 1 && version[1] == 16 && version[2] >= 1);
    }

    /**
     * Creates a skull item stack that uses the given base64-encoded texture
     *
     * @param texture The texture value. Can be found on e.g. https://minecraft-heads.com/custom-heads/
     *                in the "Value" field.
     * @return an ItemStack with this texture.
     */
    public static ItemStack create(String texture) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        try {
            String nbtString = String.format(
                    "{SkullOwner:{Id:%s,Properties:{textures:[{Value:\"%s\"}]}}}",
                    serializeUuid(UUID.randomUUID()), texture
            );
            NBTTagCompound nbt = MojangsonParser.parse(nbtString);
            nmsItem.setTag(nbt);
        } catch (Exception e) {
            throw new AssertionError("NBT Tag parsing failed - This should never happen.", e);
        }
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private static String serializeUuid(UUID uuid) {
        if (newStorageSystem) {
            StringBuilder result = new StringBuilder();
            long msb = uuid.getMostSignificantBits();
            long lsb = uuid.getLeastSignificantBits();
            return result.append("[I;")
                    .append(msb >> 32)
                    .append(',')
                    .append(msb & Integer.MAX_VALUE)
                    .append(',')
                    .append(lsb >> 32)
                    .append(',')
                    .append(lsb & Integer.MAX_VALUE)
                    .append(']')
                    .toString();
        } else {
            return '"' + uuid.toString() + '"';
        }
    }
}
