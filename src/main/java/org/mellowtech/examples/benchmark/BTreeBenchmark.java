package org.mellowtech.examples.benchmark;

import com.google.common.base.Stopwatch;
import org.mellowtech.core.bytestorable.CBByteArray;
import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.collections.BTree;
import org.mellowtech.core.collections.BTreeBuilder;
import org.mellowtech.core.collections.KeyValue;
import org.mellowtech.core.util.DelDir;
import org.mellowtech.core.util.TGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

/**
 * Created by msvens on 30/12/15.
 */
public class BTreeBenchmark {

  private CBString[] keys;
  private CBByteArray value;
  Random r = new Random();
  private BTree <String, CBString, byte[], CBByteArray> tree = null;
  int size, keySize, valueSize;
  private BTreeBuilder builder;

  public static Path rootDir = Paths.get("/Users/msvens/benchmark");

  public static void main(String[] args){
    //memory mapped index, not memory-mapped
    System.out.println("memory mapped index key-values not memory mapped");
    BTreeBuilder builder = new BTreeBuilder().indexInMemory(true);
    BTreeBenchmark bm = new BTreeBenchmark(1000000, builder, 16, 100);
    bm.runBenchmark();

    //index and keyvalues both memory mapped
    System.out.println("\nmemory mapped index key-values not memory mapped\n");
    builder = new BTreeBuilder().indexInMemory(true).valuesInMemory(true);
    bm = new BTreeBenchmark(1000000, builder, 16, 100);
    bm.runBenchmark();

    //
    System.out.println("\nindex and keyvalues not memory mapped\n");
    builder = new BTreeBuilder().indexInMemory(false).valuesInMemory(false);
    bm = new BTreeBenchmark(1000000, builder, 16, 100);
    bm.runBenchmark();


  }


  public BTreeBenchmark(int size, BTreeBuilder builder, int sizeOfKey, int sizeOfValue) {
    this.builder = builder;
    this.size = size;
    this.keySize = sizeOfKey;
    this.valueSize = sizeOfValue;
    keys = new CBString[size];
    Iterator<String> iter = TGenerator.of(String.class, sizeOfKey, 'a', 'f', false);
    for(int i = 0; i < size; i++){
      keys[i] = new CBString(iter.next());
    }
    value = new CBByteArray(TGenerator.randomStr(r, sizeOfValue, 'A', 'Z').getBytes());
    try {
      DelDir.d(rootDir.toString());
      Files.createDirectories(rootDir);
    }
    catch(Exception e){
      throw new Error(e);
    }
  }

  private Stopwatch startTest(){
    return Stopwatch.createStarted();
  }

  private void stopTest(Stopwatch sw, String text, int operations){
    sw.stop();
    long l = sw.elapsed(TimeUnit.MILLISECONDS);
    long opsPerM = operations / l;
    System.out.format("%s (keySize=%d, valueSize=%d) took %d milliseconds with %d operations per milli and %d operations per sec%n", text, keySize, valueSize, l, opsPerM, opsPerM*1000);
  }

  public void runBenchmark(){
    writeBatch();
    writeSorted();
    writeRandom();
    readRandom();
    readSequential();
  }

  public void writeSorted(){
    try {
      Path p = rootDir.resolve("btreeSorted");
      BTree<String, CBString, byte[], CBByteArray> tree = builder.build(CBString.class, CBByteArray.class, p.toString());
      Stopwatch sw = startTest();
      for(int i = 0; i < size; i++){
        tree.put(keys[i], value);
      }
      stopTest(sw, "writeSorted", size);
      tree.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public void writeBatch(){
    try {
      Path p = rootDir.resolve("btreeBatch");
      BTree<String, CBString, byte[], CBByteArray> tree = builder.build(CBString.class, CBByteArray.class, p.toString());
      Stopwatch sw = startTest();
      tree.createIndex(new Iterator<KeyValue<CBString, CBByteArray>>() {
        int curr = 0;
        @Override
        public boolean hasNext() {
          return curr < size;
        }
        @Override
        public KeyValue<CBString, CBByteArray> next() {
          return new KeyValue<CBString, CBByteArray>(keys[curr++], value);
        }
      });
      stopTest(sw, "writeBatch", size);
      tree.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public void writeRandom(){
    try {
      Path p = rootDir.resolve("btreeRandom");
      BTree<String, CBString, byte[], CBByteArray> tree = builder.build(CBString.class, CBByteArray.class, p.toString());
      shuffleArray(r, keys);
      Stopwatch sw = startTest();
      for(int i = 0; i < size; i++){
        tree.put(keys[i], value);
      }
      stopTest(sw, "writeRandom", size);
      tree.save();
      this.tree = tree;
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public void readRandom(){
    try {
      Stopwatch sw = startTest();
      for (int i = 0; i < size; i++) {
        if(tree.get(keys[i]) == null)
          System.out.println("error");
      }
      stopTest(sw, "readRandom", size);
    } catch(Exception e){
      throw new Error(e);
    }
  }

  public void readSequential(){
    try {
      Stopwatch sw = startTest();
      Iterator <KeyValue<CBString,CBByteArray>> iter = tree.iterator();
      while(iter.hasNext()){
        iter.next();
      }
      stopTest(sw, "readSequential", size);
    } catch(Exception e){
      throw new Error(e);
    }
  }

  public static void shuffleArray(Random r, CBString[] ar) {
    // If running on Java 6 or older, use `new Random()` on RHS here
    //Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--)
    {
      int index = r.nextInt(i + 1);
      // Simple swap
      CBString a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  public static void shuffleArray(Random r, String[] ar) {
    // If running on Java 6 or older, use `new Random()` on RHS here
    //Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--)
    {
      int index = r.nextInt(i + 1);
      // Simple swap
      String a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }
}
