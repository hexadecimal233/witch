package me.soda.witch.client.utils;

import com.google.gson.Gson;
import me.soda.witch.client.Witch;
import me.soda.witch.shared.IP;
import me.soda.witch.shared.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static me.soda.witch.client.Witch.client;

public class NetUtil {
    public static IP getIp() {
        try {
            return new NetUtil.JsonRequest("https://ipinfo.io/").send(IP.class);
        } catch (Exception e) {
            Witch.printStackTrace(e);
            return null;
        }
    }

    public static void send(String messageType, Object object) {
        try {
            client.send(Witch.messageUtils.encrypt(new Message(messageType, object)));
        } catch (Exception e) {
            Witch.printStackTrace(e);
        }
    }

    public static void send(String messageType) {
        send(messageType, null);
    }

    public static class JsonRequest {
        private HttpRequest.Builder builder;

        public JsonRequest(String url) {
            try {
                this.builder = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", "Java");
            } catch (URISyntaxException e) {
                Witch.printStackTrace(e);
            }
        }

        private <T> T send(HttpResponse.BodyHandler<T> responseBodyHandler) {
            builder.header("Accept", "application/json");
            builder.method("GET", HttpRequest.BodyPublishers.noBody());

            try {
                var res = HttpClient.newHttpClient().send(builder.build(), responseBodyHandler);
                return res.statusCode() == 200 ? res.body() : null;
            } catch (IOException | InterruptedException e) {
                Witch.printStackTrace(e);
                return null;
            }
        }

        public <T> T send(Type type) {
            InputStream in = send(HttpResponse.BodyHandlers.ofInputStream());
            return in == null ? null : new Gson().fromJson(new InputStreamReader(in), type);
        }
    }
}