/*
 * Copyright (c) 2013 mellowtech.org.
 *
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 *
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 *
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 *
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 *
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 *
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 *
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 */

package org.mellowtech.examples;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mellowtech.core.codec.*;

/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class Containers {

  public static void main(String[] args){
    serialize();
    list();
    testContainer1();
    testContainer3();
  }

  public static void serialize(){
    IntCodec codec = new IntCodec();

    Integer firstInt = 1;
    ByteBuffer bb = codec.to(firstInt);
    bb.flip();
    Integer secondInt = codec.from(bb);

    System.out.println("serialize: "+firstInt+" "+secondInt);
  }

  public static void list(){
    MixedListCodec codec = new MixedListCodec();
    List<Object> list = new ArrayList<>();
    list.add(1);
    list.add("a string");
    list.add(new Long(100));
    list.add(true);

    ByteBuffer bb = codec.to(list);
    list.clear();

    //don't create a new object
    bb.flip();
    list = codec.from(bb);
    Integer first = (Integer) list.get(0);
    String second = (String) list.get(1);
    Long third = (Long) list.get(2);
    Boolean forth = (Boolean) list.get(3);
    System.out.println("list: "+first+" "+second+" "+third+" "+forth);
  }

  public static void testContainer1(){

    BCodec<Container1> codec = new BCodec<Container1>(){

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
    };

    Container1 c1 = new Container1(10,"ten");
    Container1 c2 = codec.deepCopy(c1);
    System.out.println("testContainer1: "+c2.f1+" "+c2.f2);

  }


  public static void testContainer3(){
    BCodec<Container3> codec = new RecordCodec<Container3>(Container3.class);
    Container3 c1 = new Container3(10, "ten");
    Container3 c2 = codec.deepCopy(c1);
    System.out.println("testContainer3: "+c2.f1+" "+c2.f2);
  }




}
