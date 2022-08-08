import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    static Map<String, Map<Integer, Integer>> houseCities = new HashMap<>();
    static Map<String, Integer> repeatableCities = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printMenu();
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            if (Files.exists(Paths.get(command))) {
                if (command.endsWith(".csv")) {
                    parseCsv(command);
                    printStatistic();
                    clearMaps();
                }

                if (command.endsWith(".xml")) {
                    parseXml(command);
                    printStatistic();
                    clearMaps();
                }
            } else {
                System.out.println("The path is not correct. Try again");
            }
            printMenu();

//            C:\Users\370\Desktop\infomaksimum\address.csv
//              C:\Users\370\Desktop\infomaksimum\address.xml
//  C:\Users\370\Desktop\infomaksimum\example.csv
//            C:\Users\370\Desktop\infomaksimum\example_xml.xml
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("If you want to get statistics, enter the path to the file" + "\n"
                + "If you want to close the application, click Ctrl+D");
    }

    private static boolean isNumber(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void clearMaps() {
        repeatableCities.clear();
        houseCities.clear();
    }

    private static void completeMaps(String city, Integer house) {
        if (repeatableCities.containsKey(city)) {
            repeatableCities.put(city, repeatableCities.get(city) + 1);
            if (houseCities.get(city).containsKey(house)) {
                houseCities.get(city).put(house,
                        houseCities.get(city).get(house) + 1);
            } else {
                houseCities.get(city).put(house, 1);
            }
        } else {
            repeatableCities.put(city, 0);
            Map<Integer, Integer> floorHouse = new HashMap<>();
            floorHouse.put(2, 0);
            floorHouse.put(3, 0);
            floorHouse.put(4, 0);
            floorHouse.put(5, 0);
            floorHouse.put(house, 1);
            houseCities.put(city, floorHouse);
        }
    }

    private static void printStatistic() {
        System.out.println("Repeatable cities:");
        repeatableCities.entrySet()
                .stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.comparingByKey())
                .forEach(System.out::println);

        for (Map.Entry<String, Map<Integer, Integer>> entry : houseCities.entrySet()) {
            System.out.println("Number of houses in the city:");
            System.out.println(entry.getKey());
            Map<Integer, Integer> value = entry.getValue();
            for (Map.Entry<Integer, Integer> entry1 : value.entrySet()) {
                if (entry1.getKey() > 1 && entry1.getKey() < 6) {
                    System.out.println("Floor's number: " + entry1.getKey() + ", number of houses: "
                            + entry1.getValue());
                }
            }
        }
    }

    public static void parseCsv(String command) {
        try (BufferedReader br = new BufferedReader(new FileReader(command))) {
            while (br.ready()) {
                String line = br.readLine();
                String[] lineContents = line.split(";");
                if (lineContents.length == 4 &&
                        (lineContents[0] != null && !lineContents[0].equals(""))
                        && (lineContents[1] != null && !lineContents[1].equals(""))
                        && isNumber(lineContents[2]) && isNumber(lineContents[3])) {
                    completeMaps(lineContents[0], Integer.parseInt(lineContents[3]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseXml(String command) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            InputSource source = new InputSource(command);
            source.setEncoding("windows-1251");
            reader.parse(source);
            for (Item item : handler.getItems()) {
                if ((item.getCity() != null && !item.getCity().equals(""))
                        && (item.getStreet() != null && !item.getStreet().equals(""))
                        && isNumber(item.getHouse()) && isNumber(item.getFloor())) {

                    completeMaps(item.getCity(), Integer.parseInt(item.getFloor()));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}