package com.custodix.insite.mongodb.export.patient.infrastructure.mongo;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.util.SocketUtils;

import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class EmbeddedMongoConfig {
	private static final String IP = "localhost";

	@Bean
	IMongodConfig mongodConfig() throws IOException {
		return new MongodConfigBuilder().version(Version.Main.PRODUCTION)
				.net(new Net(IP, SocketUtils.findAvailableTcpPort(), Network.localhostIsIPv6()))
				.build();
	}
}
