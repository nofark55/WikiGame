import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;

public class WikiGame extends JFrame {
    private JTextField startField, goalField;
    private DefaultListModel<String> pathModel;
    private JTextArea statusArea;
    private JButton startButton;

    public WikiGame() {
        setTitle("Wiki Path Finder");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Start Page (e.g. Milton):"));
        startField = new JTextField();
        inputPanel.add(startField);
        inputPanel.add(new JLabel("Goal Page (e.g. Computer):"));
        goalField = new JTextField();
        inputPanel.add(goalField);
        startButton = new JButton("Find Path");
        inputPanel.add(startButton);

        // Results Panel
        pathModel = new DefaultListModel<>();
        JList<String> pathList = new JList<>(pathModel);
        statusArea = new JTextArea();

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(pathList), BorderLayout.CENTER);
        add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        startButton.addActionListener(e -> runSearch());
    }

    private void runSearch() {
        String start = startField.getText().trim();
        String goal = goalField.getText().trim();
        pathModel.clear();
        statusArea.setText("Searchi");

        new SwingWorker<List<String>, String>() {

            public List<String> doInBackground() {
                WikiSolver solver = new WikiSolver();
                return solver.findPath("https://en.wikipedia.org/wiki/" + start,
                        "https://en.wikipedia.org/wiki/" + goal);
            }

                public void done() {
                try {
                    List<String> result = get();
                    if (result != null) {
                        for (String s : result) pathModel.addElement(s);
                        statusArea.setText("Path found!");
                    } else {
                        statusArea.setText("No path found within depth.");
                    }
                } catch (Exception e) {
                    statusArea.setText("Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WikiGame().setVisible(true));
    }
}

// Logic Class
class WikiSolver {
    private int maxDepth = 3;

    public List<String> findPath(String startUrl, String targetUrl) {
        // uses arraylist similar to my DFS searcher, expect this is guarenteed to find the lowest count
        ArrayList<String> queue = new ArrayList<>();
        Map<String, String> parentMap = new HashMap<>();
        Map<String, Integer> depthMap = new HashMap<>();

        queue.add(startUrl);
        parentMap.put(startUrl, null);
        depthMap.put(startUrl, 0);

        while (!queue.isEmpty()) {
            String current = queue.remove(0);
            int currentDepth = depthMap.get(current);

            System.out.println("Searching (" + currentDepth + "): " + current);

            if (current.equals(targetUrl)) {
                return reconstructPath(parentMap, targetUrl);
            }

            if (currentDepth < maxDepth) {
                ArrayList<String> neighbors = getLinks(current);
                for (String next : neighbors) {
                    if (!parentMap.containsKey(next)) {
                        parentMap.put(next, current);
                        depthMap.put(next, currentDepth + 1);
                        queue.add(next);
                    }
                }
            }
        }
        return null;
    }

    private ArrayList<String> getLinks(String urlString) {
        ArrayList<String> links = new ArrayList<>();
        try {
            //largely gathered from my previous project
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) html.append(line);

            Pattern p = Pattern.compile("href=\"(/wiki/[^\":#]+)\"");
            Matcher m = p.matcher(html.toString());
            while (m.find()) {
                links.add("https://en.wikipedia.org" + m.group(1));
            }
        } catch (Exception _){

        }
        return links;
    }

    private ArrayList<String> reconstructPath(Map<String, String> parentMap, String target) {
        ArrayList<String> path = new ArrayList<>();
        String curr = target;
        while (curr != null) {
            path.add(0, curr);
            curr = parentMap.get(curr);
        }
        return path;
    }
}