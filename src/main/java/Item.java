import java.util.Objects;

public class Item {
    private String city;
    private String street;
    private String house;
    private String floor;

    public Item(String city, String street, String house, String floor) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.floor = floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return city.equals(item.city) && street.equals(item.street) && house.equals(item.house) && floor.equals(item.floor);
    }

    @Override
    public String toString() {
        return "Item{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", floor='" + floor + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, house, floor);
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getFloor() {
        return floor;
    }
}
