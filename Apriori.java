package bakalarkaskuska1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Apriori {

    static Scanner s = new Scanner(System.in);
    static String[][] transactionDatabase;
    static int minsup;

    static List<String> polozky = new ArrayList<String>();
    static List<String[]> items = new ArrayList<String[]>();
    static List<Item> frequentItemsets = new ArrayList<Item>();
    static List<Item> nonFrequentItemsets = new ArrayList<Item>();

    static int iteration = 1;
    static int maxlength = 0;

    public static void main(String[] args) throws Exception {
        /*
        - selektovanie jednotlivych poloziek zo vstupnej databazy
        - vytvorenie inicializacnych jednoprvkovych mnozin objektu Item
         */
        transactionDatabase = readFromFile();

        System.out.println("Zadajte minimalnu podporu: ");
        minsup = s.nextInt();
        for (int i = 0; i < transactionDatabase.length; i++) {
            if (transactionDatabase[i].length > maxlength) {
                maxlength = transactionDatabase[i].length;
            }
            for (int j = 0; j < transactionDatabase[i].length; j++) {
                if (!polozky.contains(transactionDatabase[i][j])) {
                    String polozka = transactionDatabase[i][j];
                    polozky.add(polozka);
                    String[] tmp = {polozka};
                    Item item = new Item();
                    item.setItems(tmp);
                    frequentItemsets.add(item);
                }
            }
        }
        check();
    }

    public static void checkItems() {
        /*
        zistovanie, kolkokrat sa mnozina nachadza vo vstupnej databaze a zapisovanie tohto udaja objeku Item
        zadanie, ze vsetky mnoziny boli uz pouzite
        vylucovaci krok algoritmu
         */
        for (int i = 0; i < frequentItemsets.size(); i++) {
            //System.out.println(Arrays.toString(frequentItemsets.get(i).getItems()));
            if (!frequentItemsets.get(i).isUsed() && minsup > 1) {
                String[] itemCheck = new String[iteration];
                int count = 0;
                for (int k = 0; k < transactionDatabase.length; k++) {
                    String[] tmp = frequentItemsets.get(i).getItems();
                    Arrays.sort(tmp);
                    Arrays.sort(transactionDatabase[k]);
                    for (int j = 0; j < tmp.length; j++) {
                        for (int l = 0; l < transactionDatabase[k].length; l++) {
                            //System.out.print(transactionDatabase[k][l] + ": " + tmp[j]);
                            //System.out.println();
                            if (tmp[j].equals(transactionDatabase[k][l])) {
                                itemCheck[count] = tmp[j];
                                // System.out.println("Count: " + itemCheck[count]);
                                count++;
                            }
                        }
                        //   System.out.println(Arrays.toString(itemCheck));
                    }
                    for (int j = 0; j < frequentItemsets.size(); j++) {
                        Item item = new Item();
                        item = frequentItemsets.get(j);
                        if (Arrays.equals(item.getItems(), itemCheck)) {
                            //System.out.println(Arrays.toString(item.getItems()) + ": " + Arrays.toString(itemCheck));
                            item.setCount(item.getCount() + 1);
                        }
                    }
                    Arrays.fill(itemCheck, null);
                    count = 0;
                }
                frequentItemsets.get(i).setUsed(true);
            }
        }
        /*
        vylucovaci krok
         */
        int x = 0;
        while (x < frequentItemsets.size()) {
            Item item = new Item();
            item = frequentItemsets.get(x);
            if (frequentItemsets.get(x).getCount() < minsup) {
                nonFrequentItemsets.add(frequentItemsets.get(x));
                frequentItemsets.remove(frequentItemsets.get(x));
            } else {
                x++;
            }
        }
    }

    public static void check() {
        if (iteration <= maxlength) {
            checkItems();
            iteration++;
            generate();
        } else {
            System.out.println("--- Frekventovane mnoziny ---");
            System.out.println();
            for (int i = 0; i < frequentItemsets.size(); i++) {
                Item item = new Item();
                item = frequentItemsets.get(i);
                System.out.println(Arrays.toString(item.getItems()) + ":" + item.getCount() + " - " + item.isUsed());
            }
            System.out.println();
            System.out.println("--- Nefrekventovane mnoziny ---");
            System.out.println();
            for (int i = 0; i < nonFrequentItemsets.size(); i++) {
                Item item = new Item();
                item = nonFrequentItemsets.get(i);
                System.out.println(Arrays.toString(item.getItems()) + ":" + item.getCount() + " - " + item.isUsed());

            }
        }
    }

    /*
    spojovaci krok algoritmu - generovanie jednotlivych mnozin
     */
    public static void generate() {

        for (int i = 0; i < frequentItemsets.size(); i++) {
            for (int j = i + 1; j < frequentItemsets.size(); j++) {
                String[] itmp = frequentItemsets.get(i).getItems();
                //System.out.println(frequentItemsets.get(i).isUsed() + ", " + frequentItemsets.get(j).isUsed());
                if (frequentItemsets.get(i).getItems().length == frequentItemsets.get(j).getItems().length) {
                    if (frequentItemsets.get(i).isUsed() && frequentItemsets.get(j).isUsed() && !Arrays.equals(frequentItemsets.get(i).getItems(), frequentItemsets.get(j).getItems())) {
                        //System.out.println("Pouzite, " + !Arrays.equals(frequentItemsets.get(i).getItems(), frequentItemsets.get(j).getItems()));
                        //System.out.println("itmp: " + (itmp.length + 1) + ", iteration: " + iteration);
                        if (itmp.length + 1 == iteration) {
                            //System.out.println(Arrays.toString(frequentItemsets.get(i).getItems()) + ", " + Arrays.toString(frequentItemsets.get(j).getItems()));
                            concateArray(frequentItemsets.get(i).getItems(), frequentItemsets.get(j).getItems());
                        }
                    }
                }
            }
        }
        check();
    }

    public static void concateArray(String[] first, String[] second) {
        //System.out.println(Arrays.toString(first) + ", " + Arrays.toString(second));
        int count = 0;
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < second.length; j++) {
                if (first[i].equals(second[j])) {
                    count++;
                }
            }
        }
        if (count == first.length - 1) {
            //System.out.println("PLATI");
            Set<String> zoznam = new HashSet<String>();
            for (int i = 0; i < first.length; i++) {
                zoznam.add(first[i]);
                zoznam.add(second[i]);
            }
            String[] tmp = zoznam.toArray(new String[iteration]);
            Arrays.sort(tmp);
            zoznam.clear();
            boolean nachadzaSa = false;
            for (int i = 0; i < items.size(); i++) {
                //System.out.println(Arrays.toString(items.get(i)) + ":" + Arrays.toString(tmp));
                if (Arrays.equals(items.get(i), tmp)) {
                    nachadzaSa = true;
                    //System.out.println(tmp + ": " + nachadzaSa);
                }
            }
            //System.out.println("--------------------------");
            if (!nachadzaSa) {
                Item item = new Item();
                item.setItems(tmp);
                items.add(tmp);
                frequentItemsets.add(item);
            }
        } else {
            //System.out.println("NEPLATI");
        }
    }

    /*
    nacitanie vstupnych dat zo suboru
     */
    public static String[][] readFromFile() throws Exception {
        File file = new File("src/bakalarkaskuska1/test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String[]> tmpS = new ArrayList<String[]>();

        String st;
        while ((st = br.readLine()) != null) {
            String[] data = st.split(" ");
            tmpS.add(data);
        }

        String[][] input = new String[tmpS.size()][];
        for (int i = 0; i < tmpS.size(); i++) {
            input[i] = tmpS.get(i);
        }
        return input;
    }
}

class Item {

    String[] items;
    int count;
    boolean used;

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
