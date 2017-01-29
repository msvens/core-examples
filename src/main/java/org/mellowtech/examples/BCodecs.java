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


import org.mellowtech.core.codec.StringCodec;

import java.nio.ByteBuffer;


/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class BCodecs {

  public static void main(String[] args){
    compareStrings();
    compareInSameBuffer();

  }

  /**
   * Compare two strings on a byte level. StringCodec encodes in UTF8
   */
  public static void compareStrings(){
    StringCodec codec = new StringCodec();
	  ByteBuffer str1 = codec.to("a string");
	  ByteBuffer str2 = codec.to("a string");
	  System.out.println(codec.byteCompare(0, str1, 0, str2));
  }

  /**
   * Compare two strings on a byte level that are stored in the same buffer. StringCodec encodes in UTF8
   */
  public static void compareInSameBuffer(){
    StringCodec codec = new StringCodec();
    ByteBuffer bb = ByteBuffer.wrap(new byte[codec.byteSize("a string 1")+codec.byteSize("a string")]);
    codec.to("a string 1", bb);
    codec.to("a string", bb);
    System.out.println(codec.byteCompare(0, codec.byteSize("a string 1"), bb));

  }
  
  




}
