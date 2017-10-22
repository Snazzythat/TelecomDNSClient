/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 * <p>
 * Query Worker
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

public class QueryWorker {

	private byte[] dnsRequest;

	private byte[] dnsAnswer = new byte[1024];

	private String dnsServerIP;
	private String domainName;
	private int timeout;
	private int retryCount;
	private int port;
	private String queryType;

	/**
	 * Constructor
	 *
	 * @param dnsIP DNS server IP
	 * @param dName domain name
	 * @param to    timeout
	 * @param rt    number of retries
	 * @param pt    port
	 * @param qt    query type
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
	}

	/**
	 * Sending the DNS query via sockets
	 */
	public void sendDnsQuery() throws Exception {
		// Address to use with the Datagram socket.
		InetAddress dnsIpAddress = convertIpAddress();

		DatagramSocket udpSock = new DatagramSocket();

		//Setting timeout, if any specified.
		if (this.timeout != 0) {
			udpSock.setSoTimeout(this.timeout);
		}

		int retryCounter = 1;

		DatagramPacket requestPacket = new DatagramPacket(dnsRequest, dnsRequest.length, dnsIpAddress, this.port);

		DatagramPacket answerPacket = new DatagramPacket(dnsAnswer, dnsAnswer.length);

		long startTime = 0;
		long endTime = 0;
		boolean successfulQuery = false;

		//Enter Main Loop
		while (true) {
			try {
				startTime = System.currentTimeMillis();
				udpSock.send(requestPacket);
				//ASYNC WAIT ON ANSWER
				udpSock.receive(answerPacket);
				endTime = System.currentTimeMillis();

				successfulQuery = true;
				System.out.println("\n");
				System.out.println("Successful query.");

			} catch (SocketTimeoutException e) {

				//If receive on socket timed out, fallback mechanism is used below
				if (retryCount > 0) {
					System.out.println("Timeout occurred. Retrying. Attempt "
							+ Integer.toString(retryCounter)
							+ " out of "
							+ Integer.toString(this.retryCount));
					if (retryCounter == this.retryCount) {
						System.out.println("ERROR \t Maximum number of retries " + this.retryCount + " exceeded");
						break;
					}
					retryCounter += 1;
				} else {
					System.out.println("ERROR \t Request timed out.");
					break;
				}
			}

			if (successfulQuery) {

				long difference = endTime - startTime;
				System.out.println("Response received after " + difference + " ms.");
				System.out.println("Parsing the DNS response packet...");
				DnsQueryAnswer answer = new DnsQueryAnswer(requestPacket.getData(), answerPacket.getData());
				//Will loop over the answer byte array, parse out the necessary fields and print to stdout
				answer.queryAnswer();
				break;
			}
		}
	}

	/**
	 * Util to build an InetAddress used for Datagram socket
	 * LEGAL UTILIZATION OF getByAddress is ALLOWED
	 *
	 * @return InetAddress object of the specified IP
	 */
	public InetAddress convertIpAddress() throws UnknownHostException {
		String[] splitIP = this.dnsServerIP.split("\\.");

		byte[] ipByt = new byte[4];

		for (int i = 0; i < splitIP.length; i++) {
			ipByt[i] = (byte) (int) Integer.valueOf(splitIP[i]);
		}
		return InetAddress.getByAddress(ipByt);
	}
}
