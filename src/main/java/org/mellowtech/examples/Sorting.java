package org.mellowtech.examples;

import org.mellowtech.core.codec.StringCodec;
import org.mellowtech.core.codec.io.CodecInputStream;
import org.mellowtech.core.codec.io.CodecOutputStream;
import org.mellowtech.core.sort.EDiscBasedSort;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by msvens on 09/09/15.
 */
public class Sorting {

  public static void main(String[] args) throws Exception{
    //testPipe();
    testScannerInput();
    //parse();sort();
    //parseAndSort();
    //verify();
  }

  public static void parse() throws Exception{
    Pattern p = Pattern.compile("[\\s|\\p{Punct}]+");
    InputStream is = new GZIPInputStream(new FileInputStream("/tmp/english.1024MB.gz"));
    Scanner s = new Scanner(is);
    s.useDelimiter(p);
    StringCodec stringCodec = new StringCodec();
    CodecOutputStream sos = new CodecOutputStream(new FileOutputStream("/tmp/english.1024MB.bs"), stringCodec);
    int i = 0;
    while(s.hasNext()){
      String n = s.next();
      if(n.length() > 1){
        sos.write(n);
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
    edb.sort(new ScannerCodecInputStream(s,1), os, 1024*1024*160);
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

  public static void testScannerInput() throws Exception {
    Pattern p = Pattern.compile("[\\s|\\p{Punct}]+");
    InputStream is = new GZIPInputStream(new FileInputStream("/tmp/english.1024MB.gz"));
    Scanner s = new Scanner(is);
    s.useDelimiter(p);
    ScannerCodecInputStream scis = new ScannerCodecInputStream(s);
    CodecInputStream<String> cis = new CodecInputStream<>(scis,new StringCodec());
    String temp;
    int i = 0;
    while((temp = cis.next()) != null){
      i++;
      if((i % 10000000) == 0)
        System.out.println(i);
    }
  }

  public static void testPipe() throws Exception {
    Pattern p = Pattern.compile("[\\s|\\p{Punct}]+");
    InputStream is = new GZIPInputStream(new FileInputStream("/tmp/english.1024MB.gz"));
    Scanner s = new Scanner(is);
    s.useDelimiter(p);
    PipedOutputStream pos = new PipedOutputStream();
    PipedInputStream pis = new PipedInputStream(pos);
    final CodecOutputStream<String> cos = new CodecOutputStream<>(pos, new StringCodec());
    final CodecInputStream<String> cis = new CodecInputStream<>(pis, new StringCodec());

    Runnable runOutput = new Runnable() {
      @Override
      public void run() {
        while(s.hasNext()){
          try {
            cos.write(s.next());
          } catch (IOException e) {
              e.printStackTrace();
              return;
          }
        }
      }
    };

    Runnable runInput = new Runnable() {
      @Override
      public void run() {
        String next = null;
        int i = 0;
        try {
          while ((next = cis.next()) != null) {
            i++;
            if((i % 10000000) == 0)
              System.out.println(i);
          }
        } catch(IOException e){
          e.printStackTrace();
          return;
        }
      }
    };

    Thread outputThread = new Thread(runOutput,"outThread");
    Thread inputThread = new Thread(runInput, "inThread");
    outputThread.start();
    inputThread.start();

  }



}
