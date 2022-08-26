package su.plasmo.logic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Limit {

    int remaining;
    int toReset;
    long checkedTime;

    public void update(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equals("X-RateLimit-Remaining")) {
                this.remaining = Integer.parseInt(header.getValue());
            }
            if (header.getName().equals("X-RateLimit-Reset-After")) {
                this.toReset = Integer.parseInt(header.getValue());
            }
        }
        this.checkedTime = System.currentTimeMillis();
    }

    public boolean remained() {
        if (remaining > 0) {
            return true;
        }
        return (int) ((System.currentTimeMillis() - checkedTime) / 1000L) > toReset;
    }

}
