package de.legoshi.td2core.discord.progress.section2;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class Cubes extends ProgressMap {
    
    public Cubes() {
        super("Cubes");
    }
    
    @Override
    public void addProgressLine() {
        add("__Start__");
        add("*C1*");
        add("**C2**: ");
        add("**C3**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C4+");
        add("--------------------------------------");
        add("**C4**: ");
        add("**C5**: ");
        add("**C6**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C7+");
        add("--------------------------------------");
        add("**C7**: ");
        add("**C8**: ");
        add("**C9**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C10+");
        add("--------------------------------------");
        add("**C10**: ");
        add("**C10 - A**: ");
        add("**C10 - B**: ");
        add("**C10 - C**: ");
        add("**C10 - D**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C10 E+");
        add("--------------------------------------");
        add("**C10 - E**: ");
        add("**C10 - F**: ");
        add("**C10 - G**: ");
        add("--------------------------------------"); //30
        add("Section 2 | Cubes C10 H+");
        add("--------------------------------------");
        add("**C10 - H**: ");
        add("**C10 - I**: ");
        add("**C10 - J**: ");
        add("**C10 - K**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C10 0+");
        add("--------------------------------------");
        add("**C10 - 0**: ");
        add("**C10 - 1**: ");
        add("**C10 - 2**: ");
        add("**C10 - 3**: ");
        add("**C10 - 4**: ");
        add("**C10 - 5**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C10 6+");
        add("--------------------------------------");
        add("**C10 - 6**: ");
        add("**C10 - 7**: ");
        add("**C10 - 8**: ");
        add("**C10 - 9**: ");
        add("**C10 - 10**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes C10 11+");
        add("--------------------------------------");
        add("**C10 - 11**: ");
        add("**C10 - 12**: ");
        add("**C10 - Final Cube**: ");
        add("--------------------------------------");
        add("Section 2 | Cubes SKY");
        add("--------------------------------------");
        add("**SKY**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 2);
        addProgressMapping(2, 3);
        addProgressMapping(3, 7);
        addProgressMapping(4, 8);
        addProgressMapping(5, 9);
        addProgressMapping(6, 13);
        addProgressMapping(7, 14);
        addProgressMapping(8, 15);
        addProgressMapping(9, 19);
        addProgressMapping(10, 20);
        addProgressMapping(11, 21);
        addProgressMapping(12, 22);
        addProgressMapping(13, 23);
        addProgressMapping(14, 27);
        addProgressMapping(15, 28);
        addProgressMapping(16, 29);
        addProgressMapping(17, 33);
        addProgressMapping(18, 34);
        addProgressMapping(19, 35);
        addProgressMapping(20, 36);
        addProgressMapping(21, 40);
        addProgressMapping(22, 41);
        addProgressMapping(23, 42);
        addProgressMapping(24, 43);
        addProgressMapping(25, 44);
        addProgressMapping(26, 45);
        addProgressMapping(27, 49);
        addProgressMapping(28, 50);
        addProgressMapping(29, 51);
        addProgressMapping(30, 52);
        addProgressMapping(31, 53);
        addProgressMapping(32, 57);
        addProgressMapping(33, 58);
        addProgressMapping(34, 59);
        addProgressMapping(35, 63);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(3, "Section 2 | Cubes C4+");
        addRoleMapping(6, "Section 2 | Cubes C7+");
        addRoleMapping(9, "Section 2 | Cubes C10+");
        addRoleMapping(14, "Section 2 | Cubes C10 E+");
        addRoleMapping(17, "Section 2 | Cubes C10 H+");
        addRoleMapping(21, "Section 2 | Cubes C10 0+");
        addRoleMapping(27, "Section 2 | Cubes C10 6+");
        addRoleMapping(32, "Section 2 | Cubes C10 11+");
        addRoleMapping(35, "Section 2 | Cubes SKY");
        addRoleMapping(36, "Section 2 | Cubes VICTOR");
    }
}
