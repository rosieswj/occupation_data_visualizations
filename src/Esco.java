import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Esco {


    public static void main(String[] args) throws Exception  {
        String path = "src/parse_esco.txt";
        List<String> l = Readfile.readFile(path);

        Set<String> todo = new HashSet<>();
        Map<String, String> m = new HashMap<>();

        for (String s: l) {
            String[] ws = s.split("\t");
            String id = ws[0];
            String name = ws[1];

            if (id.length()==3) {
                todo.add(id);
            }  //is last level

            m.put(id, name);
        }

//        writeFlatTable(m, todo);
        writeJson2(m);
    }



    static void writeJson2(Map<String, String> m) throws IOException {

        List<String> l1 = new ArrayList<>();
        List<String> l2 = new ArrayList<>();
        List<String> l3 = new ArrayList<>();

        for ( Map.Entry<String, String> e: m.entrySet()) {
            String entry = e.getKey();
            switch(entry.length()) {
                case 1:
                    l1.add(entry);
                    break;
                case 2:
                    l2.add(entry);
                    break;
                case 3:
                    l3.add(entry);
                    break;
                default:
                    break;
            }
        }

        Map<String, Map<String, List<String>>> bigm = new HashMap<>();

        for (String major: l1) {
            Map<String, List<String>> submap = new HashMap<>();
            for (String sub: l2) {
                if (sub.substring(0, 1).equals(major)) {
                    List<String> minorl = new ArrayList<>();
                    for (String minor: l3) {
                        String id2 = minor.substring(0, 2);
                        if (id2.equals(sub)) {
                            minorl.add(minor);
                        }
                    }
                    submap.put(sub,minorl );
                }
            }
            bigm.put(major, submap);
        }


        JSONObject root = new JSONObject();
        root.put("parent", "null");
        root.put("name", "esco");

        JSONArray biglist = new JSONArray();

        for (int i=0; i <10; i++) {
            String major = i+"";
            String l1id = major + " " + m.get(major);

            JSONObject o1 = new JSONObject();
            o1.put("parent", "null");
            o1.put("name", l1id);

            JSONArray children = new JSONArray();
            for (String sub: bigm.get(major).keySet()) {

                JSONObject o2 = new JSONObject();
                o2.put("parent", l1id);
                String l2id = sub + " " + m.get(sub);
                o2.put("name", l2id);

                JSONArray minors  = new JSONArray();
                List<String> l  = bigm.get(major).get(sub);
                for (String minor: l) {

                    JSONObject o3 = new JSONObject();
                    o3.put("parent", l2id);
                    String l3id = minor + " " + m.get(minor);
                    o3.put("name", l3id);
                    minors.add(o3);
                }
                o2.put("children", minors);


                children.add(o2);
            }

            o1.put("children", children);
            biglist.add(o1);
        }


        root.put("children", biglist);

        FileWriter file = new FileWriter("src/data.json");
        JSONArray towrite = new JSONArray();
        towrite.add(root);


        file.write(towrite.toJSONString());
        file.flush();
        file.close();


    }


    static void writeFlatTable(Map<String,String> m, Set<String> todo) throws Exception {
        String path = "src/isco_08.txt";
        FileWriter writer = new FileWriter(path, true);


        for (String s: todo) {
            String[] l = getLevel(s);

            writer.write(l[0] + "\t" + m.get(l[0]) +  "\t");
            writer.write(l[1] + "\t" + m.get(l[1]) +  "\t");
            writer.write(l[2] + "\t" + m.get(l[2]));
            writer.write("\n" );
        }
        writer.close();
        System.out.println("done: " + path);

    }


    static String[] getLevel(String s) {
        String[] res = new String[3];
        for (int i=0; i<3; i++) {
            res[i] = s.substring(0, i+1);
        }

        return res;
    }



}
