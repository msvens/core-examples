package org.mellowtech.examples;

import org.mellowtech.core.bytestorable.CBInt;
import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.bytestorable.io.StorableInputStream;
import org.mellowtech.core.collections.*;

import java.io.FileInputStream;
import java.io.IOException;
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
    BTreeBuilder builder = new BTreeBuilder();
    BTree<String, CBString, Integer, CBInt> db;
    db = builder.indexInMemory(true).valuesInMemory(true).build(CBString.class, CBInt.class, "/tmp/treemap");

    EHTableBuilder ehbuilder = new EHTableBuilder();
    BMap<String, CBString, String, CBString> db1;
    db1 = ehbuilder.inMemory(true).blobValues(true).build(CBString.class, CBString.class,"/tmp/hashmap");
  }

  public static void createDiscBasedMaps() throws Exception {
    DiscMapBuilder builder = new DiscMapBuilder();
    builder.blobValues(false).memMappedKeyBlocks(true);

    SortedDiscMap <String, Integer> db = builder.blobValues(false).sorted(String.class, Integer.class, "/tmp/discbasedmap");
    DiscMap <String, String> db1 = builder.blobValues(true).hashed(String.class, String.class, "tmp/hashbasedmap");

    //or more generically (in which case you would have to cast to SortedDiscMap)
    db = (SortedDiscMap<String, Integer>) builder.blobValues(false).build(String.class, Integer.class, "/tmp/discbasedmap", true);
    db1 = builder.blobValues(true).build(String.class, String.class, "tmp/hashbasedmap", false);
  }

  public static void createIndex() throws Exception {
    BTreeBuilder builder = new BTreeBuilder();
    BTree<String, CBString, Integer, CBInt> tree;
    tree = builder.indexInMemory(true).build(CBString.class, CBInt.class, "/tmp/btree/english");
    StorableInputStream <String, CBString> sis = new StorableInputStream <>(new FileInputStream("/tmp/english-sorted.bs"), new CBString());
    WordCountIter iter = new WordCountIter(sis);
    tree.createIndex(iter);
    tree.close();
  }

  public static void iterateTree() throws Exception {
    BTreeBuilder builder = new BTreeBuilder();
    BTree <String, CBString, Integer, CBInt> tree;
    tree = builder.indexInMemory(true).build(CBString.class, CBInt.class, "/tmp/btree/english");
    Iterator <KeyValue<CBString,CBInt>> iter = tree.iterator();
    while(iter.hasNext()){
      KeyValue <CBString, CBInt> kv = iter.next();
      System.out.println(kv.getKey()+": "+kv.getValue());
    }
  }

  static class WordCountIter implements Iterator<KeyValue<CBString, CBInt>> {

    StorableInputStream<String, CBString> sis;
    KeyValue<CBString, CBInt> next = null;
    CBString nextWord = null;
    CBString prev = null;

    public WordCountIter(StorableInputStream<String,CBString> sis) {
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
      next.setValue(new CBInt(count));
    }

    public boolean hasNext() {
      return nextWord != null;
    }

    public KeyValue<CBString, CBInt> next() {
      KeyValue<CBString, CBInt> tmp = next;
      getNext();
      return tmp;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }


}
