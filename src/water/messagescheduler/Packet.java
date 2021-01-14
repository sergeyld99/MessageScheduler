package water.messagescheduler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Packet {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Packet [PacketType=" + PacketType + ", BodyLength=" + BodyLength 
				+ "]";
	}
	private byte   PacketType;//Тип пакета
	private int BodyLength;//Длинна тела пакета
	private byte[] body;
	/*создадим пакет из буффера*/
	public Packet(byte[] packet) {
		ByteBuffer buffer = ByteBuffer.allocate(packet.length);
		buffer.put(packet);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.flip();
		PacketType = buffer.get();
		BodyLength = buffer.getInt();
		//body = buffer.
	}
	
	public Packet(byte packetType, int bodyLength, byte[] body) {
		super();
		PacketType = packetType;
		BodyLength = bodyLength;
		this.body = body;
	}
	public byte getPacketType() {
		return PacketType;
	}
	public void setPacketType(byte packetType) {
		PacketType = packetType;
	}
	public int getBodyLength() {
		return BodyLength;
	}
	public void setBodyLength(int bodyLength) {
		BodyLength = bodyLength;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	/*Преобразуем пакет в массив байт*/
	public byte[] toBytes() {
		int sizePacket = sizeof.BYTE_FIELD_SIZE + sizeof.INT_FIELD_SIZE + sizeof.INT_FIELD_SIZE + BodyLength;
		ByteBuffer buffer = ByteBuffer.allocate(sizePacket);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(PacketType);
		buffer.putInt(0);//Адрес, мать его так
		buffer.putInt(BodyLength);
		if (BodyLength>0)
			buffer.put(body);
		
		return buffer.array();
	}
	
	
}
