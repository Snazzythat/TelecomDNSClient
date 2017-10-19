import java.nio.ByteBuffer;

/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *         <p>
 *         Construction of DNS Question section
 */
public class DnsQuestion {

	private byte[] QNAME;
	private byte[] QTYPE;
	private byte[] QCLASS;

	private final int A = 0x0001;
	private final int NS = 0x0002;
	private final int MX = 0x000f;

	public DnsQuestion(String domainName, String queryType, int queryClass) {

		QNAME = constructQName(domainName);
		QTYPE = getQueryType(queryType);
		QCLASS = ByteBuffer.allocate(2).putInt((byte) queryClass).array();

	}

	public byte[] getDnsQuestion() {

		byte[] dnsQuestion = new byte[QNAME.length + QTYPE.length + QCLASS.length];

		int destPos = 0;

		for (byte[] byteArray : new byte[][]{QNAME, QTYPE, QCLASS}) {
			System.arraycopy(byteArray, 0, dnsQuestion, destPos, byteArray.length);
			destPos += byteArray.length - 1;
		}

		return dnsQuestion;
	}

	private byte[] constructQName(String domainName) {

		String[] domainNameArray = domainName.split(".");

		// determine length of array so we can construct it
		int queryArrayLength = domainNameArray.length + 1;
		for (String label : domainNameArray) {
			queryArrayLength += label.length();
		}

		byte[] queryNameArray = new byte[queryArrayLength];

		int destPos = 0;
		for (String label : domainNameArray) {
			queryNameArray[destPos] = (byte) label.length();
			destPos++;

			System.arraycopy(label.getBytes(), 0, queryNameArray, destPos, label.length());
			destPos += label.length() - 1;
		}

		// for end put 0
		queryNameArray[destPos] = (byte) 0;

		return queryNameArray;
	}

	private byte[] getQueryType(String queryType) {

		byte[] queryTypeArray = new byte[2];

		switch (queryType) {
			case "A":
				queryTypeArray = ByteBuffer.allocate(2).putInt((byte) A).array();
				break;
			case "NS":
				queryTypeArray = ByteBuffer.allocate(2).putInt((byte) NS).array();
				break;
			case "MX":
				queryTypeArray = ByteBuffer.allocate(2).putInt((byte) MX).array();
				break;
		}

		return queryTypeArray;
	}
}
