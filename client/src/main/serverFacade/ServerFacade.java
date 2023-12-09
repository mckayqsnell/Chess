package serverFacade;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import webSocket.WSClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is the HTTP client that makes HTTP request to the server
 */
public class ServerFacade {

    private static HttpURLConnection sendRequest(String urlString, String method, String body, String authToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod(method);
        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }
        if (body != null) {
            writeRequestBody(body, connection);
        }
        connection.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return connection;
    }

    private static void writeRequestBody(String body, HttpURLConnection connection) throws IOException {
        if (!body.isEmpty()) {
            connection.setDoOutput(true);
            try (var outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static <T> T readResponseBody(HttpURLConnection connection, Class<T> responseType) throws IOException {
        T responseBody = null;
        try (InputStream respBody = connection.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, responseType);
        }
        return responseBody;
    }

    /**
     * POST request to the server to register a user
     *
     * @param urlString the url of the server(default is localhost:8080)
     * @param username  of the user
     * @param password  of the user
     * @param email     of the user
     * @return RegisterResponse that indicates if it was successful or not
     */
    public static RegisterResponse registerRequest(String urlString, String username, String password, String email)
            throws IOException {
        urlString += "/user";
        //Prepare request body
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String requestBody = new Gson().toJson(registerRequest);
        //Send the request
        HttpURLConnection connection = sendRequest(urlString, "POST", requestBody, null);
        //Check the response
        RegisterResponse registerResponse = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            registerResponse = readResponseBody(connection, RegisterResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return registerResponse; //This will return null if there is an error. Which we could work with? kinda.
    }

    public static LoginResponse loginRequest(String urlString, String username, String password) throws IOException {
        urlString += "/session";

        LoginRequest loginRequest = new LoginRequest(username, password);
        String requestBody = new Gson().toJson(loginRequest);

        HttpURLConnection connection = sendRequest(urlString, "GET", requestBody, null);

        LoginResponse loginResponse = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            loginResponse = readResponseBody(connection, LoginResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return loginResponse;
    }

    public static LogoutResponse logoutRequest(String urlString, String authToken) throws IOException {
        urlString += "/session";

        HttpURLConnection connection = sendRequest(urlString, "DELETE", null, authToken);

        LogoutResponse logoutResponse = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            logoutResponse = readResponseBody(connection, LogoutResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return logoutResponse;
    }

    public static CreateGameResponse createGameRequest(String urlString, String authToken, String gameName) throws IOException {
        urlString += "/game";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        String requestBody = new Gson().toJson(createGameRequest);

        HttpURLConnection connection = sendRequest(urlString, "POST", requestBody, authToken);

        CreateGameResponse createGameResponse = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            createGameResponse = readResponseBody(connection, CreateGameResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return createGameResponse;
    }

    public static ListGamesResponse listGamesRequest(String urlString, String authToken) throws IOException {
        urlString += "/game";

        HttpURLConnection connection = sendRequest(urlString, "GET", null, authToken);

        ListGamesResponse listGamesResponse = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            listGamesResponse = readResponseBody(connection, ListGamesResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return listGamesResponse;
    }

    public static JoinGameResponse joinGameRequest(String urlString, String authToken, Integer gameID, String playerColor)
            throws IOException {
        urlString += "/game";

        JoinGameRequest joinGameRequest = new JoinGameRequest(gameID, playerColor);
        String requestBody = new Gson().toJson(joinGameRequest);

        HttpURLConnection connection = sendRequest(urlString, "PUT", requestBody, authToken);

        JoinGameResponse response = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            response = readResponseBody(connection, JoinGameResponse.class);
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }

        return response;
    }

    public boolean openWebSocket(String urlString, String authToken) throws Exception {
        var ws = new WSClient(urlString);

        return false;
    }

    /**
     * This method is only for testing convenience
     *
     * @param urlString url to the server
     * @throws IOException for errors
     */
    public static void clear(String urlString) throws IOException {
        urlString += "/db";

        HttpURLConnection connection = sendRequest(urlString, "DELETE", null, null);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("SEVER RETURNED AN HTTP OK: " + connection.getResponseCode());
        } else {
            System.out.println("SEVER RETURNED AN HTTP ERROR: " + connection.getResponseCode());
        }
    }
}
