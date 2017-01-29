/*


 * 
 * '
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


import java.io.File;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.mellowtech.core.codec.StringCodec;
import org.mellowtech.core.io.Record;
import org.mellowtech.core.io.RecordFile;
import org.mellowtech.core.io.RecordFileBuilder;

/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class IOs {

  public static void main(String[] args) throws Exception{
    memFile();
  }




  
  public static void memFile() throws Exception{

    StringCodec codec = new StringCodec();
    //create
    RecordFileBuilder builder = new RecordFileBuilder();
    builder.blockSize(2048).mem().reserve(0).maxBlocks(1024*1024);
    RecordFile rf = builder.build("/tmp/myfile.rf");
    System.out.println("free blocks: "+rf.getFreeBlocks());

    //insert
    byte[] b = codec.to("first string").array();
    byte[] b1 = codec.to("second string").array();
    rf.insert(100000, b);
    rf.insert(512, b1);

    //get
    b = rf.get(100000);
    String str = codec.from(b, 0);
    System.out.println(str);

    //iterate
    String tmp;
    for(Iterator<Record>iter = rf.iterator(); iter.hasNext();){
      Record r = iter.next();
      tmp = codec.from(r.data,0);
      System.out.println("record: "+r.record+" "+tmp);
    }

    //finally close and delete
    rf.close();
    new File("/tmp/myfile.rf").delete();
  }
}
