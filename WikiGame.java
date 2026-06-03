import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.io.*;
import java.net.*;
import java.util.regex.*;

public class WikiGame extends JFrame {
    private JTextField startField, goalField;
    private JSpinner depthSpinner;
    private DefaultListModel<String> pathModel;
    private JList<String> pathList;
    private JLabel statusLabel;
    private JLabel currentLinkDisplay;
    private JButton startButton;

    public WikiGame() {
        setTitle("Wiki Path Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel, this is mainly show the spinner, the path and all of the inputs for the start
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.add(new JLabel("Start Page:"));
        startField = new JTextField("Milton");
        inputPanel.add(startField);
        inputPanel.add(new JLabel("Goal Page:"));
        goalField = new JTextField("Computer");
        inputPanel.add(goalField);
        inputPanel.add(new JLabel("Max Depth:"));
        depthSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 5, 1));
        inputPanel.add(depthSpinner);
        startButton = new JButton("Find Path");
        inputPanel.add(startButton);

        // this is the center, I have the serarching side and the side that shows the path, so when the path is done, one side will show the path and while searching the other side will show the current searching link
        pathModel = new DefaultListModel<>();
        pathList = new JList<>(pathModel);
        currentLinkDisplay = new JLabel("<html>Searching: None</html>", SwingConstants.CENTER);
        //split pane between both
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(currentLinkDisplay), new JScrollPane(pathList));
        splitPane.setDividerLocation(400);
        //satus label at the botom
        statusLabel = new JLabel("Status: Idle", SwingConstants.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> runSearch());
    }

    //this takes the field infos and gets the links that are needed.
    //I mainly used .trim as a safety measure and the spinner is the thing with the depth where you can click up adn down
    //in total this just gathers the input data
    private void runSearch() {
        String start = startField.getText().trim();
        String goal = goalField.getText().trim();
        int depth = (int) depthSpinner.getValue();
        pathModel.clear();
        statusLabel.setText("Searching...");

        new SwingWorker<List<String>, String>() {
            //this call the solver to find the path from the start, and it adds the wikipedia thing so the user inputs the topic, not the article url
            public List<String> doInBackground() {
                WikiSolver solver = new WikiSolver(depth);
                return solver.findPath("https://en.wikipedia.org/wiki/" + start,
                        "https://en.wikipedia.org/wiki/" + goal,
                        (link) -> publish(link));
            }

            //this is merely to show the actual
            public void process(List<String> chunks) {
                currentLinkDisplay.setText("<html>Searching:<br>" + chunks.get(chunks.size() - 1) + "</html>");
            }

            public void done() {
                try {
                    List<String> result = get();
                    if (result != null) {
                        for (String s : result) pathModel.addElement(s);
                        statusLabel.setText("Path found!");
                    } else {
                        statusLabel.setText("No path found within depth.");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
            //looked this up, this should make it entire thing responsive and not freeze while doing this
        }.execute();
    }

    public static void main(String[] args) {
        // This creates the window and makes it visible immediately.
        new WikiGame().setVisible(true);
    }
}

class WikiSolver {
    private int maxDepth;
    //map, key is the link I just found adn the value is the one linking to it, so child parent
    private Map<String, String> parentMap = new HashMap<>();

    public WikiSolver(int maxDepth) { this.maxDepth = maxDepth; }

    public List<String> findPath(String startUrl, String targetUrl, Consumer<String> callback) {
        parentMap.put(startUrl, null);
        ArrayList<String> currentLayer = new ArrayList<>();
        currentLayer.add(startUrl);
        return recursiveSearch(currentLayer, targetUrl, 0, callback);
    }

    private List<String> recursiveSearch(ArrayList<String> layer, String target, int depth, Consumer<String> callback) {
        //base case
        if (layer.isEmpty() || depth > maxDepth) return null;
        //loop through layer
        ArrayList<String> nextLayer = new ArrayList<>();

        for (String current : layer) {
            callback.accept(current); // Show user what we are searching

            if (current.equals(target)) return reconstructPath(target);

            // Explore neighbors
            for (String next : getLinks(current)) {
                if (!parentMap.containsKey(next)) {//
                    parentMap.put(next, current);//save path
                    nextLayer.add(next);//queue for next one
                }
            }
        }
        // Recursive step: process the next "ring" of links
        return recursiveSearch(nextLayer, target, depth + 1, callback);
    }

    private ArrayList<String> getLinks(String urlString) {
        //this was taken from my previous one where I did the same getlinks thing.
        ArrayList<String> links = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) html.append(line);
            //this is the only difference, I had to extract the href from every link
            Pattern p = Pattern.compile("href=\"(/wiki/[^\":#]+)\"");
            Matcher m = p.matcher(html.toString());
            while (m.find()) {
                String sub = m.group(1);
                if (sub.contains(":") || sub.contains("Main_Page") || sub.contains("List")) continue;
                links.add("https://en.wikipedia.org" + sub);
            }
        } catch (Exception e){ }
        return links;
    }
    //reconstruct it to find the back "bredcrumb trail"
    private ArrayList<String> reconstructPath(String target) {
        //I looked some stuff up on stack overflow but I did this seperatly
        ArrayList<String> path = new ArrayList<>();
        String curr = target;
        //Keep looking up the parent of the current page until we reach the start
        while (curr != null) {
            path.add(0, curr);
            curr = parentMap.get(curr);
        }
        return path;
    }
}