package llm.poseconverter.exception;

import java.net.ConnectException;
import java.util.stream.Collectors;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import io.minio.errors.MinioException;

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

    @ExceptionHandler(ConnectException.class)
    @ResponseBody
    public SaResult error(ConnectException e) {
        return SaResult.error("连接服务器失败");
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

    @ExceptionHandler(MinioException.class)
	@ResponseBody
	public SaResult error(MinioException e) {
		return SaResult.error("文件客户端出错");
	}

    // 自定义异常处理
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public SaResult error(CustomException e) {
        return SaResult.error(e.getMessage());
    }
}
