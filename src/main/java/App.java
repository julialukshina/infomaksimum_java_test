import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    static Map<String, Map<Integer, Integer>> houseCities = new HashMap<>();
    static Map<String, Integer> repeatableLines = new HashMap<>();

    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner scanner = new Scanner(System.in);
        printMenu();
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            if (command.equals("INFOMAKSIMUM")) {
                System.out.println("The application is closed");
                break;
            }
            if (Files.exists(Paths.get(command))) {
                if (command.endsWith(".csv")) {
                    parseCsv(command, "utf-8");
                    printStatistic("utf-8");
                    clearMaps();
                }

                if (command.endsWith(".xml")) {
                    parseXml(command, "windows-1251");
                    printStatistic("windows-1251");
                    clearMaps();
                }
            } else {
                System.out.println("The path is not correct. Try again");
            }
            printMenu();
        }
        scanner.close();
    }

    /**
     * Метод, печатающий меню
     */
    private static void printMenu() {
        System.out.println("If you want to get statistics, enter the path to the file" + "\n"
                + "If you want to close the application, click Ctrl+D or enter INFOMAKSIMUM");
    }

    /**
     * Метод используется при анализе данных из файла. Определяет, являются ли данные числом
     *
     * @param s
     * @return
     * @throws NumberFormatException
     */
    private static boolean isNumber(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Метод для очистки мап. Используется после анализа каждого файла
     */
    public static void clearMaps() {
        repeatableLines.clear();
        houseCities.clear();
    }

    /**
     * Метод для заполнения мап. Мапы заполняются всеми данными по городам и этажам, чтобы имелась возможность
     * расширения функционала
     *
     * @param city
     * @param house
     */
    private static void completeMaps(String line, String city, Integer house) {
        if (repeatableLines.containsKey(line)) {
            repeatableLines.put(line, repeatableLines.get(line) + 1);
        } else {
            repeatableLines.put(line, 0);
            if (!houseCities.containsKey(city)) {
                Map<Integer, Integer> floorHouse = new HashMap<>();
                floorHouse.put(2, 0);
                floorHouse.put(3, 0);
                floorHouse.put(4, 0);
                floorHouse.put(5, 0);
                floorHouse.put(house, 1);
                houseCities.put(city, floorHouse);
            } else if (houseCities.get(city).containsKey(house)) {
                houseCities.get(city).put(house,
                        houseCities.get(city).get(house) + 1);
            } else {
                houseCities.get(city).put(house, 1);
            }
        }
    }

    /**
     * Метода для вывода предусмотренной ТЗ статистики
     *
     * @param encoding
     * @throws UnsupportedEncodingException
     */
    public static void printStatistic(String encoding) throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, encoding));
        if (!repeatableLines.isEmpty()) {
            System.out.println("Repeatable lines:");
            repeatableLines.entrySet()
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
        } else {
            System.out.println("The file have not suitable information");
        }
    }

    /**
     * Метод для разбора csv-файла
     *
     * @param command
     * @param encoding
     */
    public static void parseCsv(String command, String encoding) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(command), Charset.forName(encoding))) {
            while (br.ready()) {
                String line = br.readLine();
                String[] lineContents = line.split(";");
                if (lineContents.length == 4 &&
                        (lineContents[0] != null && !lineContents[0].equals(""))
                        && (lineContents[1] != null && !lineContents[1].equals(""))
                        && isNumber(lineContents[2]) && isNumber(lineContents[3])) {
                    StringBuilder sb = new StringBuilder(lineContents[0]);
                    String city = sb.substring(1, lineContents[0].length() - 1);
                    completeMaps(line, city, Integer.parseInt(lineContents[3]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для разбора xml-файла
     *
     * @param command
     * @param encoding
     */
    public static void parseXml(String command, String encoding) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            InputSource source = new InputSource(command);
            source.setEncoding(encoding);
            reader.parse(source);
            for (Item item : handler.getItems()) {
                if ((item.getCity() != null && !item.getCity().equals(""))
                        && (item.getStreet() != null && !item.getStreet().equals(""))
                        && isNumber(item.getHouse()) && isNumber(item.getFloor())) {
                    completeMaps(item.toString(), item.getCity(), Integer.parseInt(item.getFloor()));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}