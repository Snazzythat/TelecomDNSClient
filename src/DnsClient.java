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
	private int timeout = 0;
	private int retryCount = 0;
	private int port = 53;
	private String queryType = "A";

	public static void main(String[] args) {

		DnsClient clientIntsance = new DnsClient();

		if (args.length >= 2) {
			try {
				clientIntsance.parseArguments(args);
			} catch (Exception e) {
				System.out.println("ERROR \t Failed to parse the arguments. Exiting.");
				System.exit(69);
			}

			clientIntsance.startQuery();

		} else {
			System.out.println("No arguments were passed. See usage:\n");
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
								try {
									timeout = Integer.parseInt(args[i + 1]);
									i++;
								} catch (Exception e) {
									System.out.print("ERROR \t Format error: timeout needs to be of type integer.");
									System.exit(69);
								}
							}
							break;
						case 'r':
							if (!args[i + 1].isEmpty()) {
								try {
									retryCount = Integer.parseInt(args[i + 1]);
									i++;
								} catch (Exception e) {
									System.out.print("ERROR \t Format error: number of retries needs to be of type integer.");
									System.exit(69);
								}
							}
							break;
						case 'p':
							if (!args[i + 1].isEmpty()) {
								try {
									port = Integer.parseInt(args[i + 1]);
									i++;
								} catch (Exception e) {
									System.out.print("ERROR \t Format error: port needs to be of type integer.");
									System.exit(69);
								}
							}
							break;
						case 'n':
							queryType = "NS";
							break;
						case 'm':
							queryType = "MX";
							break;
					}

					break;
				case '@':
					dnsServerIP = args[i].replace("@", "");
					String[] splitIP = dnsServerIP.split("\\.");
					if (splitIP.length != 4) {
						System.out.print("ERROR \t Format error: Invalid IPv4 address format! Make sure its X.X.X.X");
						System.exit(69);
					}
					for (String s : splitIP) {
						int j = Integer.parseInt(s);
						if ((j < 0) || (j > 255)) {
							System.out.print("ERROR \t Format error: Invalid IPv4 address format! Every octet must be between 0-255");
							System.exit(69);
						}
					}
					if (dnsServerIP.endsWith(".")) {
						System.out.print("ERROR \t Format error: Invalid IPv4 address format! Can't end with '.'");
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

		System.out.println("\n\n");
		System.out.println("DnsClient sending request for " + domainName);
		System.out.println("Server: " + dnsServerIP);
		System.out.println("Port: " + port);
		System.out.println("Request type: " + queryType);

		QueryWorker worker = new QueryWorker(dnsServerIP, domainName, timeout, retryCount, port, queryType);
		worker.buildQuery();

		try {
			System.out.println("Sending the DNS query...\n");
			worker.sendDnsQuery();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}


}
