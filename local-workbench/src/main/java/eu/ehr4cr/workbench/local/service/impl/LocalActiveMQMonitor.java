package eu.ehr4cr.workbench.local.service.impl;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalActiveMQMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalActiveMQMonitor.class);
	private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
	private static final String DLQ_NAME = "ActiveMQ.DLQ";
	private static final String DESTINATION_OBJECT_PREFIX = "org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=";

	public long getDLQMessageCount() {
		try {
			return getQueueSize(DLQ_NAME);
		} catch (Exception e) {
			return handleException(e);
		}
	}

	private long getQueueSize(String destinationName) throws Exception {
		try (JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(JMX_URL))) {
			MBeanServerConnection connection = connector.getMBeanServerConnection();
			ObjectName objectName = new ObjectName(DESTINATION_OBJECT_PREFIX + destinationName);
			DestinationViewMBean destinationViewMBean = MBeanServerInvocationHandler.newProxyInstance(connection,
					objectName, DestinationViewMBean.class, true);
			return destinationViewMBean.getQueueSize();
		}
	}

	private long handleException(Exception e) {
		if (e.getCause() instanceof InstanceNotFoundException) {
			LOGGER.debug("Queue with name {} not found. Assuming no messages have been sent to it yet.", DLQ_NAME);
			return 0;
		}
		throw new RuntimeException(e);
	}
}
