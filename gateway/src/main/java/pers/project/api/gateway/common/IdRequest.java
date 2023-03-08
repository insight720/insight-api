package pers.project.api.gateway.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ID 请求
 *
 * @author Luo Fei
 * @date 2023/2/26
 */
@Data
public class IdRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}
