package org.mellowtech.examples;

import org.mellowtech.core.codec.StringCodec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * @author msvens
 * @since 2017-02-25
 */
public class ScannerCodecInputStream extends InputStream {

  ByteBuffer bb = ByteBuffer.allocate(1024);

  private boolean closed = false;

  private final Scanner s;
  private final int minLength;
  private final StringCodec codec = new StringCodec();

  public ScannerCodecInputStream(Scanner s) {
    this(s, -1);

  }

  public ScannerCodecInputStream(Scanner s, int minLength) {
    this.s = s;
    this.minLength = minLength;
    readNext();
  }

  @Override
  public int available() throws IOException {
    return bb.remaining();
  }

  @Override
  public int read() throws IOException {
    if (closed == true) return -1;
    if (bb.hasRemaining()) {
      return bb.get();
    }
    readNext();
    if (closed) {
      return -1;
    } else {
      return bb.get();
    }
  }

  private void readNext() {
    if (closed) return;
    if (minLength > -1) {
      readMinLength();
      return;
    }
    if (!s.hasNext()) {
      closed = true;
      return;
    }
    //CBString temp = new CBString(s.next());
    String temp = s.next();
    //if(temp.get().length() < 1) System.out.println("zero length");
    int bSize = codec.byteSize(temp);
    if (bb.capacity() < bSize) bb = ByteBuffer.allocate(bSize);
    bb.clear();
    bb.limit(bSize);
    codec.to(temp, bb);
    bb.flip();
  }

  private void readMinLength() {
    while (s.hasNext()) {
      String temp = s.next();
      //temp.set(s.next());
      if (temp.length() > minLength) {
        int bSize = codec.byteSize(temp);
        if (bb.capacity() < bSize) bb = ByteBuffer.allocate(bSize);
        bb.clear();
        bb.limit(bSize);
        codec.to(temp, bb);
        bb.flip();
        return;
      }
    }
    closed = true;
    return;
  }
}

