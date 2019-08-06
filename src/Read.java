import java.io.*;
import java.util.*;

public class Read {


    public static void main(String[] args) throws Exception {
        String path = "src/Book1.txt";
        List<String> l = readFile(path);
        List<Data> data = new ArrayList<>();

        for (String s: l) {
            String[] w = s.split("\t");
            String o = w[0].replace("\"", "");
            String b = w[1].replace("\"", "");
            data.add(new Data(o, b));  //parse entry
        }

        Map<String, List<String>> bg = new HashMap<>();
        Map<String, List<String>> onet = new HashMap<>();

        for (Data d: data) {
            String b = d.bg;
            String o = d.on;

            List<String> o_list = onet.getOrDefault(o, new ArrayList<>());
            o_list.add(b);
            onet.put(o, o_list);

            List<String> b_list = bg.getOrDefault(b, new ArrayList<>());
            b_list.add(o);
            bg.put(b, b_list);

        }

        Map<String, List<String>> onet_missing = new HashMap<>();
        Map<String, List<String>> onet_mapped = new HashMap<>();

        for (Map.Entry<String, List<String>> e: onet.entrySet()) {
            String oname = e.getKey();
           List<String> bglist = e.getValue();
           if (no_match(bglist)) {
               onet_missing.put(oname, bglist);
           }
           else {
               onet_mapped.put(oname, bglist);
           }
        }

//        write_missing(onet_missing, "src/missing.txt");
//        write_mapped(onet_mapped, "src/mapped.txt");
        write_missing(bg, "src/bg.txt");

    }


    static boolean no_match(List<String> l) {
        if (l.size() == 1) {
            if (l.get(0).equals("na")) {
                return true;
            }
        }
        return false;
    }



    public static List<String> readFile(String fileName) throws Exception {
        File file = new File(fileName);
        Scanner input = new Scanner(file);
        List<String> list = new ArrayList<>();

        while (input.hasNextLine()) {
            list.add(input.nextLine());
        }
        return list;
    }

    public static void write_missing( Map<String, List<String>> m, String path) throws IOException {
        FileWriter writer = new FileWriter(path, true);

        for (Map.Entry<String, List<String>> e: m.entrySet()) {
            String k = e.getKey();
            writer.write(k + "\t");

            for (String v: e.getValue()) {
                writer.write(v + ",");
            }
            writer.write("\n");
        }

        writer.close();
        System.out.println("done: " + path);

    }


    public static void write_mapped( Map<String, List<String>> m, String path) throws IOException {
        FileWriter writer = new FileWriter(path, true);

        for (Map.Entry<String, List<String>> e: m.entrySet()) {
            String k = e.getKey();
            writer.write(k + "\t");

            for (String v: e.getValue()) {
                if (!v.equals("na")) {
                    writer.write(v + ",");
                }
            }
            writer.write("\n");
        }

        writer.close();
        System.out.println("done: " + path);

    }




}
