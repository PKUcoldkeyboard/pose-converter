package llm.poseconverter.entity;

import java.time.ZonedDateTime;
import java.util.List;

public class Directory extends File {
    private List<File> children;

    public Directory(String objectName, String url, List<File> children) {
        super(objectName, 0, ZonedDateTime.now(), url, true);
        this.children = children;
    }

    public List<File> getChildren() {
        return children;
    }

    public void setChildren(List<File> children) {
        this.children = children;
    }

}
