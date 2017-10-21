import java.nio.ByteBuffer;

/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *         <p>
 *         Query request class. Responsible to build the DNS packet by joining header and the question.
 */
public class DnsQueryRequest {

	private byte[] requestHeader;
	private byte[] requestQuestion;
	private String domainName;
	private String queryType;

	/**
	 * Constructor
	 */
	public DnsQueryRequest(String domainName, String queryType) {

		this.domainName = domainName;
		this.queryType = queryType;
	}

	/**
	 * Gets the header byte array
	 */
	public void buildDnsRequestHeader() {
		DnsHeader dnsHeader = new DnsHeader();
		requestHeader = dnsHeader.getDnsHeader();
	}

	/**
	 * Gets the question byte array
	 */
	public void buildDnsRequestQuestion() {
		DnsQuestion dnsQuestion = new DnsQuestion(domainName, queryType, (short) 0x0001);
		requestQuestion = dnsQuestion.getDnsQuestion();
	}

	/**
	 * Joins header and question for a request packet
	 * And returns the packet data
	 */
	public byte[] createRequestPacket() {

		buildDnsRequestHeader();
		buildDnsRequestQuestion();

		return ByteBuffer.allocate(requestHeader.length + requestQuestion.length)
				.put(requestHeader)
				.put(requestQuestion)
				.array();
	}

}
