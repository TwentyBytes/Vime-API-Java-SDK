package su.plasmo.logic;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import su.plasmo.throwables.APICallException;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HTTPRequester {

    String token;
    CloseableHttpClient client = HttpClients.createDefault();
    Limit limit = new Limit();
    String url = "https://api.vimeworld.ru/";

    public HTTPRequester(String token) {
        this.token = token;
    }

    public String POST(HttpPost request) {
        return response(request);
    }

    public String GET(String request) {
        return response(new HttpGet(url + request + (this.token == null ? "" : "?token=" + this.token)));
    }

    @SneakyThrows
    private String response(HttpRequestBase request) {
        if (!limit.remained()) {
            throw new APICallException(2, "API rate limit exceeded for your IP (token rate).");
        }

        CloseableHttpResponse response = this.client.execute(request);
        String answer = EntityUtils.toString(response.getEntity());

        limit.update(response);

        if (answer == null) {
            throw new IllegalStateException("Answer is null.");
        }

        if (answer.contains("error")) {
            JSONTokener tokener = new JSONTokener(answer);
            JSONObject object = new JSONObject(tokener);
            JSONObject error = object.optJSONObject("error");
            throw new APICallException(error.optInt("error_code"), error.optString("error_msg"));
        }

        return answer;
    }
}
