

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.util.*;


/**
 * Generate ONET viz data
 * Use 2 maps
 * m: major groups      [id: int -> name: String]
 * sub m: minor groups  [id: int -> name: String]
 */
public class OnetViz {

    final static String JSON_OUTPUT_PATH  = "src/out/onet_viz.json";
    final static String ONET_JOBS_LIST   =  "src/data/job_list_onet.txt";
    final static String ONET_GROUPS_LIST   = "src/data/major_minor_onet.txt";
    final static String SOC  = "src/data/soc.txt";
    final static String IS_MAJOR = "0000";

    public static void main(String[] args) throws Exception  {

        List<String> l = Readfile.readFile(ONET_GROUPS_LIST);
        Map<String, String[]> m = new HashMap<>();
        Map<String, String[]> subm = new HashMap<>();

        for (String s: l) {
            String[] ws = s.split("\t");
            String id = ws[0];
            String name = clean(ws[1]);

            String tail = id.substring(3, 7);
            String head = id.substring(0, 2);
            if (tail.equals(IS_MAJOR)) {
                m.put(head, new String[] {id, name});
            }
            else {
                String subhead = id.substring(0, 4);
                subm.put(subhead, new String[] {id, name});
            }
        }

        writeJson(m, subm);
    }


    static String name(Map<String, String[]> m, String id) {
        String[] l = m.get(id);
        return l[0] + " " + l[1];
    }


    static void writeJson( Map<String, String[]> m, Map<String, String[]> subm) throws Exception {

        //last level
        List<String> l3raw = Readfile.readFile(ONET_JOBS_LIST);
        List<String> l3 = new ArrayList<>();
        for (int i=0; i<l3raw.size(); i++) {
            l3.add(l3raw.get(i).replace("\t"," ").replace("\"",""));
        }

        List<String> soc = Readfile.readFile(SOC);
        for (int i=0; i<soc.size(); i++) {
            soc.set(i, soc.get(i).replace("\t","  ").replace("\"",""));
        }

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


        JSONObject root = new JSONObject();
        root.put("parent", "null");
        root.put("name", "ONET");

        JSONArray biglist = new JSONArray();
        Set<String> majors = bigm.keySet();
        List<String> major_l = new ArrayList<>(majors);
        Collections.sort(major_l);

        for (String major: major_l) {
            JSONObject o1 = new JSONObject();
            o1.put("parent", "ONET");
            o1.put("name", major);

            JSONArray children = new JSONArray();
            Set<String> minors_unsorted = bigm.get(major).keySet();
            List<String> minor_l = new ArrayList<>(minors_unsorted);
            Collections.sort(minor_l);

            for (String minor: minor_l) {

                JSONObject o2 = new JSONObject();
                o2.put("parent", major);
                o2.put("name", minor);

                JSONArray minors  = new JSONArray();
                List<String> l  = bigm.get(major).get(minor);
                for (String occ: l) {
                    JSONObject o3 = new JSONObject();
                    o3.put("parent", minor);
                    o3.put("name", occ);
                    JSONArray socs  = new JSONArray();

                    for (String so: soc) {
                        if (so.substring(0, 6).equals(occ.substring(0, 6))) {
                            JSONObject o4 = new JSONObject();
                            o4.put("parent", occ);
                            String soc_name = so.substring(12);
                            System.out.println(soc_name);

                            o4.put("name", soc_name);
                            socs.add(o4);
                        }
                    }
                    o3.put("children", socs);   //done with occ group
                    minors.add(o3);
                }
                o2.put("children", minors); //done with minor group
                children.add(o2);
            }

            o1.put("children", children);  //done with major group
            biglist.add(o1);
        }

        root.put("children",biglist);


        FileWriter file = new FileWriter(JSON_OUTPUT_PATH);

        file.write(root.toJSONString());
        file.flush();
        file.close();

    }

    static String clean(String s) {
        return s.replace("\"", "");
    }




}
