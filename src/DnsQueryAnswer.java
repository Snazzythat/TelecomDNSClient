import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DnsQueryAnswer {

	private byte[] dnsQueryAnswer;
	private int dnsQueryQuestionLength;
	private int dnsAnswerPointer;

	// Actual data extracted from the answer, to be sent to stdout
	private short dnsAnswerType;
	private String dnsAnswerIP;
	private String dnsAnswerNameServer;
	private String dnsAnswerCanonicalName;
	private int dnsMailServerPreference;
	private String dnsMailServerExchange;

	public DnsQueryAnswer(byte[] dnsQueryAnswer, int dnsQueryQuestionLength) {

		this.dnsQueryAnswer = dnsQueryAnswer;
		this.dnsQueryQuestionLength = dnsQueryQuestionLength;
	}

	public void queryAnswer() {

		// TODO: loop the following based on ANCOUNT

		queryAnswerName();
		queryAnswerType();
		queryAnswerClass();
		queryTtl();
		queryRdLength();
		queryRData();

	}

	public void queryAnswerName() {

	}

	public void queryAnswerType() {

	}

	public void queryAnswerClass() {

	}

	public void queryTtl() {

	}

	public void queryRdLength() {

	}

	public void queryRData() {

		switch (dnsAnswerType) {
			case 0x0001:
				dnsAnswerIP = queryNameServer(dnsAnswerPointer);
				break;
			case 0x0002:
				dnsAnswerNameServer = queryNameServer(dnsAnswerPointer);
				break;
			case 0x0005:
				dnsAnswerCanonicalName = queryNameServer(dnsAnswerPointer);
				break;
			case 0x000f:
				dnsMailServerPreference = queryMailServerPreference(dnsAnswerPointer);
				dnsMailServerExchange = queryMailServerExchange(dnsAnswerPointer);
				break;
		}
	}

	/**
	 * Verifies if the label is a pointer (compression purposes)
	 *
	 * @param offset
	 * @return
	 */
	public boolean isCompression(int offset) {
		return (dnsQueryAnswer[offset] & 0xC0) == 0xC0;
	}

	/**
	 * Merges two bytes into an integer
	 *
	 * @param b1
	 * @param b2
	 * @return
	 */
	public int getInt(byte b1, byte b2) {
		return b1 << 8 & 0xFF00 | b2 & 0xFF;
	}

	/**
	 * Begin name with "", then if an index is encountered during name parse,
	 * pass the already built name recursively
	 *
	 * @param offset
	 * @param qName
	 * @return
	 */
	public String queryName(int offset, String qName) {

		String name = qName;
		int index = offset;

		while (dnsQueryAnswer[index] != 0) {

			if (!name.equals("") || name.charAt(name.length() - 1) != '.') {
				name += ".";
			}

			if (isCompression(index)) {
				queryName(dnsQueryAnswer[index + 1], name);
			}

			int length = dnsQueryAnswer[index];
			ByteBuffer buffer = ByteBuffer.allocate(length).put(dnsQueryAnswer, index + 1, index + length - 1);
			try {
				name += new String(buffer.array(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			index += length + 1;
		}

		return name;
	}

	/**
	 * @param offset
	 * @param RdLength
	 * @return
	 */
	public String queryIpAddress(int offset, int RdLength) {

		String ipAddress = "";

		for (int counter = 0; counter < RdLength; counter++) {

			if (!ipAddress.equals("")) {
				ipAddress += ".";
			}

			ipAddress += dnsQueryAnswer[offset] & 0xFF;
			offset++;
		}

		return ipAddress;
	}

	/**
	 * @param offset
	 * @return
	 */
	public String queryNameServer(int offset) {
		return queryName(offset, "");
	}

	/**
	 * @param offset
	 * @return
	 */
	public String queryCanonicalName(int offset) {
		return queryName(offset, "");
	}

	/**
	 * @param offset
	 * @return
	 */
	public int queryMailServerPreference(int offset) {

		return getInt(dnsQueryAnswer[offset], dnsQueryAnswer[offset + 1]);
	}

	/**
	 * @param offset
	 * @return
	 */
	public String queryMailServerExchange(int offset) {

		return queryName(offset, "");
	}


}
