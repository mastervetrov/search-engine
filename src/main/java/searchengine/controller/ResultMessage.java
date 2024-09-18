package searchengine.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ResultMessage {
    private String result;
    private String error;
    public ResultMessage(String result) {
        this.result = result;
    }
}
