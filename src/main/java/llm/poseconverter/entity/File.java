package llm.poseconverter.entity;

import java.time.ZonedDateTime;

public class File {
    private String prefix;
    private String fileName;
    private long fileSize;
    private ZonedDateTime lastModified;
    private String url;
    private boolean isDir;

    public File(String fileName, long fileSize, ZonedDateTime lastModified, String url, boolean isDir, String prefix) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.url = url;
        this.isDir = isDir;
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean getIsDir() {
        return isDir;
    }
    public void setDir(boolean isDir) {
        this.isDir = isDir;
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
