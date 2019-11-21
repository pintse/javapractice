package idv.ryan.practice.http;


import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;

public class OkHttpConnector {
    private static OkHttpClient.Builder createHttpsClientBuilder() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();

        //Trust any certificate include the self-signed
        clientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientBuilder;
    }

    private static OkHttpClient createAuthenticatedHttpsClient(final String username,
                                                               final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = createHttpsClientBuilder().
                authenticator(new Authenticator() {
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder().header("Authorization", credential).build();
                    }
                }).build();

        return httpClient;
    }

    private static OkHttpClient createHttpsClient() {
        return createHttpsClientBuilder().build();
    }

    public static void main(String[] args) throws IOException {
        OkHttpClient httpClient = OkHttpConnector.createHttpsClient();

        Request request = new Request.Builder()
                .url("https://yourURL1")
                .build();

        Response response = httpClient.newCall(request).execute();
        System.out.println(response.body().string());

        OkHttpClient authenticatedHttpClient = OkHttpConnector.createAuthenticatedHttpsClient("your account", "your password");

        Request request2 = new Request.Builder()
                .url("https://yourURL2")
                .build();

        Response response2 = authenticatedHttpClient.newCall(request2).execute();
        System.out.println(response2.body().string());

        Request request3 = new Request.Builder()
                .url("https://yourURL3")
                .build();

        Response response3 = authenticatedHttpClient.newCall(request3).execute();
        System.out.println(response3.body().string());

    }
}
