package control;

import model.File;
import model.List;

/**
 * Created by Jean-Pierre on 05.11.2016.
 */
public class MainController {

    private final List<File>[] allShelves;    //Ein Array, das Objekte der Klasse Liste verwaltet, die wiederum Objekte der Klasse File verwalten.

    public MainController() {
        allShelves = new List[2];
        allShelves[0] = new List<File>(); //Beachtet die unterschiedliche Instanziierung! Was bedeutet das?
        allShelves[1] = new List<>();
        createFiles();
    }

    /**
     * Die Akten eines Regals werden vollständig ausgelesen.
     *
     * @param index Regalnummer
     * @return String-Array mit den Familiennamen
     */
    public String[] showShelfContent(int index) {
        List<File> list = allShelves[index];
        int size = 0;
        list.toFirst();
        while (list.hasAccess()) {
            size++;
            list.next();
        }
        String[] output = new String[size];
        list.toFirst();
        for (int i = 0; i < output.length; i++) {
            output[i] = list.getContent().getName();
            list.next();
        }
        return output;
    }

    /**
     * Ein Regal wird nach Familiennamen aufsteigend sortiert.
     *
     * @param index Regalnummer des Regals, das sortiert werden soll.
     * @return true, falls die Sortierung geklappt hat, sonst false.
     */
    public boolean sort(int index) {
        if (index >= 0 && index < allShelves.length) {
            int counter = 0;
            allShelves[index].toFirst();
            while (allShelves[index].hasAccess()) {
                allShelves[index].next();
                counter++;
            }
            File[] arr = new File[counter];
            allShelves[index].toFirst();
            for (int i = 0; i < arr.length; i++) {
                arr[i] = allShelves[index].getContent();
                allShelves[index].remove();
            }
            int longest = 0;
            for (File file :
                    arr) {
                if (file != null) {
                    if (file.getName().length() > longest) longest = file.getName().length();
                }
            }
            arr = raddixSortUltraRecrsive(arr, longest);
            allShelves[index].insert(arr[arr.length - 1]);
            allShelves[index].toFirst();
            for (int i = 0; i < arr.length - 1; i++) {
                allShelves[index].toLast();
                allShelves[index].insert(arr[i]);
            }
            return true;
        }
        return false;
    }

    public File[] raddixSortUltraRecrsive(File[] arr, int depth) {

        if (depth > 0) {
            int[] counting = new int[27];
            for (File file :
                    arr) {
                if (file.getName().length() < depth) {
                    counting[0]++;
                } else {
                    int charValue = file.getName().charAt(depth - 1);
                    if (charValue == 32 || charValue == 46) counting[0]++;
                    else {
                        if (charValue < 97) {
                            charValue += 32;
                        }
                        charValue -= 96;
                        counting[charValue]++;

                    }

                }

            }
            for (int i = 1; i < 27; i++) {
                counting[i] = counting[i] + counting[i - 1];
            }
            for (int i = 26; i > 0; i--) {
                counting[i] = counting[i - 1];
            }
            counting[0] = 0;
            File[] newSorted = new File[arr.length];

            for (File file :
                    arr) {
                if (file.getName().length() < depth) {
                    newSorted[counting[0]] = file;
                    counting[0]++;
                } else {
                    int charValue = file.getName().charAt(depth - 1);
                    if (charValue == 32 || charValue == 46) {
                        newSorted[counting[0]] = file;
                        counting[0]++;
                    } else {
                        if (charValue < 97) {
                            charValue += 32;
                        }
                        charValue -= 96;
                        newSorted[counting[charValue]] = file;
                        counting[charValue]++;

                    }

                }

            }
            arr = newSorted;
            return raddixSortUltraRecrsive(arr, depth - 1);
        }
        return arr;

    }

    /**
     * Die gesammte Aktensammlung eines Regals wird zur Aktensammlung eines anderen Regals gestellt.
     *
     * @param from Regalnummer, aus dem die Akten genommen werden. Danach sind in diesem Regal keine Akten mehr.
     * @param to   Regalnummer, in das die Akten gestellt werden.
     * @return true, falls alles funktionierte, sonst false.
     */
    public boolean appendFromTo(int from, int to) {
        if (allShelves[from].isEmpty()) return false;
        allShelves[to].concat(allShelves[from]);
        return true;
    }

