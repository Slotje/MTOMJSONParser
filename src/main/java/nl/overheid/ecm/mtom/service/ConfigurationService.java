package nl.overheid.ecm.mtom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import nl.overheid.ecm.mtom.exception.ConfigurationException;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import nl.overheid.ecm.mtom.model.ClientConfiguration;
import nl.overheid.ecm.mtom.validator.JsonSchemaValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing client configurations
 */
@ApplicationScoped
public class ConfigurationService {
    private static final Logger LOG = Logger.getLogger(ConfigurationService.class);

    @ConfigProperty(name = "client.config.storage.path", defaultValue = "/opt/app/config/clients")
    String configStoragePath;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    JsonSchemaValidator schemaValidator;

    private final Map<String, ClientConfiguration> configCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOG.infof("Configuration service initialized with storage path: %s", configStoragePath);

        // Create storage directory if it doesn't exist
        File storageDir = new File(configStoragePath);
        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (created) {
                LOG.infof("Created configuration storage directory: %s", configStoragePath);
            }
        }

        // Pre-load all configurations
        loadAllConfigurations();
    }

    /**
     * Get client configuration by client ID
     */
    public ClientConfiguration getConfiguration(String clientId) {
        // Check cache first
        ClientConfiguration config = configCache.get(clientId);
        if (config != null) {
            LOG.debugf("Configuration for client %s found in cache", clientId);
            return config;
        }

        // Load from filesystem
        config = loadConfiguration(clientId);
        if (config != null) {
            configCache.put(clientId, config);
            return config;
        }

        // Not found
        LOG.errorf("No configuration found for client: %s", clientId);
        throw new ConfigurationException(
            ErrorCode.CONFIG_001,
            String.format("No configuration found for client: %s", clientId)
        );
    }

    /**
     * Save or update client configuration
     */
    public void saveConfiguration(ClientConfiguration config) {
        try {
            // Validate configuration
            schemaValidator.validateClientConfiguration(config);

            // Write to filesystem
            Path configPath = getConfigPath(config.getClientId());
            String json = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(config);
            Files.writeString(configPath, json);

            // Update cache
            configCache.put(config.getClientId(), config);

            LOG.infof("Saved configuration for client: %s", config.getClientId());

        } catch (IOException e) {
            LOG.errorf("Failed to save configuration for client %s: %s",
                config.getClientId(), e.getMessage());
            throw new ConfigurationException(
                ErrorCode.CONFIG_003,
                String.format("Failed to save configuration for client %s", config.getClientId()),
                e
            );
        }
    }

    /**
     * Delete client configuration
     */
    public void deleteConfiguration(String clientId) {
        try {
            Path configPath = getConfigPath(clientId);
            Files.deleteIfExists(configPath);
            configCache.remove(clientId);

            LOG.infof("Deleted configuration for client: %s", clientId);

        } catch (IOException e) {
            LOG.errorf("Failed to delete configuration for client %s: %s",
                clientId, e.getMessage());
            throw new ConfigurationException(
                ErrorCode.CONFIG_003,
                String.format("Failed to delete configuration for client %s", clientId),
                e
            );
        }
    }

    /**
     * Reload configuration from filesystem
     */
    public void reloadConfiguration(String clientId) {
        ClientConfiguration config = loadConfiguration(clientId);
        if (config != null) {
            configCache.put(clientId, config);
            LOG.infof("Reloaded configuration for client: %s", clientId);
        } else {
            configCache.remove(clientId);
            LOG.warnf("Configuration not found during reload for client: %s", clientId);
        }
    }

    /**
     * Reload all configurations
     */
    public void reloadAllConfigurations() {
        configCache.clear();
        loadAllConfigurations();
        LOG.info("Reloaded all configurations");
    }

    private ClientConfiguration loadConfiguration(String clientId) {
        try {
            Path configPath = getConfigPath(clientId);
            if (!Files.exists(configPath)) {
                return null;
            }

            String json = Files.readString(configPath);
            ClientConfiguration config = objectMapper.readValue(json, ClientConfiguration.class);

            // Validate configuration
            schemaValidator.validateClientConfiguration(config);

            LOG.debugf("Loaded configuration for client: %s", clientId);
            return config;

        } catch (IOException e) {
            LOG.errorf("Failed to load configuration for client %s: %s",
                clientId, e.getMessage());
            throw new ConfigurationException(
                ErrorCode.CONFIG_003,
                String.format("Failed to load configuration for client %s", clientId),
                e
            );
        }
    }

    private void loadAllConfigurations() {
        try {
            File storageDir = new File(configStoragePath);
            File[] configFiles = storageDir.listFiles((dir, name) -> name.endsWith(".json"));

            if (configFiles != null) {
                for (File file : configFiles) {
                    try {
                        String json = Files.readString(file.toPath());
                        ClientConfiguration config = objectMapper.readValue(
                            json, ClientConfiguration.class);

                        schemaValidator.validateClientConfiguration(config);
                        configCache.put(config.getClientId(), config);

                        LOG.debugf("Loaded configuration: %s", config.getClientId());
                    } catch (Exception e) {
                        LOG.errorf("Failed to load configuration from %s: %s",
                            file.getName(), e.getMessage());
                    }
                }

                LOG.infof("Loaded %d client configurations", configCache.size());
            }
        } catch (Exception e) {
            LOG.error("Failed to load configurations", e);
        }
    }

    private Path getConfigPath(String clientId) {
        return Paths.get(configStoragePath, clientId + ".json");
    }

    public Map<String, ClientConfiguration> getAllConfigurations() {
        return Map.copyOf(configCache);
    }
}
