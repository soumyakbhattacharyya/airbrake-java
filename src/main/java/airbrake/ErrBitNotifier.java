package airbrake;

import airbrake.AirbrakeNotice;
import airbrake.NoticeXml;

import java.io.*;
import java.net.*;

public class ErrBitNotifier {

    private final String baseUrl;

    public ErrBitNotifier(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private void addingProperties(final HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-type", "text/xml");
        connection.setRequestProperty("Accept", "text/xml, application/xml");
        connection.setRequestMethod("POST");
    }

    private HttpURLConnection createConnection() throws IOException {
        return (HttpURLConnection) new URL(String.format("http://%s/notifier_api/v2/notices", baseUrl)).openConnection();
    }

    private void err(final AirbrakeNotice notice, final Exception e) {
        e.printStackTrace();
    }

    public int notify(final AirbrakeNotice notice) {
        try {
            final HttpURLConnection toairbrake = createConnection();
            addingProperties(toairbrake);
            String toPost = new NoticeXml(notice).toString();
            return send(toPost, toairbrake);
        } catch (final Exception e) {
            err(notice, e);
        }
        return 0;
    }

    private int send(final String yaml, final HttpURLConnection connection) throws IOException {
        int statusCode;
        final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(yaml);
        writer.close();
        statusCode = connection.getResponseCode();
        return statusCode;
    }

}