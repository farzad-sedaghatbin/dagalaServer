package ir.company.app.service.migmig;

import ir.company.app.domain.entity.ErrorLog;
import ir.company.app.repository.ErrorLogRepository;
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

    @ExceptionHandler(value = {RuntimeException.class, Throwable.class})
    protected ResponseEntity<Object> InternalError(Exception ex, WebRequest request) {
        try {


            ErrorLog errorLog = new ErrorLog();
            StringWriter result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            errorLog.setLog(result.toString());
            errorLogRepository.save(errorLog);
            return ResponseEntity.ok("500");
        } catch (Exception e) {
            return ResponseEntity.ok("500");

        }
    }
}
