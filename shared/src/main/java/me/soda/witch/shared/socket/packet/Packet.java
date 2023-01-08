package me.soda.witch.shared.socket.packet;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

public class Packet<T> implements Serializable {
    private T data;
    private boolean compressed = false;

    public Packet(T data) {
        this.data = data;
    }

    public T get() throws IOException, ClassNotFoundException, DataFormatException {
        if (!compressed)
            return data;
        else {
            return decompress();
        }
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this.data);
        byte[] bytes = b.toByteArray();
        b.close();
        o.close();
        return bytes;
    }

    public void compress() throws IOException {
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        final DeflaterOutputStream outputStream = new DeflaterOutputStream(bytesOut, deflater);
        outputStream.write(toBytes());
        outputStream.finish();
        byte[] bytes = bytesOut.toByteArray();
        outputStream.close();
        bytesOut.close();
        this.data = (T) bytes;
        this.compressed = true;
    }

    @SuppressWarnings("unchecked")
    private T decompress() throws IOException, ClassNotFoundException, DataFormatException {
        byte[] bytes = (byte[]) this.data;
        Inflater inflater = new Inflater(true);
        inflater.setInput(bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
        byte[] buffer = new byte[4096];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();

        ByteArrayInputStream b = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream o = new ObjectInputStream(b);
        T message = (T) o.readObject();
        b.close();
        o.close();
        return message;
    }
}
