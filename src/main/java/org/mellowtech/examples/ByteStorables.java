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

import org.mellowtech.core.bytestorable.CBInt;
import org.mellowtech.core.bytestorable.CBMixedList;

/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class ByteStorables {

  public static void main(String[] args){
    serialize();
    list();
    testContainer1();
    testContainer2();
    testContainer3();
  }

  public static void serialize(){
    CBInt firstInt = new CBInt(1);
    ByteBuffer bb = firstInt.to();
    bb.flip();
    CBInt secondInt = firstInt.from(bb);
    System.out.println("serialize: "+firstInt.get()+" "+secondInt.get());
  }

  public static void list(){
    CBMixedList list = new CBMixedList();
    list.add(1);
    list.add("a string");
    list.add(new Long(100));
    list.add(true);

    ByteBuffer bb = list.to();
    list.clear();

    //don't create a new object
    bb.flip();
    list = list.from(bb);
    Integer first = (Integer) list.get(0);
    String second = (String) list.get(1);
    Long third = (Long) list.get(2);
    Boolean forth = (Boolean) list.get(3);
    System.out.println("list: "+first+" "+second+" "+third+" "+forth);
  }

  public static void testContainer1(){
    Container1 c1 = new Container1(10,"ten");
    Container1 c2 = c1.deepCopy();
    System.out.println("testContainer1: "+c2.f1+" "+c2.f2);
  }

  public static void testContainer2(){
    Container2 c1 = new Container2(10, "ten");
    //c1.f1 = 10; c1.f2 = "ten";
    Container2 c2 = c1.deepCopy();
    System.out.println("testContainer2: "+ c1.f2+" "+c2.f2);
  }

  public static void testContainer3(){
    Container3 c1 = new Container3();
    c1.get().f1 = 10;
    c1.get().f2 = "ten";
    Container3 c2 = c1.deepCopy();
    System.out.println("testContainer3: "+c2.get().f1+" "+c2.get().f2);
  }




}
