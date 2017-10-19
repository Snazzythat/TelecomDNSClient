/**
 * Query request class. Responsible to build the DNS packet by joining header and the question.
 */
public class DnsQueryRequest {

    private byte[] requestHeader;

    /**
     * Constructor
     */
    public DnsQueryRequest(){

    }

    /**
     * Gets the header byte array
     */
    public void buildDnsRequestHeader(){
        Header dnsHeader = new Header();
        requestHeader = dnsHeader.getDNSHeader();
    }

    /**
     * Gets the question byte array
     */
    public void buildDnsRequestQuestion(){

    }

    /**
     * Joins header and question for a request packet
     * And returns the packet data
     */
    public void createRequestPacket()
    {

    }

}
