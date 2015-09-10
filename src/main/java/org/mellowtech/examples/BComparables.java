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

import org.mellowtech.core.bytestorable.CBString;

/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class BComparables {

  public static void main(String[] args){
    compareStrings();
    compareInSameBuffer();

  }

  /**
   * Compare two strings on a byte level. CBString encodes in UTF8
   */
  public static void compareStrings(){
	  ByteBuffer str1 = new CBString("a string").to();
	  ByteBuffer str2 = new CBString("a string").to();
	  System.out.println(new CBString().byteCompare(0, str1, 0, str2));
  }

  /**
   * Compare two strings on a byte level that are stored in the same buffer. CBString encodes in UTF8
   */
  public static void compareInSameBuffer(){
    CBString str1 = new CBString("a string 1");
    CBString str2 = new CBString("a string");
    ByteBuffer bb = ByteBuffer.wrap(new byte[str1.byteSize()+str2.byteSize()]);
    str1.to(bb);
    str2.to(bb);
    System.out.println(new CBString().byteCompare(0, str1.byteSize(), bb));

  }
  
  




}
