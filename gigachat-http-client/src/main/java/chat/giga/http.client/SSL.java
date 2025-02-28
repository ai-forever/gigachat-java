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
    private boolean verifySslCerts = true;

    @Default
    private String protocol = "TLS";

    private String keystorePath;
    private String keystorePassword;
    @Default
    private String keystoreType = "JKS";

    private String truststorePath;
    private String truststorePassword;
    @Default
    private String trustStoreType = "JKS";
}
