package com.insights;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoginService {
    public static String login() throws Exception {
        String authCode = null;
        try {
            authCode = getAuthCode();
        } catch (Exception e) {
            System.out.println("Failed to get the auth code");
            System.out.println(e.toString());
            return authCode;
        }

        // Exchange the auth code for a jwt from the server
        String jsonBody = "{\"authCode\" : \"" + authCode + "\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(App.applicationProperties.get("api.endpoint") + "/public/auth"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        String jwt = client.send(request, BodyHandlers.ofString()).body();
        return jwt;
    }

    private static String waitForAuthCode() throws IOException {
        ServerSocket server = new ServerSocket(3000);
        Socket client = server.accept();

        Scanner scanner = new Scanner(client.getInputStream(), StandardCharsets.UTF_8);
        String authCode = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.contains("code=")) {
                authCode = line.split("code=")[1].split("&")[0];
                break;
            }
        }
        String responseBody = "Login successful please return to the console application :)";
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseBody;

        OutputStream outputStream = client.getOutputStream();
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
        scanner.close();
        return authCode;
    }

    private static String getAuthCode() throws Exception {
        String authUrl = "https://accounts.google.com/o/oauth2/auth"
                + "?client_id=" + System.getenv("OAUTH_CLIENT_ID")
                + "&redirect_uri=" + App.applicationProperties.getProperty("google.redirecturi")
                + "&response_type=code"
                + "&scope=openid%20phone%20email%20profile";

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(authUrl));
        } else {
            System.out.println("Please open this URL manually: " + authUrl);
            System.out.println("Trying to connect to the server");
        }
        
        String authCode = waitForAuthCode();
        return authCode;
    }
}
