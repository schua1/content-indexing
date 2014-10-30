package org.deshang.content.indexing.util.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.deshang.content.indexing.model.WpComment;
import org.deshang.content.indexing.model.WpPost;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;

public class LuceneIndexUtil {

    public static final String LUCENE_INDEX_FIELD_SITE = "site";
    public static final String LUCENE_INDEX_FIELD_ID = "id";
    public static final String LUCENE_INDEX_FIELD_AUTHOR = "author";
    public static final String LUCENE_INDEX_FIELD_CONTENT = "content";
    public static final String LUCENE_INDEX_FIELD_TITLE = "title";

    public Analyzer getAnalyzer() {
        return new ComplexAnalyzer();
    }

    public IndexWriterConfig getIndexWriterConfig() {
        return new IndexWriterConfig(Version.LUCENE_43, getAnalyzer());
    }

    public IndexWriter buildRAMIndexWriter() throws IOException {
        
        Directory index = new RAMDirectory();
        IndexWriter writer = new IndexWriter(index, getIndexWriterConfig());
        return writer;
    }

    public Document covertWpPost2Document(WpPost post, long siteId) {
       Document doc = new Document();
       
       doc.add(new StringField(LUCENE_INDEX_FIELD_AUTHOR, post.getAuthorUser().getLoginName(), Field.Store.YES));
       doc.add(new LongField(LUCENE_INDEX_FIELD_ID, post.getId(), Field.Store.YES));
       doc.add(new LongField(LUCENE_INDEX_FIELD_SITE, siteId, Field.Store.YES));
       doc.add(new TextField(LUCENE_INDEX_FIELD_TITLE, post.getTitle(), Field.Store.YES));
       doc.add(new TextField(LUCENE_INDEX_FIELD_CONTENT, post.getContent(), Field.Store.YES));

       return doc;
    }

    public Document covertWpComment2Document(WpComment comment, long siteId) {
        Document doc = new Document();
        
        if (comment.getAuthorUser() != null) {
            doc.add(new StringField(LUCENE_INDEX_FIELD_AUTHOR, comment.getAuthorUser().getLoginName(), Field.Store.YES));
        } else {
            doc.add(new StringField(LUCENE_INDEX_FIELD_AUTHOR, comment.getAuthor(), Field.Store.YES));
        }
        doc.add(new LongField(LUCENE_INDEX_FIELD_ID, comment.getId(), Field.Store.YES));
        doc.add(new LongField(LUCENE_INDEX_FIELD_SITE, siteId, Field.Store.YES));
        doc.add(new TextField(LUCENE_INDEX_FIELD_TITLE, comment.getParentPost().getTitle(), Field.Store.YES));
        doc.add(new TextField(LUCENE_INDEX_FIELD_CONTENT, comment.getContent(), Field.Store.YES));

        return doc;
    }

    public synchronized void storeIndex(Directory index, String path) throws IOException {

        File dir = new File(path);
        if (dir.exists() && !dir.isDirectory()) {
            throw new IOException("Specified path name [" + path + "] is not a directory");
        } else if (dir.exists()) {
            deleteFile(dir);
        }
        dir.mkdirs();

        FSDirectory fs = FSDirectory.open(dir);
        IndexWriter writer = new IndexWriter(fs, getIndexWriterConfig());
        writer.addIndexes(index);
        writer.close();
    }

    private void deleteFile(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length > 0) {
                for (File temp : files) {
                    deleteFile(temp);
                }
            }
        }
        dir.delete();
    }
}
