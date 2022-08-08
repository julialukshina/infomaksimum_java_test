import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

class XMLHandler extends DefaultHandler {
    private ArrayList<Item> items = new ArrayList<>();

    public ArrayList<Item> getItems() {
        return items;
    }

    @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("item")) {
                String city = attributes.getValue("city");
                String street = attributes.getValue("street");
                String house = attributes.getValue("house");
                String floor = attributes.getValue("floor");
                items.add(new Item(city, street, house, floor));
            }
        }
    }