package sjes.dzfp.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage<T> implements Serializable {

    private int code = 0;

    private String message;

    private T data;

    public static <T> ResponseMessage defaultSuccess(T data) {
        return new ResponseMessage(1, "", data);
    }

    public static <T> ResponseMessage success(int code, T data) {
        return new ResponseMessage(code, "", data);
    }

    public void setSuccess(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public void setFailure(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> ResponseMessage success(int code, String message) {
        return new ResponseMessage(code, message, null);
    }

    public static <T> ResponseMessage defaultFailure(String message, T data) {
        return new ResponseMessage(0, message, data);
    }

    public static <T> ResponseMessage defaultFailure(T data) {
        return new ResponseMessage(0, "", data);
    }

    public static <T> ResponseMessage failure(int code, String message, T data) {
        return new ResponseMessage(code, message, data);
    }
}
