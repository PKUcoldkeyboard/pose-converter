package llm.poseconverter.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("pc_videos")
public class Video {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_name")
    private String videoName;

    @TableField("video_path")
    private String videoPath;

    @TableField("status")
    private int status;

    @TableField("video_length")
    private int videoLength;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
}
