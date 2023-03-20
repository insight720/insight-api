package pers.project.api.facade.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表【api_digest (接口摘要) 】的数据 Entity
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "api_digest")
public class ApiDigest {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建账户主键
     */
    private Long accountId;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求方法
     */
    private Integer method;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口状态
     */
    private Integer apiStatus;

    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}