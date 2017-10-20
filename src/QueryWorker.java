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
				successfulQuery = true;

			} catch (SocketTimeoutException e) {

				//If receive on socket timed out, fallback mechanism is used below
				if (retryCount > 0) {
					System.out.println("Timeout occurred. Retrying. Attempt "
							+ Integer.toString(retryCounter)
							+ " out of "
							+ Integer.toString(this.retryCount));
					if (retryCounter == this.retryCount) {
						System.out.println("Maximum amount of retries reached. Exiting...");
						break;
					}
					retryCounter += 1;
				} else {
					System.out.println("Request timed out. Exiting...");
					break;
				}
			}

			if (successfulQuery) {
				endTime = System.currentTimeMillis();

				//TODO: parse response
				//TODO: print stuff as specified
				break;
			}
		}
	}

	/**
	 * Util to build an InetAddress used for Datagram socket
	 * LEGAL UTILIZATION OF getByAddress is ALLOWED
	 *
	 * @return
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
