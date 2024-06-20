package com.kyle.spring.rest.starter;

import com.kyle.spring.rest.starter.exception.ParamErrorException;
import com.kyle.spring.rest.starter.infra.ProcessException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseController {
    private final static Logger LOG = LoggerFactory.getLogger(BaseController.class);
    public static final String ERR_CODE = "001";
    public static final String ERR_MSG = "未知错误";
    private static final String PARAM_ERR_CODE = "0001";
    private static final String PARAM_ERR_MSG = "参数错误,字段：";
    @Autowired(required = false)
    protected ProcessException processException;

    public ProcessException getProcessException() {
        return processException;
    }

    static Function<Exception, BaseResponse> constructBaseResponse = (t) -> {
        BaseResponse base = new BaseResponse();
        if (t instanceof BaseException) {
            BaseException baseException = (BaseException) t;
            base.setErrCode(baseException.getReturnErrCode());
            base.setMessage(baseException.getReturnErrMsg());
        } else if (t instanceof BaseBusinessException) {
            BaseBusinessException baseBusinessException = (BaseBusinessException) t;
            base.setErrCode(baseBusinessException.getReturnErrCode());
            base.setMessage(baseBusinessException.getReturnErrMsg());
        }
        base.setSuccess(false);
        return base;
    };

    /**
     * hystrix fallback 方法
     *
     * @param errCode
     * @param errMsg
     * @return
     */
    public BaseResponse fallBackResponse(String errCode, String errMsg) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setMessage(errMsg);
        return response;
    }

    /**
     * 全局运行时异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    private ResponseEntity<Object> handleExceptions(HttpServletRequest request, HandlerMethod handlerMethod, Exception e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        LOG.error("handleExceptions Exception:", e);

        BaseResponse base = new BaseResponse();
        if (e instanceof UndeclaredThrowableException) {
            Throwable throwable = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
            if (throwable instanceof BaseBusinessException) {
                BaseBusinessException baseBusinessException = (BaseBusinessException) throwable;
                base = constructBaseResponse.apply(baseBusinessException);
                return new ResponseEntity<>(base, HttpStatus.OK);
            }
        }

        base.setSuccess(false);
        base.setErrCode(ERR_CODE);
        base.setMessage(ERR_MSG);

        return new ResponseEntity<>(base, HttpStatus.OK);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    private ResponseEntity<Object> handleHttpMessageNotReadableException(HttpServletRequest request, HandlerMethod handlerMethod, HttpMessageNotReadableException e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        JsonMappingException exception = (JsonMappingException) e.getCause();
        BaseResponse base = new BaseResponse();
        base.setSuccess(false);
        base.setErrCode(PARAM_ERR_CODE);
        StringBuilder fieldNames = new StringBuilder();
        if (exception != null) {
            List<JsonMappingException.Reference> paths = exception.getPath();
            if (paths != null && !paths.isEmpty()) {
                for (int i = 0; i < paths.size(); i++) {
                    JsonMappingException.Reference reference = paths.get(i);
                    if (!StringUtils.isEmpty(reference.getFieldName())) {
                        fieldNames.append(reference.getFieldName());
                        if (i < paths.size() - 1 && paths.get(i + 1).getIndex() > -1) {
                            fieldNames.append("[").append(paths.get(i + 1).getIndex()).append("]");
                            i++;
                        }
                        fieldNames.append(".");
                    }
                }
            }
        }
        if (fieldNames.length() > 0) {
            fieldNames.deleteCharAt(fieldNames.length() - 1);
        }
        base.setMessage(String.format("%s%s", PARAM_ERR_MSG, fieldNames.toString()));
        LOG.info(PARAM_ERR_MSG, e);
        return new ResponseEntity(base, HttpStatus.OK);
    }

    @ExceptionHandler(value = ParamErrorException.class)
    private ResponseEntity<Object> handleParamErrorException(HttpServletRequest request, HandlerMethod handlerMethod, ParamErrorException e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        BaseResponse base = new BaseResponse();
        base.setSuccess(false);
        base.setErrCode(PARAM_ERR_CODE);
        base.setMessage(String.format("%s%s", PARAM_ERR_MSG, e.getParamNames() == null ? "" : String.join(",", Arrays.asList(e.getParamNames()))));
        LOG.info(PARAM_ERR_MSG, e);
        return new ResponseEntity(base, HttpStatus.OK);
    }

    /**
     * 全局业务异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    private ResponseEntity<Object> handleBusinessExceptions(HttpServletRequest request, HandlerMethod handlerMethod, BaseException e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        BaseResponse base = constructBaseResponse.apply(e);
        return new ResponseEntity<>(base, HttpStatus.OK);
    }

    /**
     * 全局业务异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseBusinessException.class)
    private ResponseEntity<Object> handleBusinessExceptions(HttpServletRequest request, HandlerMethod handlerMethod, BaseBusinessException e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        BaseResponse base = constructBaseResponse.apply(e);
        return new ResponseEntity<>(base, HttpStatus.OK);
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    private ResponseEntity<Object> handleBindExceptions(HttpServletRequest request, HandlerMethod handlerMethod, Throwable e) {
        if (getProcessException() != null) {
            getProcessException().process(request, handlerMethod, e);
        }
        BaseResponse base = new BaseResponse();
        base.setSuccess(false);
        base.setErrCode(PARAM_ERR_CODE);
        base.setMessage(PARAM_ERR_MSG);
        StringBuilder fields = new StringBuilder();
        List<String> errFields = new ArrayList<>();
        StringBuilder returnMsg = new StringBuilder();
        returnMsg.append(PARAM_ERR_MSG);
        if (e instanceof BindException) {
            ((BindException) e).getFieldErrors().forEach(fieldError -> {
                fields.append(String.format("%s-%s;", fieldError.getField(), fieldError.getDefaultMessage()));
                errFields.add(fieldError.getField());
            });
        } else if (e instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().forEach(fieldError -> {
                fields.append(String.format("%s-%s;", fieldError.getField(), fieldError.getDefaultMessage()));
                errFields.add(fieldError.getField());
            });

        }
        if (errFields.size() > 0) {
            Collections.sort(errFields);
            for (String field : errFields) {
                if (!returnMsg.toString().contains(field)) {
                    returnMsg.append(field).append(",");
                }
            }
            returnMsg.deleteCharAt(returnMsg.length() - 1);
            LOG.info(PARAM_ERR_MSG + ":" + fields.toString());
            base.setMessage(returnMsg.toString());
        }
        return new ResponseEntity(base, HttpStatus.OK);
    }

    protected void paramError(String... paramNames) throws ParamErrorException {
        throw new ParamErrorException(paramNames);
    }

}
