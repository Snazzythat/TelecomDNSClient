/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *         <p>
 *         A simple implementation of a DNS client capable to query A/NS/MX records.
 */
public class DnsClient {

	private String dnsServerIP;
	private String domainName;
	private int timeout;
	private int retryCount;
	private int port;
	private String queryType;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		DnsClient clientIntsance = new DnsClient();

		if (args.length > 0) {
			try {
				clientIntsance.parseArguments(args);
			} catch (Exception e) {
				System.out.println("Failed to parse the arguments. Exiting.");
				System.exit(69);
			}

			clientIntsance.startQuery();
		} else {
			System.out.println("No arhuments were passed. See usage:\n");
			System.out.println("@server | (req) - IPv4 address of the DNSserver");
			System.out.println("name | (req) - Domain name to query.");
			System.out.println("-t timeout | (opt) - Timeout before query retransmission");
			System.out.println("-r max-retries | (opt) - Retry count for retransmission");
			System.out.println("-p port | (opt) - UDP port number of the DNSserver, default: 53");
			System.out.print("-mx|-ns  | (opt) - Perform an MX or NS query, default: A");
			System.exit(69);
		}
	}

	/**
	 * Parses arguments
	 *
	 * @param args
	 */
	public void parseArguments(String[] args) {

		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
				case '-':
					switch (args[i].charAt(1)) {
						case 't':
							if (!args[i + 1].isEmpty()) {
								timeout = Integer.parseInt(args[i + 1]);
								i++;
							}
							break;
						case 'r':
							if (!args[i + 1].isEmpty()) {
								retryCount = Integer.parseInt(args[i + 1]);
								i++;
							}
							break;
						case 'p':
							if (!args[i + 1].isEmpty()) {
								port = Integer.parseInt(args[i + 1]);
								i++;
							}
							break;
						case 'n':
							queryType = "NX";
							i++;
							break;
						case 'm':
							queryType = "MX";
							i++;
							break;
					}

					break;
				case '@':
					dnsServerIP = args[i].replace("@", "");
					String[] splitIP = dnsServerIP.split("\\.");
					if (splitIP.length != 4) {
						System.out.print("Invalid IPv4 address format! Make sure its X.X.X.X");
						System.exit(69);
					}
					for (String s : splitIP) {
						int j = Integer.parseInt(s);
						if ((j < 0) || (j > 255)) {
							System.out.print("Invalid IPv4 address format! Every octet must be between 0-255");
							System.exit(69);
						}
					}
					if (dnsServerIP.endsWith(".")) {
						System.out.print("Invalid IPv4 address format! Can't end with '.'");
						System.exit(69);
					}
					break;
				default:
					domainName = args[i];
					break;
			}
		}
	}

	/**
	 * Start the Query Worker to build the DNS query and send it to the server
	 */
	public void startQuery() {
		QueryWorker worker = new QueryWorker(dnsServerIP, domainName, timeout, retryCount, port, queryType);
	}


}
