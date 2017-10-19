import java.nio.*;
import java.util.*;

/**
 * Lab 2 Telecom ECSE 489
 *
 * @author Roman Andoni 260585085
 * @author Armen Stepanians 260586139
 *         <p>
 *         Construction of DNS Header section
 */
public class DnsHeader {

	private short id;
	private short headerFlags;
	private short qdCount;
	private short anCount;
	private short nsCount;
	private short arCount;
	private final int randRange = Short.MAX_VALUE + 1;
	public byte[] headerData;

	/**
	 * Constructor
	 */
	public DnsHeader() {
		this.id = generateRandomID();
		this.qdCount = 0x0001;
		this.anCount = 0x0000;
		this.nsCount = 0x0000;
		this.arCount = 0x0000;
		this.headerFlags = 0x0100;
		//In flags, the only bit set is the RD bit
		//Indicating a recursive request
	}

	/**
	 * Random ID geenrator
	 *
	 * @return 16-bit short for ID
	 */
	private short generateRandomID() {
		Random randomGen;
		randomGen = new Random();
		short randomID = (short) randomGen.nextInt(randRange);
		return randomID;
	}

	/**
	 * Building the header byte array
	 *
	 * @return
	 */
	public byte[] getDNSHeader() {
		//16bit/2byte * 6 entries -> 12 bytes per header
		ByteBuffer headerData = ByteBuffer.allocate(12);
		headerData.putShort(this.id);
		headerData.putShort(this.headerFlags);
		headerData.putShort(this.qdCount);
		headerData.putShort(this.anCount);
		headerData.putShort(this.nsCount);
		headerData.putShort(this.arCount);

		return headerData.array();
	}

}
