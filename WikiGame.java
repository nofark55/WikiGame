import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiGame {

    private int maxDepth;
    private java.util.ArrayList<String> path = new java.util.ArrayList<>();
    private HashSet<String> visited = new HashSet<>();

    public static void main(String[] args) {
        new WikiGame();
    }

    public WikiGame() {
        String startLink = "https://en.wikipedia.org/wiki/Milton_Academy";
        String endLink = "https://en.wikipedia.org/wiki/Car";
        //remember compouding, you can be searching a TON of pages at a depth of 4.
        maxDepth = 5;

        // Add the starting link to the path manually if found
        if (findLink(startLink, endLink, 0)) {
            System.out.println("found");
            System.out.println(" -> " + startLink); // Prints the starting point
            for (String p : path) System.out.println(" -> " + p);
        } else {
            System.out.println("Did not find it within depth " + maxDepth);
        }
    }

    public boolean findLink(String currentLink, String targetLink, int depth) {
        if (currentLink.equals(targetLink)) return true;

        if (depth >= maxDepth || visited.contains(currentLink)) return false;

        visited.add(currentLink);
        System.out.println("Depth " + depth + ": Searching " + currentLink);

        try {
            URL url = new URL(currentLink);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder htmlBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                htmlBuilder.append(line);
            }
            br.close();
            String html = htmlBuilder.toString();

            Pattern p = Pattern.compile("href=\"(/wiki/[^\":#]+)\"");
            Matcher m = p.matcher(html);

            while (m.find()) {
                String subPath = m.group(1);

                if (subPath.equalsIgnoreCase("/wiki/Main_Page") || subPath.equalsIgnoreCase("/wiki/Geographic_coordinate_system")) {
                    continue;
                }

                String nextLink = "https://en.wikipedia.org" + subPath;

                if (findLink(nextLink, targetLink, depth + 1)) {
                    path.add(0, nextLink); // Standard Java way to insert at the front
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("skipped");
        }

        return false;
    }
}