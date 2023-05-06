package llm.poseconverter.entity;

import java.time.ZonedDateTime;

public class File {
    private String fileName;
    private long fileSize;
    private ZonedDateTime lastModified;
    private String url;

    public File(String fileName, long fileSize, ZonedDateTime lastModified, String url) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.url = url;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public ZonedDateTime getLastModified() {
        return lastModified;
    }
    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
}
