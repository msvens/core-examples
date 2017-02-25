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


import org.mellowtech.core.codec.IntCodec;
import org.mellowtech.core.codec.MixedListCodec;
import org.mellowtech.core.codec.RecordCodec;
import org.mellowtech.core.codec.StringCodec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class BCodecs {



  public static void main(String[] args){
    serialize();
    list();
    testContainer1();
    testContainer3();
    compareStrings();
    compareInSameBuffer();
  }

  public static void serialize(){
    IntCodec codec = new IntCodec();
    int first = 1;
    ByteBuffer bb = codec.to(1);
    bb.flip();
    Integer second = codec.from(bb);
    System.out.println("serialize: "+first+" "+second);
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

    bb.flip();
    list = codec.from(bb);
    Integer first = (Integer) list.get(0);
    String second = (String) list.get(1);
    Long third = (Long) list.get(2);
    Boolean forth = (Boolean) list.get(3);
    System.out.println("list: "+first+" "+second+" "+third+" "+forth);
  }

  public static void testContainer1(){
    Container1 c1 = new Container1(10,"ten");
    Container1Codec codec = new Container1Codec();
    Container1 c2 = codec.deepCopy(c1);
    System.out.println("testContainer1: "+c2.f1+" "+c2.f2);
  }

  public static void testContainer3(){
    Container3 c1 = new Container3(10, "ten");
    RecordCodec<Container3> codec = new RecordCodec<>(Container3.class);
    Container3 c2 = codec.deepCopy(c1);
    System.out.println("testContainer3: "+c2.f1+" "+c2.f2);
  }

  /**
   * Compare two strings on a byte level. StringCodec encodes in UTF8
   */
  public static void compareStrings(){
    StringCodec codec = new StringCodec();
	  ByteBuffer str1 = codec.to("a string");
	  ByteBuffer str2 = codec.to("a string");
	  System.out.println("compareStrings: "+codec.byteCompare(0, str1, 0, str2));
  }

  /**
   * Compare two strings on a byte level that are stored in the same buffer. StringCodec encodes in UTF8
   */
  public static void compareInSameBuffer(){
    StringCodec codec = new StringCodec();
    ByteBuffer bb = ByteBuffer.wrap(new byte[codec.byteSize("a string 1")+codec.byteSize("a string")]);
    codec.to("a string 1", bb);
    codec.to("a string", bb);
    System.out.println("compareInSameBuffer: "+codec.byteCompare(0, codec.byteSize("a string 1"), bb));
  }
  
  




}
