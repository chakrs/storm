package net.neophyte.streaming.storm.sample.spout;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import net.neophyte.streaming.storm.sample.config.JMSConfiguration;
import net.neophyte.streaming.storm.sample.jmsconnector.SimpleConnectionProvider;

/**
 * 
 * @author shuvro
 *
 */
public class SampleSpout extends BaseRichSpout {

	private static final long serialVersionUID = 6766956999672361586L;
	private SpoutOutputCollector collector;

	private long msgCount = 0;
	private Session session = null;
	private MessageConsumer msgConsumer = null;
	private String queueName = null;
	JMSConfiguration jmsConfiguration = null;

	public SampleSpout(JMSConfiguration jmsConfiguration, String queueName) {
		if (jmsConfiguration == null || queueName == null || queueName.isEmpty()) {
			throw new IllegalArgumentException("The JMSConfiguration object or the queue name is invalid!");
		}

		this.jmsConfiguration = jmsConfiguration;
		this.queueName = queueName;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("message"));
	}

	@Override
	public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		Connection connection;
		try {
			connection = SimpleConnectionProvider.createConnectionInstance(jmsConfiguration.getBrokerUrl(),
					jmsConfiguration.getUserId(), jmsConfiguration.getPassword());
			connection.start();
			this.session = SimpleConnectionProvider.getSession(Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			System.out.println("Exception in open()");
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {
		try {

			if (this.msgConsumer == null) {
				this.msgConsumer = createMsgConsumer(queueName);
			}

			if (msgConsumer != null) {
				Message message = msgConsumer.receive(jmsConfiguration.getReceiveTimeout());

				if (message != null && message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					// System.out.println("Spout Emiting Message: " + text);

					this.collector.emit(new Values(text));
					msgCount++;
					System.out.println("Spout Emited Message #: " + msgCount);
					// System.out.println("Spout Messages recieved from queue
					// count = " + msgCount);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in nextTuple");
			e.printStackTrace();
		}
	}

	private MessageConsumer createMsgConsumer(String queueName) {
		MessageConsumer msgConsumer = null;
		try {
			if (session != null) {
				synchronized (session) {
					Destination destination = session.createQueue(queueName);
					msgConsumer = session.createConsumer(destination);
				}
			}
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
		return msgConsumer;
	}

	@Override
	public void close() {
	}
}