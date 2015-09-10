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

import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.io.Record;
import org.mellowtech.core.io.RecordFile;
import org.mellowtech.core.io.RecordFileBuilder;

/**
 * Date: 2013-04-14
 * Time: 09:05
 *
 * @author Martin Svensson
 */
public class Example2 {

  public static void main(String[] args) throws Exception{
    System.out.println(Math.ceil(3/(double)2));
    memFile();

  }
  
  public static void memFile() throws Exception{
    RecordFileBuilder builder = new RecordFileBuilder();
    builder.blockSize(2048).mem().reserve(0).maxBlocks(1024*1024);
    RecordFile rf = builder.build("/tmp/myfile.rf");
    
    byte[] b = new CBString("some string").to().array();
    rf.insert(100000, b);
    rf.insert(512, b);
    
    b = rf.get(100000);
    CBString str = new CBString().from(b, 0);
    System.out.println(str.get());
    
    for(Iterator<Record>iter = rf.iterator(); iter.hasNext();){
      System.out.println("record: "+iter.next().record);
    }
    rf.close();
    //new File("/tmp/myfile.rf").delete();
  }
}
