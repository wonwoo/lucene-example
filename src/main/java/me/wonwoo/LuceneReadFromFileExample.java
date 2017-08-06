package me.wonwoo;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by wonwoolee on 2017. 8. 6..
 */
public class LuceneReadFromFileExample {
  public static void main(String[] args) throws IOException, ParseException {

    StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
    String outputDir = "/Users/wonwoolee/IdeaProjects/lucene-example/src/main/resources/output";

    Directory directory = FSDirectory.open(Paths.get(outputDir));
    IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    
    try(IndexWriter writer = new IndexWriter(directory, config)){
      createDocument("1","wonwoo", "wonwoo@test.com", writer );
      createDocument("2","kevin", "kevin@test.com", writer );
      createDocument("3","lee wonwoo", "test@gmail.com", writer );
      writer.commit();
    }

    IndexReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    System.out.println("** generalQuerySearch **");

    QueryParser queryParser = new QueryParser("name", standardAnalyzer);
    Query query = queryParser.parse("wonwoo");
    TopDocs querySearch = searcher.search(query, 5);
    print(querySearch.scoreDocs, searcher);

    System.out.println(querySearch.totalHits);

    System.out.println("** wildcardQuerySearch **");

    Query wildcardQuery = new WildcardQuery(new Term("email", "*test*") );
    TopDocs wildcardQuerySearch = searcher.search(wildcardQuery, 5);
    print(wildcardQuerySearch.scoreDocs, searcher);
    System.out.println(wildcardQuerySearch.totalHits);

  }

  private static void print(ScoreDoc[] scoreDocs, IndexSearcher searcher) throws IOException {
    for(ScoreDoc scoreDoc : scoreDocs) {
      System.out.println("doc : " + scoreDoc.doc);
      System.out.println("score : " + scoreDoc.score);
      System.out.println("shardIndex" + scoreDoc.shardIndex);
      System.out.println("document : " + searcher.doc(scoreDoc.doc));
    }
  }

  private static void createDocument(String id, String name, String email, IndexWriter writer) throws IOException {
    Document document = new Document();
    document.add(new Field("_id", id, StoredField.TYPE));
    document.add(new TextField("name", name, Field.Store.YES));
    document.add(new TextField("email", email, Field.Store.YES));
    writer.addDocument(document);

  }

//  private static void index(final List<Document> docs, final IndexWriter indexWriter) throws IOException {
//    if (docs.size() > 1) {
//      indexWriter.addDocuments(docs);
//    } else {
//      indexWriter.addDocument(docs.get(0));
//    }
//  }
//
//  private IndexWriter createWriter(boolean create) throws IOException {
//    try {
//      final IndexWriterConfig iwc = getIndexWriterConfig(create);
//      return createWriter(store.directory(), iwc);
//    } catch (LockObtainFailedException ex) {
////      logger.warn("could not lock IndexWriter", ex);
//      throw ex;
//    }
//  }
//  // pkg-private for testing
//  IndexWriter createWriter(Directory directory, IndexWriterConfig iwc) throws IOException {
//    return new IndexWriter(directory, iwc);
//  }
//
//  private IndexWriterConfig getIndexWriterConfig(boolean create) {
//
//    StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
//    final IndexWriterConfig iwc = new IndexWriterConfig(standardAnalyzer);
//    iwc.setCommitOnClose(false); // we by default don't commit on close
//    iwc.setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND);
//    iwc.setUseCompoundFile(true); // always use compound on flush - reduces # of file-handles on refresh
//    return iwc;
//  }

}
