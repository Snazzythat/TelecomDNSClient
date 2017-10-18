/**
 * Lab 2 Telecom ECSE 489
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *
 * A simple implementation of a DNS client capable to query A/NS/MX records.
 */
public class DnsClient {
	
	private String dnsServerIP;
	private String domainName;
	private int timeout;
	private int retryCount;
	private int port;
	private int queryType;
	
	public static void main(String[] args) {
	
		DnsClient clientIntsance = new DnsClient();
		
		if(args.length > 0){
			try{
				clientIntsance.parseArguments(args);
			}
			catch(Exception e){
				System.out.println("Failed to parse the arguments. Exiting.");
				System.exit(69);
			}
			
			clientIntsance.startQuery();
		}
		else{
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
	 * Parses args
	 * @param arguments
	 */
	public void parseArguments(String[] arguments){
		
	}
	
	/**
	 * Start the Query Worker to build the DNS query and send it to the server
	 */
	public void startQuery()
	{
		
	}
	

}
