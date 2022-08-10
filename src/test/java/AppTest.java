import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private String test1 = "Repeatable lines:\r\n" +
            "\"Братск\";\"7-я Вишнёвая улица\";49;5=1\r\n";
    private String test2 = "Repeatable lines:\r\n" +
            "Item{city='Братск', street='7-я Вишнёвая улица', house='49', floor='5'}=1\r\n";
    private String test3 = "Number of houses in the city:\r\n" +
            "Братск\r\n" +
            "Floor's number: 2, number of houses: 0\r\n" +
            "Floor's number: 3, number of houses: 0\r\n" +
            "Floor's number: 4, number of houses: 0\r\n" +
            "Floor's number: 5, number of houses: 1\r\n" +
            "Number of houses in the city:\r\n" +
            "Балашов\r\n" +
            "Floor's number: 2, number of houses: 0\r\n" +
            "Floor's number: 3, number of houses: 0\r\n" +
            "Floor's number: 4, number of houses: 0\r\n" +
            "Floor's number: 5, number of houses: 0\r\n";

    /**
     * Перед каждым тестом мапы чистятся
     */
    @BeforeEach
    void clearMaps() {
        App.clearMaps();
    }

    /**
     * проверка корректности работы с csv-файлом
     */
    @Test
    void correctParseCsv() {
        App.parseCsv("src/example.csv", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableLines.isEmpty());
        assertEquals(App.houseCities.size(), 12);
        assertEquals(App.repeatableLines.size(), 13);
        assertEquals(App.houseCities.get("Братск").get(5), 2);
        assertEquals(App.repeatableLines.get("\"Азов\";\"Просека, улица\";156;3"), 1);
        assertEquals(App.houseCities.get("Бийск").get(3), 1);
        assertEquals(App.repeatableLines.get("\"Бийск\";\"Сиреневая улица\";14;3"), 0);
    }

    /**
     * проверка корректности работы с xml-файлом
     */
    @Test
    void correctParseXml() {
        App.parseXml("src/example_xml.xml", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableLines.isEmpty());
        assertEquals(App.houseCities.size(), 10);
        assertEquals(App.repeatableLines.size(), 11);
        assertEquals(App.houseCities.get("Братск").get(5), 2);
        Item item = new Item("Азов", "Просека, улица", "156", "3");
        assertEquals(App.repeatableLines.get(item.toString()), 1);
        item = new Item("Ачинск", "Варшавское шоссе", "39", "4");
        assertEquals(App.houseCities.get(item.getCity()).get(4), 1);
        assertEquals(App.repeatableLines.get(item.toString()), 0);

    }

    /**
     * проверка корректности очистки мап
     */
    @Test
    void correctClearMaps() {
        App.parseCsv("src/example.csv", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableLines.isEmpty());
        App.clearMaps();
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableLines.isEmpty());
        App.parseXml("src/example_xml.xml", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableLines.isEmpty());
        App.clearMaps();
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableLines.isEmpty());
    }

    /**
     * проверка корректности обработки неверного пути к файлу
     */
    @Test
    void correctWorkWithIncorrectPath() {
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableLines.isEmpty());
        App.parseCsv("examples", "utf-8");
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableLines.isEmpty());
    }

    /**
     * проверка корректности вывода статистики для csv-файло
     */
    @Test
    public void testStatisticCsv() throws UnsupportedEncodingException {
        App.parseCsv("src/test_statistic.csv", "utf-8");
        System.setOut(new PrintStream(output));
        App.printStatistic("windows-1251");
        assertEquals(test1 + test3, output.toString());
        System.setOut(null);
    }

    /**
     * проверка корректности вывода статистики для xml-файло
     */
    @Test
    public void testStatisticXml() throws UnsupportedEncodingException {
        App.parseXml("src/test_statistic_xml.xml", "utf-8");
        System.setOut(new PrintStream(output));
        App.printStatistic("windows-1251");
        assertEquals(test2 + test3, output.toString());
        System.setOut(null);
    }

}
