

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.*;



public class Onet {


    public static void main(String[] args) throws Exception  {
        String path = "src/data/parse_o.txt";
        List<String> l = Readfile.readFile(path);

        Map<String, String[]> m = new HashMap<>();
        Map<String, String[]> subm = new HashMap<>();

        for (String s: l) {
            String[] ws = s.split("\t");
            String id = ws[0];
            String name = clean(ws[1]);

            String tail = id.substring(3, 7);
            String head = id.substring(0, 2);
            if (tail.equals("0000")) {
                m.put(head, new String[] {id, name});
            }
            else {
                String subhead = id.substring(0, 4);
                subm.put(subhead, new String[] {id, name});
            }
        }


//        writeFlatTable(m, subm);
        writeJson(m, subm);
    }


    static String name(Map<String, String[]> m, String id) {
        String[] l = m.get(id);
        return l[0] + " " + l[1];
    }


    static void writeJson( Map<String, String[]> m, Map<String, String[]> subm) throws Exception {


        String bc_path = "src/data/bc.txt";
        List<String> l3raw = Readfile.readFile(bc_path);
        List<String> l3 = new ArrayList<>();
        for (int i=0; i<l3raw.size(); i++) {
            l3.add(l3raw.get(i).replace("\t"," ").replace("\"",""));
        }

        /************************************************/

        Map<String, Map<String, List<String>>> bigm = new HashMap<>();

        for (String major: m.keySet()) {

            Map<String, List<String>> l1child = new HashMap<>();

            for (String sub: subm.keySet() ) {

                if (sub.substring(0, 2).equals(major)) {

                    List<String> l2child = new ArrayList<>();
                    for (String occ: l3) {
                        String id2 = occ.substring(0, 4);
                        if (id2.equals(sub)) {
                            l2child.add(occ);
                        }
                    }
                    l1child.put(name(subm, sub), l2child );
                }
            }
            bigm.put(name(m, major), l1child);
        }


        System.out.println(bigm.toString());
        JSONObject root = new JSONObject();
        root.put("parent", "null");
        root.put("name", "onet");

        JSONArray biglist = new JSONArray();

        for (String major: bigm.keySet()) {

            JSONObject o1 = new JSONObject();
            o1.put("parent", "onet");
            o1.put("name", major);

            JSONArray children = new JSONArray();
            for (String minor: bigm.get(major).keySet()) {

                JSONObject o2 = new JSONObject();
                o2.put("parent", major);
                o2.put("name", minor);

                JSONArray minors  = new JSONArray();
                List<String> l  = bigm.get(major).get(minor);
                for (String occ: l) {

                    JSONObject o3 = new JSONObject();
                    o3.put("parent", minor);

                    o3.put("name", occ);
                    minors.add(o3);
                }
                o2.put("children", minors);

                children.add(o2);
            }

            o1.put("children", children);
            biglist.add(o1);
        }

        root.put("children",biglist);


        FileWriter file = new FileWriter("src/out/viz.json");

        file.write(root.toJSONString());
        file.flush();
        file.close();

    }

    static String clean(String s) {
        return s.replace("\"", "");
    }


    static void writeFlatTable( Map<String, String[]> m, Map<String, String[]> subm) throws Exception {
        String out_path = "src/out/onet_groups_2.txt";
        FileWriter writer = new FileWriter(out_path, true);

        String bc_path = "src/data/bc.txt";
        List<String> children = Readfile.readFile(bc_path);

        for (String cdata: children) {
            String[] c = cdata.split("\t");
            String id = c[0]; String name = clean(c[1]);

            String i1 = id.substring(0, 2);
            String i2 = id.substring(0, 4);

            String[] major = m.get(i1);
            String[] minor = subm.get(i2);

            writer.write(major[0] + "@" + major[1] + "@");
            writer.write(minor[0] + "@" + minor[1] + "@");
            writer.write(id + "@" + name + "\n");
        }

        writer.close();
        System.out.println("done: " + out_path);

    }





}
