package org.mellowtech.examples;

import org.mellowtech.core.codec.IntCodec;
import org.mellowtech.core.codec.StringCodec;
import org.mellowtech.core.codec.io.CodecInputStream;
import org.mellowtech.core.collections.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by msvens on 09/09/15.
 */
public class DiscCollections {

  public static void main(String[] args) throws Exception{
    //createIndex();
    //iterateTree();
  }

  public static void createTreeAndHashMaps() throws Exception {
    StringCodec strCodec = new StringCodec();
    IntCodec intCodec = new IntCodec();
    Path dir = Paths.get("/tmp");
    BTreeBuilder builder = new BTreeBuilder();
    BTree<String, Integer> db;
    db = builder.memoryMappedValues(true).build(strCodec, intCodec, dir, "treemap");

    EHTableBuilder ehbuilder = new EHTableBuilder();
    BMap<String, String> db1;
    db1 = ehbuilder.inMemory(true).blobValues(true).build(strCodec, strCodec,"/tmp/hashmap");
  }

  public static void createDiscBasedMaps() throws Exception {
    DiscMapBuilder builder = new DiscMapBuilder();
    builder.blobValues(false);

    SortedDiscMap <String, Integer> db = builder.blobValues(false).sorted(String.class, Integer.class, "/tmp/discbasedmap");
    DiscMap <String, String> db1 = builder.blobValues(true).hashed(String.class, String.class, "tmp/hashbasedmap");

    //or more generically (in which case you would have to cast to SortedDiscMap)
    db = (SortedDiscMap<String, Integer>) builder.blobValues(false).build(String.class, Integer.class, "/tmp/discbasedmap", true);
    db1 = builder.blobValues(true).build(String.class, String.class, "tmp/hashbasedmap", false);
  }

  public static void createIndex() throws Exception {
    StringCodec strCodec = new StringCodec();
    IntCodec intCodec = new IntCodec();
    BTreeBuilder builder = new BTreeBuilder();
    BTree<String, Integer> tree;
    Path dir = Paths.get("/tmp/btree");
    tree = builder.build(strCodec, intCodec, dir, "english");
    CodecInputStream<String> sis = new CodecInputStream<>(new FileInputStream("/tmp/english-sorted.bs"), strCodec);
    WordCountIter iter = new WordCountIter(sis);
    tree.createTree(iter);
    tree.close();
  }

  public static void iterateTree() throws Exception {
    BTreeBuilder builder = new BTreeBuilder();
    BTree <String, Integer> tree;
    Path dir = Paths.get("/tmp/btree");
    tree = builder.build(new StringCodec(), new IntCodec(), dir, "english");
    Iterator <KeyValue<String, Integer>> iter = tree.iterator();
    while(iter.hasNext()){
      KeyValue <String, Integer> kv = iter.next();
      System.out.println(kv.getKey()+": "+kv.getValue());
    }
  }

  static class WordCountIter implements Iterator<KeyValue<String,Integer>> {

    CodecInputStream<String> sis;
    KeyValue<String, Integer> next = null;
    String nextWord = null;
    String prev = null;

    public WordCountIter(CodecInputStream<String> sis) {
      this.sis = sis;
      next = new KeyValue<>();
      try {
        nextWord = sis.next();
        prev = nextWord;
      } catch (IOException e) {
        throw new Error("could not read");
      }
      getNext();
    }

    private void getNext() {
      if (nextWord == null) {
        next = null;
        return;
      }
      next = new KeyValue<>();
      int count = 1;
      next.setKey(nextWord);
      try {
        while (true) {
          nextWord = sis.next();
          if (nextWord == null || nextWord.compareTo(next.getKey()) != 0) {
            break;
          }
          count++;
          prev = nextWord;
        }
      } catch (IOException e) {
        throw new Error("could not read");
      }
      next.setValue(count);
    }

    public boolean hasNext() {
      return nextWord != null;
    }

    public KeyValue<String, Integer> next() {
      KeyValue<String, Integer> tmp = next;
      getNext();
      return tmp;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }


}
