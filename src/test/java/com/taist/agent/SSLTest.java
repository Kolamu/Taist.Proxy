package com.taist.agent;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

import javax.net.ssl.*;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SSLTest {
    KeyPair serverKey = null;
    public void test() {

    }
    private void clientHandshake() throws Exception {
        SSLEngine sslEngine = getEngine();
        sslEngine.setUseClientMode(false);
        SSLSession session = sslEngine.getSession();
        ByteBuffer peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        ByteBuffer peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());

        ByteBuffer myAppData = ByteBuffer.wrap("Hello, I'm server.".getBytes());
        ByteBuffer myNetData = ByteBuffer.allocate(session.getPacketBufferSize());

        sslEngine.beginHandshake();
        SSLEngineResult result;
        SSLEngineResult.HandshakeStatus hsStatus = sslEngine.getHandshakeStatus();
        SSLEngineResult.Status status;
        while(hsStatus != SSLEngineResult.HandshakeStatus.FINISHED){

            System.out.println("handshake status: " + hsStatus);
            switch (hsStatus) {
                case NEED_TASK:
                    Runnable runnable;
                    while((runnable=sslEngine.getDelegatedTask()) != null){
                        System.out.println("run task");
                        runnable.run();
                    }
                    hsStatus = sslEngine.getHandshakeStatus();
                    break;
                case NEED_UNWRAP:
                    RequestBody body = null;//receiver.receive();
                    peerNetData = peerNetData.put(body.getData());

                    peerNetData.flip();
                    //peerAppData.flip();
                    do {

                        result = sslEngine.unwrap(peerNetData, peerAppData);
                        System.out.println("Unwrapping:\n" + result);
                        if(peerAppData.limit() > 0) {
                            System.out.println("app data : \n" + new String(peerAppData.array()));
                        }
                        // During an handshake renegotiation we might need to perform
                        // several unwraps to consume the handshake data.
                    } while (result.getStatus() == SSLEngineResult.Status.OK &&
                            result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP &&
                            result.bytesProduced() == 0);

                    if (peerAppData.position() == 0 &&
                            result.getStatus() == SSLEngineResult.Status.OK &&
                            peerNetData.hasRemaining())
                    {

                        result = sslEngine.unwrap(peerNetData, peerAppData);
                        System.out.println("Unwrapping:\n" + result);

                    }

                    hsStatus = result.getHandshakeStatus();
                    status = result.getStatus();

                    assert status != status.BUFFER_OVERFLOW : "buffer not overflow." + status.toString();

                    // Prepare the buffer to be written again.
                    peerNetData.compact();
                    // And the app buffer to be read.
                    peerAppData.flip();

                    break;

                case NEED_WRAP:

                    myNetData.clear();
                    result = sslEngine.wrap(myAppData, myNetData);

                    hsStatus = result.getHandshakeStatus();
                    status = result.getStatus();

                    while (status != SSLEngineResult.Status.OK) {
                        System.out.println("wrap status: " + status);
                        switch (status) {

                            case BUFFER_OVERFLOW:
                                break;
                            case BUFFER_UNDERFLOW:
                                break;
                        }
                    }
                    myNetData.flip();
                    //receiver.execute(new ResponseBody(myNetData.array()));

                    break;

            }

            if (myAppData.limit() == peerAppData.position()) {

                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                break;
            }
        }
    }

    private SSLEngine getEngine() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(
                getKeyManagerFactory().getKeyManagers(),
                getTrustManagerFactory().getTrustManagers(),
                null);
        return context.createSSLEngine();
    }

    private KeyManagerFactory getKeyManagerFactory() throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        String password = "taist.proxy";
        kmf.init(getKeyStore("*.baidu.com", password), password.toCharArray());

        return kmf;
    }

    private TrustManagerFactory getTrustManagerFactory() throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        String password = "taist.proxy";
        tmf.init(getKeyStore("*.baidu.com", password));
        return tmf;
    }

    private KeyStore getKeyStore(String host, String password) throws Exception {
        KeyStore ks = CertUtil.loadKeyStore("c:\\taist.proxy.pfx", password);
        KeyStore rs = KeyStore.getInstance(KeyStore.getDefaultType());
        rs.load(null, null);
        X509Certificate root = (X509Certificate) ks.getCertificate("Taist Proxy Root CA");
        String issuer = CertUtil.getSubject(root);
        PrivateKey privateKey = (PrivateKey) ks.getKey("Taist Proxy Root CA", password.toCharArray());
        serverKey = CertUtil.genKeyPair();
        X509Certificate cert = CertUtil.genCert(
                issuer,
                privateKey,
                root.getNotBefore(),
                root.getNotAfter(),
                serverKey.getPublic(),
                host);
        rs.setKeyEntry(
                "entry",
                serverKey.getPrivate(),
                password.toCharArray(),
                new Certificate[] { cert });
        return rs;
    }
}
