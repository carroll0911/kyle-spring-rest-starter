package com.kyle.spring.rest.starter.aop;

import com.kyle.spring.rest.starter.BaseController;
import com.kyle.spring.rest.starter.BaseException;
import com.kyle.spring.rest.starter.BaseRequest;
import com.kyle.utils.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
@Aspect
@Component
@Order(1)
public class ControllerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ControllerInterceptor.class);

    private static final String PREFIX_FORMAT = String.format("%n");
    private static final String SEMICOLON = ";";
    private static final String COLON = ":";
    private static final String PREFIX_LINE = "----------";
    private static final String SUFFIX_LINE = "------------------------------------------------------------------------------------------------------------------------";
    private static final String STR_URL = "URL:              ";
    private static final String STR_IP = "IP:               ";
    private static final String STR_REQUEST_METHOD = "REQUEST_METHOD:   ";
    private static final String STR_CLASS_METHOD = "CLASS_METHOD:     ";
    private static final String STR_PARAMS = "PARAMS:           ";
    private static final String STR_HEADERS = "HEADERS:          ";
    private static final String STR_RESULT = "RESULT:           ";
    private static final String STR_TIME = "TIMES:            %S MS";
    private static final String ERR_MSG = "参数错误,字段：";
    private static final String ERR_CODE = "0001";

    /**
     * 定义拦截规则：拦截org.springframework.web.bind.annotation.RequestMapping注解的方法。
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethodPointcut() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。）
     */
    @Around("controllerMethodPointcut()")
    @SuppressWarnings("unused")
    private Object aroundInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args = joinPoint.getArgs();
        long startTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        Class clazz = joinPoint.getSignature().getDeclaringType();
        if (!BaseController.class.isAssignableFrom(clazz)) {
            throw new BaseException(ERR_CODE, "没有继承BaseController");
        }
        //请求参数
        StringBuilder strRequest = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof BaseRequest) {
                strRequest.append(StringUtil.objToJsonString(arg));

            } else if (arg instanceof BindingResult) {
                // 参数校验错误
                BindingResult bindingResult = (BindingResult) arg;

                StringBuilder errMsg = new StringBuilder();
                List<String> errFields = new ArrayList<>();
                StringBuilder returnMsg = new StringBuilder();
                returnMsg.append(ERR_MSG);
                if (bindingResult.hasErrors()) {
                    for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        errMsg.append(fieldError.getField()).append(COLON)
                                .append(fieldError.getDefaultMessage()).append(SEMICOLON);
                        errFields.add(fieldError.getField());
                    }
                    Collections.sort(errFields);
                    for (String field : errFields) {
                        if (!returnMsg.toString().contains(field)) {
                            returnMsg.append(field).append(",");
                        }
                    }
                    returnMsg.deleteCharAt(returnMsg.length() - 1);
                    // 记录下请求内容
                    sb.append(PREFIX_FORMAT + format(joinPoint.getSignature().getDeclaringType().getSimpleName()))
                            .append(PREFIX_FORMAT).append(STR_URL).append(request.getRequestURL().toString())
                            .append(PREFIX_FORMAT).append(STR_IP).append(request.getRemoteAddr())
                            .append(PREFIX_FORMAT).append(STR_REQUEST_METHOD).append(request.getMethod())
                            .append(PREFIX_FORMAT).append(STR_CLASS_METHOD).append(joinPoint.getSignature().getName())
                            .append(PREFIX_FORMAT).append(STR_PARAMS).append(strRequest.toString())
                            .append(PREFIX_FORMAT).append(STR_HEADERS).append(StringUtil.objToJsonString(getHeadersInfo(request)))
                            .append(PREFIX_FORMAT).append(PREFIX_LINE).append(SUFFIX_LINE);
                    log.info(sb.toString());
                    log.warn(String.format(PREFIX_FORMAT + errMsg.toString()));
                    throw new BaseException(ERR_CODE, returnMsg.toString());
                }
            } else if (arg instanceof MultipartFile) {
                MultipartFile mfArg = (MultipartFile) arg;
                strRequest.append(String.format("%s;%s", mfArg.getName(), mfArg.getSize())).append(",");
            } else {
                if (!(arg instanceof HttpServletResponse) && !(arg instanceof HttpServletRequest)) {
                    strRequest.append(StringUtil.objToJsonString(arg)).append(",");
                }

            }
        }

        sb.append(PREFIX_FORMAT + format(joinPoint.getSignature().getDeclaringType().getSimpleName()))
                .append(PREFIX_FORMAT).append(STR_URL).append(request.getRequestURL().toString())
                .append(PREFIX_FORMAT).append(STR_IP).append(request.getRemoteAddr())
                .append(PREFIX_FORMAT).append(STR_REQUEST_METHOD).append(request.getMethod())
                .append(PREFIX_FORMAT).append(STR_CLASS_METHOD).append(joinPoint.getSignature().getName())
                .append(PREFIX_FORMAT).append(STR_PARAMS).append(strRequest.toString())
                .append(PREFIX_FORMAT).append(STR_HEADERS).append(StringUtil.objToJsonString(getHeadersInfo(request)));

        //响应结果
        long endTimeMillis = 0;
        long exeTime = 0;
        try {
            Object result = joinPoint.proceed();
            endTimeMillis = System.currentTimeMillis();
            exeTime = endTimeMillis - startTimeMillis;
            String strResponse = StringUtil.objToJsonString(result);
            sb.append(PREFIX_FORMAT).append(STR_RESULT).append(strResponse)
                    .append(String.format(PREFIX_FORMAT + STR_TIME, exeTime))
                    .append(PREFIX_FORMAT).append(PREFIX_LINE).append(SUFFIX_LINE);
            // 记录下响应内容
            log.info(sb.toString());
            return result;
        } catch (Exception e) {
            endTimeMillis = System.currentTimeMillis();
            exeTime = endTimeMillis - startTimeMillis;
            sb.append(PREFIX_FORMAT).append(STR_RESULT)
                    .append(String.format(PREFIX_FORMAT + STR_TIME, exeTime))
                    .append(PREFIX_FORMAT).append(PREFIX_LINE).append(SUFFIX_LINE);
            // 记录下响应内容
            log.info(sb.toString());
            throw e;
        }
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 异常通知
     *
     * @param jp
     * @param ex
     */
    @SuppressWarnings("unused")
    @AfterThrowing(value = "controllerMethodPointcut()", throwing = "ex")
    private void afterThrowing(JoinPoint jp, Exception ex) {
        log.error(String.format(PREFIX_FORMAT + jp.getSignature().getName()), ex);
    }

    /**
     * log格式化
     *
     * @param controller
     * @return
     */
    private String format(String controller) {
        int size = controller.length();
        String replace = SUFFIX_LINE.substring(0, size);
        return PREFIX_LINE + SUFFIX_LINE.replaceFirst(replace, controller);
    }
}