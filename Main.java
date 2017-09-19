import db.Database;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {
    private static final String EXIT   = "exit";
    private static final String PROMPT = "> ";

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Database db = new Database();
        System.out.print(PROMPT);

        String query;
        while ((query = in.readLine()) != null) {
            if (EXIT.equals(query)) {
                break;
            }

            if (!query.trim().isEmpty()) {
                String result = db.transact(query);
                if (result.length() > 0) {
                    System.out.println(result);
                }
            }
            System.out.print(PROMPT);
        }
        in.close();
    }
}
