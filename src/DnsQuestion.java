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

	private final short A = 0x0001;
	private final short NS = 0x0002;
	private final short MX = 0x000f;

	public DnsQuestion(String domainName, String queryType, short queryClass) {

		QNAME = constructQName(domainName);
		QTYPE = getQueryType(queryType);
		QCLASS = ByteBuffer.allocate(2).putShort(queryClass).array();
	}

	public byte[] getDnsQuestion() {

		return ByteBuffer.allocate(QNAME.length + QTYPE.length + QCLASS.length)
				.put(QNAME)
				.put(QTYPE)
				.put(QCLASS)
				.array();
	}

	private byte[] constructQName(String domainName) {

		String[] domainNameArray = domainName.split("\\.");

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
			destPos += label.length();
		}

		// for end put 0
		queryNameArray[destPos] = (byte) 0;

		return queryNameArray;
	}

	private byte[] getQueryType(String queryType) {

		ByteBuffer queryTypeArray = ByteBuffer.allocate(2);

		switch (queryType) {
			case "A":
				queryTypeArray.putShort(A);
				break;
			case "NS":
				queryTypeArray.putShort(NS);
				break;
			case "MX":
				queryTypeArray.putShort(MX);
				break;
		}

		return queryTypeArray.array();
	}
}
