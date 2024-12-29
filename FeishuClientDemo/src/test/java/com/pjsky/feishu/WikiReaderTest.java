package com.pjsky.feishu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WikiReaderTest {

    @BeforeEach
    public void init() {
        WikiReader.init();
    }

    @Test
    public void testListSpace() throws Exception {
        WikiReader.listSpace();
    }   

    @Test
    public void testListSpaceNode() throws Exception {    
        String spaceId = "7438065836432179204"; // 知识空间ID
        WikiReader.listSpaceNode(spaceId);    
    }

    @Test
    public void testGetSpaceNode() throws Exception {
        String nodeToken = "B22IwAbgciTuTjkKF70cS62mnPh"; //node_token
        WikiReader.getSpaceNode(nodeToken);
    }

    @Test
    public void testDownloadFile() throws Exception {
        // 知识库节点的obj_type为 file 时，obj_token为 file_token
        String fileToken = "LhY6bfBOAob8NOx6ywCcQ2ISnNg"; //obj_token
        WikiReader.downloadFile(fileToken);
    }

    @Test
    public void testExportWordSync() throws Exception {
        String objToken = "EwCsdGaDQomgZ4xdgXAchQ4mnkG"; //obj_token
        String objType = "docx"; //doc, docx, sheet, bitable
        String fileExtension = "docx"; //docx, pdf, xlsx, csv
        WikiReader.exportWordSync(objToken, objType, fileExtension);
    }

    @Test
    public void testListDocumentBlock() throws Exception {
        String documentId = "EwCsdGaDQomgZ4xdgXAchQ4mnkG"; //obj_token
        WikiReader.listDocumentBlock(documentId);
    }

    @Test
    public void testDownloadMedia() throws Exception {
        String fileToken = "EKlxb3gvio5TtEx5hxUcjSkrndd"; //obj_token
        WikiReader.downloadMedia(fileToken);
    }
    
    
}
