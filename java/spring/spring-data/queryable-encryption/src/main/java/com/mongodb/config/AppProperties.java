package com.mongodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mongodb")
public class AppProperties {
	protected String uri;
	protected String cryptSharedLibPath;
	protected String keyVaultNamespace;
	protected String encryptedDatabaseName;
	protected String encryptedCollectionName;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getCryptSharedLibPath() {
		return cryptSharedLibPath;
	}

	public void setCryptSharedLibPath(String cryptSharedLibPath) {
		this.cryptSharedLibPath = cryptSharedLibPath;
	}

	public String getKeyVaultNamespace() {
		return keyVaultNamespace;
	}

	public void setKeyVaultNamespace(String keyVaultNamespace) {
		this.keyVaultNamespace = keyVaultNamespace;
	}

	public String getEncryptedDatabaseName() {
		return encryptedDatabaseName;
	}

	public void setEncryptedDatabaseName(String encryptedDatabaseName) {
		this.encryptedDatabaseName = encryptedDatabaseName;
	}

	public String getEncryptedCollectionName() {
		return encryptedCollectionName;
	}

	public void setEncryptedCollectionName(String encryptedCollectionName) {
		this.encryptedCollectionName = encryptedCollectionName;
	}
}