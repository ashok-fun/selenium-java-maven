package config;

@FunctionalInterface
public interface EncryptedConfigPlaceholder {
    String decrypt(String encryptedValue);
}