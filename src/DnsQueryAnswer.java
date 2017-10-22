import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DnsQueryAnswer {

	private byte[] dnsQueryQuestion;
	private byte[] dnsQueryAnswer;
	private int dnsQueryQuestionLength;
	private int dnsAnswerPointer;
	private int dnsRdLength;

	// Actual data extracted from the answer, to be sent to stdout
	private short dnsAnswerType;
	private String dnsAnswerIP;
	private String dnsAnswerNameServer;
	private String dnsAnswerCanonicalName;
	private int dnsMailServerPreference;
	private String dnsMailServerExchange;

	/**
	 * Constructor
	 *
	 * @param dnsQueryQuestion Question bytes to be compared against answer for validity
	 * @param dnsQueryAnswer   Answer bytes to be parsed
	 */
	public DnsQueryAnswer(byte[] dnsQueryQuestion, byte[] dnsQueryAnswer) {

		this.dnsQueryQuestion = dnsQueryQuestion;
		this.dnsQueryAnswer = dnsQueryAnswer;
		this.dnsQueryQuestionLength = dnsQueryQuestion.length;
	}

	public void queryAnswer() {

		// TODO: loop the following based on ANCOUNT

		queryAnswerValidity();
		queryAnswerQuestionFields();
		queryAnswerName();
		queryAnswerType();
		queryAnswerClass();
		queryTtl();
		queryRdLength();
		queryRData();

	}

	public void queryAnswerQuestionFields() {

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
				dnsAnswerIP = queryIpAddress(dnsAnswerPointer, dnsRdLength);
				break;
			case 0x0002:
				dnsAnswerNameServer = queryNameServer(dnsAnswerPointer);
				break;
			case 0x0005:
				dnsAnswerCanonicalName = queryCanonicalName(dnsAnswerPointer);
				break;
			case 0x000f:
				dnsMailServerPreference = queryMailServerPreference(dnsAnswerPointer);
				dnsMailServerExchange = queryMailServerExchange(dnsAnswerPointer);
				break;
		}
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

	/**
	 * Checks the AA field in Answers packet Question fields.
	 * If set to 1, name server is authoritative
	 *
	 * @param offset
	 * @return
	 */
	public boolean queryIfNameServerAuthoritative(int offset) {
		boolean authoritative = false;
		return authoritative;
	}

	/**
	 * Checks the TC field in Answers packet Question fields.
	 * If set to 1, message was truncated
	 *
	 * @param offset
	 * @return
	 */
	public boolean queryIfMessageWasTruncated(int offset) {
		boolean truncated = false;
		return truncated;
	}

	/**
	 * Checks the RA field in Answers packet Question fields.
	 * If set to 1, server supports recursion.
	 * If 0, need to indicate error.
	 *
	 * @param offset
	 * @return
	 */
	public boolean queryIfServerSupportsRecursion(int offset) {
		boolean recursionSupported = false;
		return recursionSupported;
	}

	/**
	 * Checks RCODE field in Answers packet Question fields
	 * If field is 0, no error, else any of 5 specified error types must be handled
	 *
	 * @param offset
	 */
	public void queryIfErrorsExist(int offset) {

	}

	/**
	 * Checks ANCOUNT field in Answers packet Question fields
	 * Indicates the number of resource records found in answer section
	 *
	 * @param offset
	 * @return
	 */
	public int queryAnswerSectionRecordsCount(int offset) {
		int numberOfRRs = 0;
		return numberOfRRs;
	}

	/**
	 * Checks ARCOUNT field in Answers packet Question fields
	 * Indicates the number of resource records found in additional section
	 *
	 * @param offset
	 * @return
	 */
	public int queryAdditionalSectionRecordsCount(int offset) {
		int numberOfRRs = 0;
		return numberOfRRs;
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
	 * Verifies if the answer data is actual valid by comparing
	 * question and answer IDs
	 */
	public void queryAnswerValidity() {
		if (getInt(dnsQueryQuestion[0], dnsQueryQuestion[1]) != getInt(dnsQueryAnswer[0], dnsQueryAnswer[1])) {
			System.out.println("Error \t Invalid DNS answer packet received: request and answer IDs don't match.");
			System.exit(69);
		}
	}

}
