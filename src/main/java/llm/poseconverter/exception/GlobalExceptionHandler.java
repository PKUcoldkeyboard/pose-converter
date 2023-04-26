package llm.poseconverter.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常捕获
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // 通用异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SaResult error(Exception e) {
        return SaResult.error();
    }

    // 自定义异常处理
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public SaResult error(CustomException e) {
        return SaResult.error(e.getMessage());
    }
}
