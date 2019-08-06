import java.util.Objects;

public class Data {
    String bg;
    String on;

    public Data(String on, String bg) {
        this.on = on;
        this.bg = bg;
    }

    @Override
    public String toString() {
        return  on + "\t" + bg;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Data data = (Data) o;
//        return Objects.equals(bg, data.bg) &&
//                Objects.equals(on, data.on);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(bg, on);
//    }
}
