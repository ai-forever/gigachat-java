package chat.giga.http.client;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder
@Accessors(fluent = true)
public class SSL {

    @Default
    boolean verifySslCerts = true;

    @Default
    String protocol = "TLS";

    String keystorePath;
    String keystorePassword;
    @Default
    String keystoreType = "JKS";

    String truststorePath;
    String truststorePassword;
    @Default
    String trustStoreType = "JKS";
}
