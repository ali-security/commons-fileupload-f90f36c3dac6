/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.fileupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Test;

/**
 * Unit tests for file count limit functionality.
 */
public class FileCountLimitTest extends FileUploadTestCase {

    private static final String REQUEST_WITH_THREE_FILES =
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"file1\"; filename=\"foo1.tab\"\r\n" +
        "Content-Type: text/whatever\r\n" +
        "\r\n" +
        "This is the content of the file 1\n" +
        "\r\n" +
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"file2\"; filename=\"foo2.tab\"\r\n" +
        "Content-Type: text/whatever\r\n" +
        "\r\n" +
        "This is the content of the file 2\n" +
        "\r\n" +
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"file3\"; filename=\"foo3.tab\"\r\n" +
        "Content-Type: text/whatever\r\n" +
        "\r\n" +
        "This is the content of the file 3\n" +
        "\r\n" +
        "-----1234--\r\n";

    @Test
    public void testFileCountLimitExceeded() throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setFileCountMax(2);

        HttpServletRequest req = new MockHttpServletRequest(
                REQUEST_WITH_THREE_FILES.getBytes("US-ASCII"), CONTENT_TYPE);
        try {
            upload.parseRequest(req);
            fail("Expected FileCountLimitExceededException");
        } catch (FileCountLimitExceededException e) {
            assertEquals(2, e.getLimit());
        }
    }

    @Test
    public void testFileCountLimitNotExceeded() throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setFileCountMax(5);

        HttpServletRequest req = new MockHttpServletRequest(
                REQUEST_WITH_THREE_FILES.getBytes("US-ASCII"), CONTENT_TYPE);
        List<FileItem> fileItems = upload.parseRequest(req);
        assertEquals(3, fileItems.size());
    }

    @Test
    public void testFileCountLimitDisabled() throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setFileCountMax(-1);

        HttpServletRequest req = new MockHttpServletRequest(
                REQUEST_WITH_THREE_FILES.getBytes("US-ASCII"), CONTENT_TYPE);
        List<FileItem> fileItems = upload.parseRequest(req);
        assertEquals(3, fileItems.size());
    }

    @Test
    public void testFileCountLimitExact() throws Exception {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setFileCountMax(3);

        HttpServletRequest req = new MockHttpServletRequest(
                REQUEST_WITH_THREE_FILES.getBytes("US-ASCII"), CONTENT_TYPE);
        List<FileItem> fileItems = upload.parseRequest(req);
        assertEquals(3, fileItems.size());
    }
}
