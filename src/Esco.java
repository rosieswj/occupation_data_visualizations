import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Generate ONET viz data
 * Use 2-level hashmap maps [id: int -> name: String]
 */
public class Esco {

    final static String JSON_OUTPUT_PATH  = "src/out/esco_viz.json";
    final static String ESCO_JOBS_LIST   =   "src/data/job_list_esco.txt";
    final static String ESCO_TITLE   = "src/data/esco_job_title.txt";

    public static void main(String[] args) throws Exception  {
        List<String> l = Readfile.readFile(ESCO_JOBS_LIST);

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

        writeJson2(m);
    }

    static String clean(String s) {
        return s.replace("\t"," ").replace("\"","");
    }


    static void writeJson2(Map<String, String> m) throws Exception {

        List<String> jobs_raw = Readfile.readFile(ESCO_TITLE);
        List<String[] > jobs = new ArrayList<>();
        for (String s: jobs_raw) {
            String[] ss = s.split("\t");
            ss[0] = ss[0].substring(0, 3);
            ss[1] = clean(ss[1]);
            jobs.add(ss);
        }

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
        System.out.println(bigm.toString());


        JSONObject root = new JSONObject();
        root.put("parent", "null");
        root.put("name", "ESCO");
        JSONArray biglist = new JSONArray();

        for (int i=0; i <10; i++) {
            String major = i+"";
            String l1id = major + " " + m.get(major);

            JSONObject o1 = new JSONObject();
            o1.put("parent", "ESCO");
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

                    JSONArray ejobs  = new JSONArray();
                    for (String[] j: jobs) {
                        if (j[0].equals(l3id.substring(0,3))) {

                            JSONObject o4 = new JSONObject();
                            o4.put("parent", l3id);
                            o4.put("name", j[1]);

                            ejobs.add(o4);
                        }
                    }
                    o3.put("children", ejobs);
//                    System.out.println(l2id + "-> " + l3id + "-> " + ejobs.toJSONString());
                    minors.add(o3);
                }

                o2.put("children", minors);
                children.add(o2);
            }
            o1.put("children", children);
            biglist.add(o1);
        }
        root.put("children",biglist);

        FileWriter file = new FileWriter(JSON_OUTPUT_PATH);
        file.write(root.toJSONString());
        file.flush();
        file.close();

    }



}
