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

	/**
	 * Constructor
	 */
	public DnsQueryRequest() {

	}

	/**
	 * Gets the header byte array
	 */
	public void buildDnsRequestHeader() {
		DnsHeader dnsHeader = new DnsHeader();
		requestHeader = dnsHeader.getDNSHeader();
	}

	/**
	 * Gets the question byte array
	 */
	public void buildDnsRequestQuestion() {

	}

	/**
	 * Joins header and question for a request packet
	 * And returns the packet data
	 */
	public void createRequestPacket() {

	}

}
