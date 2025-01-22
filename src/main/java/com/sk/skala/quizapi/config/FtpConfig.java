package com.sk.skala.quizapi.config;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

//@Configuration
public class FtpConfig {

	@Value("${ftp.host}")
	private String ftpHost;

	@Value("${ftp.port}")
	private int ftpPort;

	@Value("${ftp.username}")
	private String ftpUsername;

	@Value("${ftp.password}")
	private String ftpPassword;

	@Value("${ftp.remote.directory}")
	private String remoteDirectory;

	@Bean
	DefaultFtpSessionFactory ftpSessionFactory() {
		DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
		factory.setHost(ftpHost);
		factory.setPort(ftpPort);
		factory.setUsername(ftpUsername);
		factory.setPassword(ftpPassword);
		factory.setClientMode(FTPClient.ACTIVE_REMOTE_DATA_CONNECTION_MODE); // You can switch to passive mode if needed
		return factory;
	}

	@Bean
	FtpInboundFileSynchronizer ftpFileSynchronizer() {
		FtpInboundFileSynchronizer synchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
		synchronizer.setRemoteDirectory(remoteDirectory);
		synchronizer.setDeleteRemoteFiles(false); // Optional: Set to true if you want to delete files after downloading
		return synchronizer;
	}
}
