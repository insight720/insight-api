package pers.project.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应结果
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private String code;

    private String message;

    private T data;

}
