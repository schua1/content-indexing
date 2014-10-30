/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;

public class LuceneIndexUtil {

    public static final String LUCENE_INDEX_FIELD_SITE = "site";
    public static final String LUCENE_INDEX_FIELD_ID = "id";
    public static final String LUCENE_INDEX_FIELD_AUTHOR = "author";
    public static final String LUCENE_INDEX_FIELD_CONTENT = "content";
    public static final String LUCENE_INDEX_FIELD_TITLE = "title";

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndexUtil.class);

    private static class AnalyzerHolder {
        static final Analyzer analyzer = new ComplexAnalyzer();
    }

    public Analyzer getAnalyzer() {
        return AnalyzerHolder.analyzer;
    }

    public IndexWriterConfig getIndexWriterConfig() {
        return new IndexWriterConfig(Version.LUCENE_43, getAnalyzer());
    }

    public IndexWriter buildRAMIndexWriter() throws IOException {
        LOGGER.debug("Enter buildRAMIndexWriter()");

        Directory index = new RAMDirectory();
        IndexWriter writer = new IndexWriter(index, getIndexWriterConfig());

        LOGGER.debug("Exit buildRAMIndexWriter()");
        return writer;
    }

    public Document covertWpPost2Document(WpPost post, long siteId) {
       LOGGER.debug("Enter covertWpPost2Document(WpPost, long)");
       Document doc = new Document();
       
       doc.add(new StringField(LUCENE_INDEX_FIELD_AUTHOR, post.getAuthorUser().getLoginName(), Field.Store.YES));
       doc.add(new LongField(LUCENE_INDEX_FIELD_ID, post.getId(), Field.Store.YES));
       doc.add(new LongField(LUCENE_INDEX_FIELD_SITE, siteId, Field.Store.YES));
       doc.add(new TextField(LUCENE_INDEX_FIELD_TITLE, post.getTitle(), Field.Store.YES));
       doc.add(new TextField(LUCENE_INDEX_FIELD_CONTENT, post.getContent(), Field.Store.YES));

       LOGGER.debug("Exit covertWpPost2Document(WpPost, long)");
       return doc;
    }

    public Document covertWpComment2Document(WpComment comment, long siteId) {
        LOGGER.debug("Enter covertWpComment2Document(WpComment, long)");
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

        LOGGER.debug("Exit covertWpComment2Document(WpComment, long)");
        return doc;
    }

    public synchronized void storeIndex(Directory index, String path) throws IOException {
        LOGGER.debug("Enter storeIndex(Directory, String)");

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
        LOGGER.debug("Exit storeIndex(Directory, String)");
    }

    private void deleteFile(File dir) {
        LOGGER.debug("Enter deleteFile(File)");
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length > 0) {
                for (File temp : files) {
                    deleteFile(temp);
                }
            }
        }
        dir.delete();
        LOGGER.debug("Exit deleteFile(File)");
    }
}
