import java.util.ArrayList;

public class WikiGame {

    private int maxDepth;
    private ArrayList<String> path = new ArrayList<>();

    public static void main(String[] args) {
        WikiGame w = new WikiGame();
    }
//f
    public WikiGame() {
        String startLink = "https://en.wikipedia.org/wiki/MV_Hondius_hantavirus_outbreak";  // beginning link, where the program will start
        String endLink = "https://en.wikipedia.org/wiki/Epidemiology";    // ending link, where the program is trying to get to
        maxDepth = 1;           // start this at 1 or 2, and if you get it going fast, increase

        if (findLink(startLink, endLink, 0)) {
            System.out.println("found it********************************************************************");
            path.add(startLink);
        } else {
            System.out.println("did not find it********************************************************************");
        }

    }

    // recursion method
    public boolean findLink(String startLink, String endLink, int depth) {

        System.out.println("depth is: " + depth + ", link is: https://en.wikipedia.org" + startLink);

        // BASE CASE
        if (startLink.equals(endLink)) {
            return true;
        } else if (!startLink.equals(endLink)) {

        }

        // GENERAL RECURSIVE CASE
        else {

        }

        return false;
    }

}
