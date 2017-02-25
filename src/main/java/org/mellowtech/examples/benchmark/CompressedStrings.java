package org.mellowtech.examples.benchmark;

import org.mellowtech.core.codec.ZString;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Created by msvens on 28/12/15.
 */
public class CompressedStrings {


  public static void main(String[] args){
    generateCompressedStrings();
  }

  public static void generateCompressedStrings(){
    try {
      FileChannel fc = FileChannel.open(Paths.get("/tmp/english.1024MB"));
      ByteBuffer buffer = ByteBuffer.allocate(10*1024);
      int totSize = 0, compSize = 0;
      int i = 0;
      while(fc.read(buffer) != -1){
        //System.out.println(i);
        buffer.flip();
        totSize += buffer.remaining();
        String str = new String(buffer.array(),0, buffer.remaining(), StandardCharsets.ISO_8859_1);
        ZString comp = new ZString(str);
        compSize += comp.getCompressedData().length;
        buffer.clear();
      }
      System.out.println(compSize/(1024*1024)+" "+totSize/(1024*1024));
    } catch(Exception e){
      throw new Error(e);
    }
  }

  public static void compressEntireFile(){

  }

}
