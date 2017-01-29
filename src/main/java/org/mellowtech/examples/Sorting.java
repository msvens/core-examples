package org.mellowtech.examples;

import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.bytestorable.io.ScannerInputStream;
import org.mellowtech.core.bytestorable.io.StorableInputStream;
import org.mellowtech.core.bytestorable.io.StorableOutputStream;
import org.mellowtech.core.codec.StringCodec;
import org.mellowtech.core.codec.io.CodecInputStream;
import org.mellowtech.core.sort.EDiscBasedSort;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by msvens on 09/09/15.
 */
public class Sorting {

  public static void main(String[] args) throws Exception{
    //parse();sort();
    //parseAndSort();
    //verify();
  }

  public static void parse() throws Exception{
    Pattern p = Pattern.compile("[\\s|\\p{Punct}]+");
    InputStream is = new GZIPInputStream(new FileInputStream("/tmp/english.1024MB.gz"));
    Scanner s = new Scanner(is);
    s.useDelimiter(p);
    CBString tmp;
    StorableOutputStream sos = new StorableOutputStream(new FileOutputStream("/tmp/english.1024MB.bs"));
    int i = 0;
    while(s.hasNext()){
      String n = s.next();
      if(n.length() > 1){
        tmp = new CBString(n);
        sos.write(tmp);
        i++;
      }
      if(i % 1000000 == 0)
        System.out.println(i);
    }
    sos.flush();
  }

  public static void eSort() throws Exception {
    long l = System.currentTimeMillis();
    EDiscBasedSort<String> edb = new EDiscBasedSort <>(new StringCodec(), "/tmp");
    edb.sort("/tmp/english.1024MB.bs", "/tmp/english-sorted.bs", 1024*1024*160);
    System.out.println("esort took: "+ (System.currentTimeMillis() - l) + "ms");
  }

  public static void parseAndSort() throws Exception {
    Pattern p = Pattern.compile("[\\s|\\p{Punct}]+");
    InputStream is = new GZIPInputStream(new FileInputStream("/tmp/english.1024MB.gz"));
    Scanner s = new Scanner(is);
    s.useDelimiter(p);
    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream("/tmp/english-sorted-1.bs"),1024*1024);
    EDiscBasedSort <String> edb = new EDiscBasedSort <>(new StringCodec(), "/tmp");
    edb.sort(new ScannerInputStream(s,1), os, 1024*1024*160);
    os.close();
  }

  public static void verify() throws Exception {
    CodecInputStream<String> sis = new CodecInputStream<>(new FileInputStream("/tmp/english-sorted.bs"), new StringCodec());
    String prev = sis.next();
    String next = null;
    while((next = sis.next()) != null){
      if(prev.compareTo(next) > 0){
        System.out.println("previous word greater than next "+prev+" "+next);
      }
      prev = next;
    }
  }

}
