import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DnsQueryAnswer {

	private byte[] dnsQueryQuestion;
	private byte[] dnsQueryAnswer;
	private int dnsAnswerPointer;
	private short dnsRdLength;
	private boolean dnsAnswerIsAuthorative = false;
	private boolean dnsAnswerTruncated = false;
	private int dnsAnswerRRCount;
	private int dnsAdditionalRRCount;

	// Actual data extracted from the answer, to be sent to stdout
	private short dnsAnswerType;
	private short dnsAnswerClass;
	private int dnsAnswerTtl;
	private String dnsAnswerName = "";
	private String dnsAnswerIP = "";
	private String dnsAnswerNameServer = "";
	private String dnsAnswerCanonicalName = "";
	private int dnsMailServerPreference;
	private String dnsMailServerExchange = "";

	/**
	 * Constructor
	 *
	 * @param dnsQueryQuestion Question bytes to be compared against answer for validity
	 * @param dnsQueryAnswer   Answer bytes to be parsed
	 */
	public DnsQueryAnswer(byte[] dnsQueryQuestion, byte[] dnsQueryAnswer) {

		this.dnsQueryQuestion = dnsQueryQuestion;
		this.dnsQueryAnswer = dnsQueryAnswer;
		this.dnsAnswerPointer = dnsQueryQuestion.length;
	}

	/**
	 * Main looping parser function
	 */
	public void queryAnswer() {

		// TODO: loop the following based on ANCOUNT
		queryAnswerValidity();
		queryAnswerHeaderFields();

		for (int anCount = queryAnswerSectionRecordsCount(6); anCount > 0; anCount--) {
			queryAnswerName();
			queryAnswerType();
			queryAnswerClass();
			queryTtl();
			queryRdLength();
			queryRData();
		}

	}

	/**
	 * Goes through answer header and gets static fields to print out
	 */
	public void queryAnswerHeaderFields() {

		//AA and TC fields in header is located in byte 2
		dnsAnswerIsAuthorative = queryIfNameServerAuthoritative(2);
		dnsAnswerTruncated = queryIfMessageWasTruncated(2);

		//RA field located in byte 3
		queryIfServerSupportsRecursion(3);

		//RCODE field located in byte 3, last 4 bits
		switch (queryErrorCode(3)) {
			case 1:
				System.out.println("ERROR \t Format error: the name server was unable to interpret the query.");
				break;
			case 2:
				System.out.println("ERROR \t  Server failure: the name server was unable to process this query " +
						"due to a problem with the name server");
				break;
			case 3:
				System.out.println("NOTFOUND \t Name error: the domain name specified in the query not found.");
				break;
			case 4:
				System.out.println("ERROR \t Not implemented: the name server does not support the requested kind of query.");
				break;
			case 5:
				System.out.println("ERROR \t Refused: the name server refuses to perform the requested operation for policy reasons");
				break;
		}

		dnsAnswerRRCount = queryAnswerSectionRecordsCount(6);
		dnsAdditionalRRCount = queryAdditionalSectionRecordsCount(10);
	}

	public void queryAnswerName() {

		queryName(dnsAnswerPointer, dnsAnswerName);

		while (dnsQueryAnswer[dnsAnswerPointer] != 0) {

			if (isCompression(dnsQueryAnswer[dnsAnswerPointer])) {
				dnsAnswerPointer += 2;
				break;
			}
			dnsAnswerPointer += dnsQueryAnswer[dnsAnswerPointer] + 1;
		}
	}

	public void queryAnswerType() {

		dnsAnswerType = (short) ((dnsQueryAnswer[dnsAnswerPointer] & 0xFF) << 8
				| dnsQueryAnswer[dnsAnswerPointer + 1] & 0xFF);

		dnsAnswerPointer += 2;
	}

	public void queryAnswerClass() {

		dnsAnswerClass = (short) ((dnsQueryAnswer[dnsAnswerPointer] & 0xFF) << 8
				| dnsQueryAnswer[dnsAnswerPointer + 1] & 0xFF);

		dnsAnswerPointer += 2;
	}

	public void queryTtl() {

		dnsAnswerTtl = ((dnsQueryAnswer[dnsAnswerPointer] & 0xFF) << 24)
				| (dnsQueryAnswer[dnsAnswerPointer + 1] & 0xFF) << 16
				| ((dnsQueryAnswer[dnsAnswerPointer + 2] & 0xFF) << 8)
				| dnsQueryAnswer[dnsAnswerPointer + 3] & 0xFF;

		dnsAnswerPointer += 4;
	}

	public void queryRdLength() {

		dnsRdLength = (short) ((dnsQueryAnswer[dnsAnswerPointer] & 0xFF) << 8
				| dnsQueryAnswer[dnsAnswerPointer + 1] & 0xFF);

		dnsAnswerPointer += 2;
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

		dnsAnswerPointer += dnsRdLength + 1;
	}

	/**
	 * Begin name with "", then if an index is encountered during name parse,
	 * pass the already built name recursively
	 *
	 * @param offset offset in the answer array
	 * @param qName  query name parsed so far
	 * @return completed query name
	 */
	public String queryName(int offset, String qName) {

		String name = qName;
		int index = offset;

		while (dnsQueryAnswer[index] != 0) {

			if (!name.equals("") || (name.length() > 0 && name.charAt(name.length() - 1) != '.')) {
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
	 * @param offset   offset in the answer array
	 * @param RdLength length of the RData section
	 * @return IP address
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
	 * @param offset offset in the answer array
	 * @return name of the server
	 */
	public String queryNameServer(int offset) {
		return queryName(offset, "");
	}

	/**
	 * @param offset offset in the answer array
	 * @return server canonical name
	 */
	public String queryCanonicalName(int offset) {
		return queryName(offset, "");
	}

	/**
	 * @param offset offset in the answer array
	 * @return mail server preference
	 */
	public int queryMailServerPreference(int offset) {

		return getInt(dnsQueryAnswer[offset], dnsQueryAnswer[offset + 1]);
	}

	/**
	 * @param offset offset in the answer array
	 * @return mail server exchange
	 */
	public String queryMailServerExchange(int offset) {

		return queryName(offset, "");
	}

	/**
	 * Checks the AA field in Answers packet Question fields.
	 * If set to 1, name server is authoritative
	 * Field is located in 3rd byte of Question
	 *
	 * @param offset offset in the answer array
	 * @return determines if name server is authoritative or not
	 */
	public boolean queryIfNameServerAuthoritative(int offset) {
		boolean isAuthoritative = false;

		if (((dnsQueryAnswer[offset] >> 2) & 1) == 1) {
			isAuthoritative = true;
		}
		return isAuthoritative;
	}

	/**
	 * Checks the TC field in Answers packet Question fields.
	 * If set to 1, message was truncated
	 *
	 * @param offset offset in the answer array
	 * @return determines if answer is truncated or not
	 */
	public boolean queryIfMessageWasTruncated(int offset) {
		boolean truncated = false;

		if (((dnsQueryAnswer[offset] >> 1) & 1) == 1) {
			truncated = true;
		}
		return truncated;
	}

	/**
	 * Checks the RA field in Answers packet Question fields.
	 * If set to 1, server supports recursion.
	 * If 0, need to indicate error.
	 *
	 * @param offset offset in the answer array
	 * @return determines if server supports recursion or not
	 */
	public boolean queryIfServerSupportsRecursion(int offset) {
		boolean recursionSupported = false;

		if (((dnsQueryAnswer[offset] >> 7) & 1) == 1) {
			recursionSupported = true;
		} else {
			System.out.println("ERROR \t The server does not support recursive requests");
		}
		return recursionSupported;
	}

	/**
	 * Checks RCODE field in Answers packet Question fields
	 * If field is 0, no error, else any of 5 specified error types must be handled
	 *
	 * @param offset offset in the answer array
	 */
	public int queryErrorCode(int offset) {
		return dnsQueryAnswer[offset] & 0x0F;
	}

	/**
	 * Checks ANCOUNT field in Answers packet Question fields
	 * Indicates the number of resource records found in answer section
	 *
	 * @param offset offset in the answer array
	 * @return returns the ANCOUNT field
	 */
	public int queryAnswerSectionRecordsCount(int offset) {
		return getInt(dnsQueryAnswer[offset], dnsQueryAnswer[offset + 1]);
	}

	/**
	 * Checks ARCOUNT field in Answers packet Question fields
	 * Indicates the number of resource records found in additional section
	 *
	 * @param offset offset in the answer array
	 * @return returns the ARCOUNT field
	 */
	public int queryAdditionalSectionRecordsCount(int offset) {
		return getInt(dnsQueryAnswer[offset], dnsQueryAnswer[offset + 1]);
	}

	/**
	 * Verifies if the label is a pointer (compression purposes)
	 *
	 * @param offset offset in the answer array
	 * @return determines if the name is compressed or not
	 */
	public boolean isCompression(int offset) {
		return (dnsQueryAnswer[offset] & 0xC0) == 0xC0;
	}

	/**
	 * Merges two bytes into an integer
	 *
	 * @param b1 more significant byte
	 * @param b2 less significant byte
	 * @return the two bytes conjoined as an integer
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
			System.out.println("ERROR \t Invalid DNS answer packet received: request and answer IDs don't match.");
			System.exit(69);
		}
	}

}
