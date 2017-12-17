package ir.company.app.service.migmig;

import ir.company.app.domain.entity.ErrorLog;
import ir.company.app.domain.entity.User;
import ir.company.app.repository.ErrorLogRepository;
import ir.company.app.repository.UserRepository;
import ir.company.app.security.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Inject
    ErrorLogRepository errorLogRepository;
    @Inject
    UserRepository userRepository;

    @ExceptionHandler(value = {RuntimeException.class, Throwable.class})
    protected ResponseEntity<Object> InternalError(Exception ex, WebRequest request) {

        ErrorLog errorLog = new ErrorLog();
        String login = SecurityUtils.getCurrentUserLogin();
        User user = null;

        if (login != null)
            user = userRepository.findOneByLogin(login);
        StringWriter result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        errorLog.setUser(user);
        ex.printStackTrace(printWriter);
        errorLog.setLog(result.toString());
        errorLogRepository.save(errorLog);
        String bodyOfResponse = "Internal error";
        return handleExceptionInternal(ex, bodyOfResponse,
            new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
