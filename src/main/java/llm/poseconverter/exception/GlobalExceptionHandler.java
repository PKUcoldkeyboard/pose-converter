package llm.poseconverter.exception;

import java.util.stream.Collectors;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常捕获
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 通用异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SaResult error(Exception e) {
        return SaResult.error(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
	@ResponseBody
	public SaResult error(BindException e) {
		return SaResult.error(e.getAllErrors().stream().map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining("，")));
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public SaResult error(NotLoginException e) {
        return SaResult.error("请先登录！").setCode(401);
    }

    // 自定义异常处理
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public SaResult error(CustomException e) {
        return SaResult.error(e.getMessage());
    }
}
