package ma.org.ormt.config.logging;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingService {

    @Pointcut("execution(* ma.org.ormt.resource..*.*(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("============================================ START ============================================");
        log.info("Performed by: {}", getCurrentUsername());

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes
                    .getRequest();
            Map<String, String[]> paramMap = request.getParameterMap();

            if (paramMap.isEmpty()) {
                log.info("No parameters");
            }
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                log.info("Parameter [{}]: {}", entry.getKey(), String.join(", ", entry.getValue()));
            }
        }

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;

        log.info("{} executed in {} ms", joinPoint.getSignature(), elapsedTime);
        log.info("============================================= END =============================================");
        return result;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // For simple cases, or use a more detailed user object if available.
        }
        return "Unauthenticated";
    }

    public void logMethodParameters(String methodName, Object[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                log.info("Parameter [{}] for method {}: {}", i, methodName, args[i]);
            }
        } else {
            log.info("Method {} has no parameters", methodName);
        }
    }

    public void logMethod(String entityType) {
        log.info("Method: {} - Input: {}", entityType);
    }

}