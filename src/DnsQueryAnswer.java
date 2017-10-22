import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DnsQueryAnswer {

	private byte[] dnsQueryAnswer;
	private int dnsQueryQuestionLength;
	private int dnsAnswerPointer;

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

	}

	public boolean isCompression(int offset) {
		return (dnsQueryAnswer[offset] & 0xC0) == 0xC0;
	}

	public String queryName(int offset) {

		String name = "";
		int index = offset;

		while (dnsQueryAnswer[index] != 0) {

			if (!name.equals("")) {
				name += ".";
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

}