    /**
     * Es wird eine neue Akte erstellt und einem bestimmten Regal hinzugefügt.
     *
     * @param index       Regalnummer
     * @param name        Name der Familie
     * @param phoneNumber Telefonnummer der Familie
     * @return true, falls das Hinzufügen geklappt hat, sonst false.
     */
    public boolean appendANewFile(int index, String name, String phoneNumber) {
        if (index < allShelves.length && !name.equals("") && !phoneNumber.equals("")) {
            allShelves[index].append(new File(name, phoneNumber));
            return true;
        }
        return false;
    }

    /**
     * Es wird eine neue Akte in ein Regal eingefügt. Funktioniert nur dann sinnvoll, wenn das Regal vorher bereits nach Namen sortiert wurde.
     *
     * @param index       Regalnummer, in das die neue Akte einsortiert werden soll.
     * @param name        Name der Familie
     * @param phoneNumber Telefonnummer der Familie
     * @return true, falls das Einfügen geklappt hat, sonst false.
     */
    public boolean insertANewFile(int index, String name, String phoneNumber) {
        if (index >= 0 && index < allShelves.length && name.equalsIgnoreCase("")) {
            List<File> list = allShelves[index];
            list.toFirst();
            while (list.hasAccess()) {
                if (list.getContent().getName().compareTo(name) < 0) {
                    list.insert(new File(name, phoneNumber));
                    return true;
                }
                list.next();
            }
        }
        return false;
    }

    /**
     * Es wird nach einer Akte gesucht.
     *
     * @param name Familienname, nach dem gesucht werden soll.
     * @return Zahlen-Array der Länge 2. Bei Index 0 wird das Regal, bei Index 1 die Position der Akte angegeben. Sollte das Element - also die Akte zum Namen - nicht gefunden werden, wird {-1,-1} zurückgegeben.
     */
    public int[] search(String name) {
        int j;
        for (int i = 0; i < allShelves.length; i++) {
            j = 0;
            allShelves[i].toFirst();
            while (!allShelves[i].isEmpty()) {
                if (allShelves[i].getContent().getName().equalsIgnoreCase(name)) {
                    return new int[]{i, j};
                }
                allShelves[i].next();
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * Eine Akte wird entfernt. Dabei werden die enthaltenen Informationen ausgelesen und zurückgegeben.
     *
     * @param shelfIndex Regalnummer, aus dem die Akte entfernt wird.
     * @param fileIndex  Aktennummer, die entfernt werden soll.
     * @return String-Array der Länge 2. Index 0 = Name, Indedx 1 = Telefonnummer.
     */
    public String[] remove(int shelfIndex, int fileIndex) {
        String[] out;
        if (shelfIndex >= 0 && shelfIndex < allShelves.length) {
            int counter = 0;
            allShelves[shelfIndex].toFirst();
            while (allShelves[shelfIndex].hasAccess()) {
                if (counter == fileIndex) {
                    out = new String[]{allShelves[shelfIndex].getContent().getName(), allShelves[shelfIndex].getContent().getPhoneNumber()};
                    allShelves[shelfIndex].remove();
                    return out;
                }
                counter++;
                allShelves[shelfIndex].next();
            }
            out = new String[]{"" + shelfIndex, "Nicht vorhanden!"};
            return out;

        }
        return new String[]{"Nicht vorhanden", "Nicht vorhanden"};
    }

    /**
     * Es werden 14 zufällige Akten angelegt und zufällig den Regalen hinzugefügt.
     */
    private void createFiles() {
        for (int i = 0; i < 14; i++) {
            int shelfIndex = (int) (Math.random() * allShelves.length);

            int nameLength = (int) (Math.random() * 5) + 3;
            String name = "";
            for (int j = 0; j < nameLength; j++) {
                name = name + (char) ('A' + (int) (Math.random() * 26));
            }

            int phoneLength = (int) (Math.random() * 2) + 8;
            String phone = "0";
            for (int k = 1; k < phoneLength; k++) {
                phone = phone + (int) (Math.random() * 10);
            }

            appendANewFile(shelfIndex, name, phone);
        }
    }
}
