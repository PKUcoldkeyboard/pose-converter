package llm.poseconverter.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("pc_blender")
public class Blender {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 外键
    @TableField("user_id")
    private Long userId;
    
    @TableField("blender_name")
    private String bleander_name;

    @TableField("blender_path")
    private String bleander_path;

    @TableField("status")
    private int status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBleander_name() {
        return bleander_name;
    }

    public void setBleander_name(String bleander_name) {
        this.bleander_name = bleander_name;
    }

    public String getBleander_path() {
        return bleander_path;
    }

    public void setBleander_path(String bleander_path) {
        this.bleander_path = bleander_path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bleander_name == null) ? 0 : bleander_name.hashCode());
        result = prime * result + ((bleander_path == null) ? 0 : bleander_path.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + status;
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Blender other = (Blender) obj;
        if (bleander_name == null) {
            if (other.bleander_name != null)
                return false;
        } else if (!bleander_name.equals(other.bleander_name))
            return false;
        if (bleander_path == null) {
            if (other.bleander_path != null)
                return false;
        } else if (!bleander_path.equals(other.bleander_path))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (status != other.status)
            return false;
        if (updatedAt == null) {
            if (other.updatedAt != null)
                return false;
        } else if (!updatedAt.equals(other.updatedAt))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    
}
