/**
 * Main Quesry Worker class
 *
 */
public class QueryWorker {
	
	public String dnsServerIP;
	public String domainName;
	public int timeout;
	public int retryCount;
	public int port;
	public String queryType;
	
	/**
	 * Constructor
	 * @param dnsIP
	 * @param dName
	 * @param to
	 * @param rt
	 * @param pt
	 * @param qt
	 */
    public QueryWorker(String dnsIP, String dName, int to, int rt, int pt, String qt){  
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
    public void buildQuery()
    {
    	
    }
}
