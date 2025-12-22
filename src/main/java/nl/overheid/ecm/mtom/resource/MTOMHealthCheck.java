package nl.overheid.ecm.mtom.resource;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Health check for MTOM parser application
 */
@Liveness
@ApplicationScoped
public class MTOMHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("MTOM Parser")
            .up()
            .withData("version", "1.0.0")
            .build();
    }
}
