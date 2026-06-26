package org.fw.core.cases;

import java.io.File;

public class PeopleStorage {
    public static void main(String[] args) {
        File saves = new File(".ps");
        saves.mkdirs();

        File file = new File(saves, "data.fw");


    }
}
