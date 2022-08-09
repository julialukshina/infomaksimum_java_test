import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final String TEST = "Repeatable cities:\r\n" +
            "Братск=1\r\n" +
            "Number of houses in the city:\r\n" +
            "Братск\r\n" +
            "Floor's number: 2, number of houses: 0\r\n" +
            "Floor's number: 3, number of houses: 0\r\n" +
            "Floor's number: 4, number of houses: 0\r\n" +
            "Floor's number: 5, number of houses: 2\r\n" +
            "Number of houses in the city:\r\n" +
            "Балашов\r\n" +
            "Floor's number: 2, number of houses: 0\r\n" +
            "Floor's number: 3, number of houses: 0\r\n" +
            "Floor's number: 4, number of houses: 0\r\n" +
            "Floor's number: 5, number of houses: 0\r\n";
    @BeforeEach
    void clearMaps() {
        App.clearMaps();
    }

    @Test
    void correctParseCsv() {
        App.parseCsv("src/example.csv", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableCities.isEmpty());
        assertEquals(App.houseCities.size(), 13);
        assertEquals(App.repeatableCities.size(), 13);
        assertEquals(App.houseCities.get("Братск").get(5), 2);
        assertEquals(App.repeatableCities.get("Братск"), 1);
        assertEquals(App.houseCities.get("Бийск").get(3), 1);
        assertEquals(App.repeatableCities.get("Бийск"), 0);
    }

    @Test
    void correctParseXml() {
        App.parseXml("src/example_xml.xml", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableCities.isEmpty());
        assertEquals(App.houseCities.size(), 11);
        assertEquals(App.repeatableCities.size(), 11);
        assertEquals(App.houseCities.get("Братск").get(5), 2);
        assertEquals(App.repeatableCities.get("Братск"), 1);
        assertEquals(App.houseCities.get("Ачинск").get(4), 1);
        assertEquals(App.repeatableCities.get("Ачинск"), 0);
    }

    @Test
    void correctClearMaps() {
        App.parseCsv("src/example.csv", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableCities.isEmpty());
        App.clearMaps();
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableCities.isEmpty());
        App.parseXml("src/example_xml.xml", "utf-8");
        assertFalse(App.houseCities.isEmpty());
        assertFalse(App.repeatableCities.isEmpty());
        App.clearMaps();
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableCities.isEmpty());
    }

    @Test
    void correctWorkWithIncorrectPath() {
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableCities.isEmpty());
        App.parseCsv("examples", "utf-8");
        assertTrue(App.houseCities.isEmpty());
        assertTrue(App.repeatableCities.isEmpty());
    }

    @Test
    public void testStatisticCsv() throws UnsupportedEncodingException {
        App.parseCsv("src/test_statistic.csv", "utf-8");
        System.setOut(new PrintStream(output));
        App.printStatistic("windows-1251");
       assertEquals(TEST, output.toString());
        System.setOut(null);
    }

    @Test
    public void testStatisticXml() throws UnsupportedEncodingException {
        App.parseXml("src/test_statistic_xml.xml", "utf-8");
        System.setOut(new PrintStream(output));
        App.printStatistic("windows-1251");
        assertEquals(TEST, output.toString());
        System.setOut(null);
    }

}
