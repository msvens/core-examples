package org.mellowtech.examples;

import org.mellowtech.core.codec.BCodec;
import org.mellowtech.core.codec.CodecUtil;
import org.mellowtech.core.codec.StringCodec;

import java.nio.ByteBuffer;

/**
 * @author msvens
 * @since 2017-02-25
 */
public class Container1Codec implements BCodec<Container1> {

  BCodec<String> f2Codec = new StringCodec();

  @Override
  public int byteSize(Container1 c) {
    return CodecUtil.byteSize(4 + f2Codec.byteSize(c.f2), true);
  }

  @Override
  public int byteSize(ByteBuffer bb) {
    return CodecUtil.peekSize(bb, true);
  }

  @Override
  public Container1 from(ByteBuffer bb) {
    CodecUtil.getSize(bb, true);
    int f1 = bb.getInt();
    String f2 = f2Codec.from(bb);
    return new Container1(f1,f2);
  }

  @Override
  public void to(Container1 c, ByteBuffer bb) {
    CodecUtil.putSize(4 + f2Codec.byteSize(c.f2), bb, true);
    bb.putInt(c.f1);
    f2Codec.to(c.f2, bb);
  }
}
