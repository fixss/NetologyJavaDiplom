import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    String word = in.readLine().toLowerCase();
                    List<PageEntry> pageEntryList = engine.search(word);
                    convertJson(pageEntryList);
                    out.println(convertJson(pageEntryList));
                    System.out.println(convertJson(pageEntryList));
                }
            }
        } catch (IOException e) {
            System.out.println("Error. Can't start server.");
            e.printStackTrace();
        }
    }

    public static String convertJson(List<PageEntry> pageList) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setPrettyPrinting()
                .create();
        return gson.toJson(pageList);
    }
}