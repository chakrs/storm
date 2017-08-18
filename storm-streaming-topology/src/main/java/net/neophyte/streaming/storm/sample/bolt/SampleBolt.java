package net.neophyte.streaming.storm.sample.bolt;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import net.neophyte.streaming.storm.sample.config.JMSConfiguration;
import net.neophyte.streaming.storm.sample.jmsconnector.SimpleConnectionProvider;

/**
 * 
 * @author shuvro
 *
 */
public class SampleBolt extends BaseRichBolt {

	private static final long serialVersionUID = 2599690761641182867L;
	
	private OutputCollector collector;
	private long msgCount = 0;
	private Session session = null;
	private MessageProducer destinationMsgProducer = null;
	private MessageProducer errorMsgProducer = null;
	private String destinationQueueName = null;
	private String errorQueueName = null;
	JMSConfiguration jmsConfiguration = null;

	public SampleBolt(JMSConfiguration jmsConfiguration, String destinationQueueName, String errorQueueName) {
		if (jmsConfiguration == null || destinationQueueName == null || destinationQueueName.isEmpty()
				|| errorQueueName == null || errorQueueName.isEmpty()) {
			throw new IllegalArgumentException("The Properties object or the destination/error queue name is invalid!");
		}
		this.jmsConfiguration = jmsConfiguration;
		this.destinationQueueName = destinationQueueName;
		this.errorQueueName = errorQueueName;
	}

	@Override
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		Connection connection;
		try {
			connection = SimpleConnectionProvider.createConnectionInstance(jmsConfiguration.getBrokerUrl(),
					jmsConfiguration.getUserId(), jmsConfiguration.getPassword());
			connection.start();
			this.session = SimpleConnectionProvider.getSession(Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			System.out.println("Exception in prepare()");
			e.printStackTrace();
		}
	}

	@Override
	public void execute(Tuple tuple) {
		String message = tuple.getStringByField("message");
		if (message == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}

		msgCount++;

		try {
			System.out.println("Bolt Received Message #:" + msgCount);

			if (this.destinationMsgProducer == null) {
				this.destinationMsgProducer = createMsgProducer(destinationQueueName);
			}
			/* forwards the message to the destination queue */
			if (destinationMsgProducer != null) {
				sendMessage(destinationMsgProducer, message);
				System.out.println("Bolt Sent Message # " + msgCount + " to destination queue");
				this.collector.ack(tuple);
			}
		} catch (Exception e) {
			System.out
					.println("Exception occured in SampleBolt - posting the message to error queue: " + errorQueueName);
			if (this.errorMsgProducer == null) {
				this.errorMsgProducer = createMsgProducer(errorQueueName);
			}
			sendMessage(errorMsgProducer, message);
			System.out.println("Bolt Sent Message # " + msgCount + " to error queue!");
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("message"));
	}

	private MessageProducer createMsgProducer(String queueName) {
		MessageProducer msgProducer = null;
		try {
			if (session != null) {
				synchronized (session) {
					Destination destination = session.createQueue(queueName);
					msgProducer = session.createProducer(destination);
					msgProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
				}
			}
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
		return msgProducer;
	}

	private synchronized void sendMessage(MessageProducer producer, String message) {
		try {
			TextMessage jmsMessage = (TextMessage) session.createTextMessage(message);
			producer.send(jmsMessage);
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
	}

	@Override
	public void cleanup() {

	}
}