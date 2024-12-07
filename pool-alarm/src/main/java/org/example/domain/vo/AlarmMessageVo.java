package org.example.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 告警信息的vo类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmMessageVo {
    private String applicationName;
    private String ThreadPoolName;
    private String message;
    private Map<String,String> parameters;
}
