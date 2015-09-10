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

import org.mellowtech.core.bytestorable.BStorable;
import org.mellowtech.core.bytestorable.CBInt;
import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.bytestorable.CBUtil;

/**
 * Date: 2013-04-14
 * Time: 12:08
 *
 * @author Martin Svensson
 */
//START SNIPPET: c1-class
public class Container1 implements BStorable <Container1, Container1> {

  public CBInt f1 = new CBInt();
  public CBString f2 = new CBString();

  public Container1(){;}

  public Container1(Integer field1, String field2){
    f1 = new CBInt(field1);
    f2 = new CBString(field2);
  }
  
  public Container1(CBInt field1, CBString field2){
    f1 = field1;
    f2 = field2;
  }

  @Override
  public Container1 from(ByteBuffer bb) {
    CBUtil.getSize(bb, true); //read past size indicator
    CBInt tmpInt = f1.from(bb);
    CBString tmpStr = f2.from(bb);
    return new Container1(tmpInt, tmpStr);
  }

  @Override
  public void to(ByteBuffer bb) {
    CBUtil.putSize(f1.byteSize()+f2.byteSize(), bb, true);
    f1.to(bb);
    f2.to(bb);
  }

  @Override
  public int byteSize() {
    return CBUtil.byteSize(f1.byteSize()+f2.byteSize(), true);
  }

  @Override
  public int byteSize(ByteBuffer bb) {
    return CBUtil.peekSize(bb, true);
  }
  
  @Override
  public Container1 get() {
    return this;
  }
}
//END SNIPPET: c1-class