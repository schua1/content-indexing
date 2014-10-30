package org.deshang.content.indexing.util.jdbc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public abstract class AbstractRowMapper<T> implements RowMapper<T> {

    protected String getBlobContent(Blob blob) throws SQLException {

        String blobContent = null;
        try {
            InputStream in = blob.getBinaryStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int data = -1;
            while ((data = bis.read()) != -1) {
                baos.write(data);
            }
            blobContent = baos.toString("UTF-8");
        } catch (IOException e) {
            throw new SQLException("Can't read blob conent", e);
        }

        return blobContent;
    }
}
