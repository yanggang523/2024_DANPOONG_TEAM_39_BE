package com.example._2024_danpoong_team_39_be.login;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> BaseResponse<T> onSuccess(T data) {
        return new BaseResponse<>(true, data, "Success");
    }

    public static <T> BaseResponse<T> onError(String message) {
        return new BaseResponse<>(false, null, message);
    }
}

