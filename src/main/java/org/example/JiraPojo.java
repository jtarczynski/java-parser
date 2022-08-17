package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class JiraPojo {

    private String title;

    private String priority;

    private String key;

    @JsonProperty("key")
    private void unpackKey(Map<String, Object> key) {
        this.key = (String) key.get("content");
    }

    @JsonProperty("priority")
    private void unpackPriority(Map<String, Object> priority) {
        this.priority = (String) priority.get("content");
    }
}
