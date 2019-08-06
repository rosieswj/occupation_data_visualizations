import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Readfile {

    public static List<String> readFile(String fileName) throws Exception {
        File file = new File(fileName);
        Scanner input = new Scanner(file);
        List<String> list = new ArrayList<>();

        while (input.hasNextLine()) {
            list.add(input.nextLine());
        }
        return list;
    }
}
