package chat.giga.http.client;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.platform.commons.util.Preconditions;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

class SSLTestUtils {

    static void createKeyStore(
            File destination,
            String type,
            String DN,
            String alias,
            String keyStorePassword,
            String keyPassword
    ) {
        Preconditions.notNull(destination, "destination should not to be null");
        Preconditions.notBlank(type, "type should not to be blank");
        Preconditions.notBlank(DN, "DN should not to be blank");
        Preconditions.notBlank(alias, "alias should not to be blank");
        Preconditions.notBlank(keyStorePassword, "keyStorePassword should not to be blank");
        Preconditions.notBlank(keyPassword, "keyPassword should not to be blank");
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            var keyPair = keyPair("RSA", 512);
            var cert = createCertificate(keyPair, DN);
            var keyStore = KeyStore.getInstance(type);
            keyStore.load(null, keyStorePassword.toCharArray());
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPassword.toCharArray(), new Certificate[] { cert });
            try (FileOutputStream output = new FileOutputStream(destination)) {
                keyStore.store(output, keyStorePassword.toCharArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }
    }

    private static X509Certificate createCertificate(
            KeyPair keyPair,
            String DN
    ) throws OperatorCreationException, CertificateException {
        var subject = new X500Name(DN);
        var now = System.currentTimeMillis();
        var notBefore = new Date(now);
        var notAfter = new Date(now + 365 * 86400000L);
        var builder = new JcaX509v3CertificateBuilder(
                subject,
                BigInteger.valueOf(now),
                notBefore,
                notAfter,
                subject,
                keyPair.getPublic()
        );
        var signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(keyPair.getPrivate());
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(builder.build(signer));
    }

    private static KeyPair keyPair(String algorithm, int keysize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keysize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
