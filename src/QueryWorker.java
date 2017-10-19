/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *         <p>
 *         Query Worker
 */
public class QueryWorker {

	private byte[] dnsRequest;

	private String dnsServerIP;
	private String domainName;
	private int timeout;
	private int retryCount;
	private int port;
	private String queryType;

	/**
	 * Constructor
	 *
	 * @param dnsIP
	 * @param dName
	 * @param to
	 * @param rt
	 * @param pt
	 * @param qt
	 */
	public QueryWorker(String dnsIP, String dName, int to, int rt, int pt, String qt) {
		dnsServerIP = dnsIP;
		domainName = dName;
		timeout = to;
		retryCount = rt;
		port = pt;
		queryType = qt;
	}

	/**
	 * Build the actual DNS query
	 */
	public void buildQuery() {
		DnsQueryRequest dnsQueryRequest = new DnsQueryRequest(domainName, queryType);
		dnsRequest = dnsQueryRequest.createRequestPacket();

		System.out.println(69);
	}
}
