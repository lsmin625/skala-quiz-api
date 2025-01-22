package com.sk.skala.quizapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

//@Configuration
public class SftpConfig {

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port}")
	private int sftpPort;

	@Value("${sftp.username}")
	private String sftpUsername;

	@Value("${sftp.password}")
	private String sftpPassword;

	@Value("${sftp.remote.directory}")
	private String remoteDirectory;

	@Bean
	DefaultSftpSessionFactory sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
		factory.setHost(sftpHost);
		factory.setPort(sftpPort);
		factory.setUser(sftpUsername);
		factory.setPassword(sftpPassword);
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@Bean
	SftpInboundFileSynchronizer fileSynchronizer() {
		SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
		synchronizer.setRemoteDirectory(remoteDirectory);
		synchronizer.setDeleteRemoteFiles(false);
		return synchronizer;
	}
}
