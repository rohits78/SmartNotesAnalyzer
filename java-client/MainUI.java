import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import org.json.JSONObject;

public class MainUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Smart Notes Analyzer");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextArea inputArea = new JTextArea("Write or paste your notes here...");
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(inputArea);

        JButton analyzeButton = new JButton("Analyze Notes");

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(analyzeButton, BorderLayout.CENTER);
        frame.add(resultScroll, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> {
            try {
                String inputText = inputArea.getText();
                String result = sendToPythonAPI(inputText);
                resultArea.setText(result);
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    public static String sendToPythonAPI(String inputText) throws Exception {
        URL url = new URL("http://localhost:5000/analyze");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonInput = String.format("{\"text\": \"%s\"}", inputText.replace("\"", "'").replace("\n", " "));

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }

        JSONObject json = new JSONObject(response.toString());

        return String.format(
            " Summary:\n%s\n\n🗝️ Keywords: %s\n\n Sentiment: %s\n\n Word Count: %d\n⏱ Reading Time: %s",
            json.getString("summary"),
            json.getJSONArray("keywords").toString(),
            json.getString("sentiment"),
            json.getInt("word_count"),
            json.getString("reading_time")
        );
    }
}
