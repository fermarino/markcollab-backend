package com.markcollab.exception;

import com.markcollab.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> onApi(ApiException ex, HttpServletRequest req) {
        ErrorResponse err = build(ex.getStatus(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        BindingResult br = ex.getBindingResult();
        String msg = br.getFieldErrors().stream()
                .map(f -> f.getField()+": "+f.getDefaultMessage())
                .reduce((a,b)->a+"; "+b)
                .orElse("Dados inválidos");
        ErrorResponse err = build(HttpStatus.BAD_REQUEST.value(), msg, req.getRequestURI());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> onParse(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ErrorResponse err = build(HttpStatus.BAD_REQUEST.value(),
                "JSON malformado: "+ex.getMostSpecificCause().getMessage(),
                req.getRequestURI());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onOther(Exception ex, HttpServletRequest req) {
        ErrorResponse err = build(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado. Tente novamente mais tarde.",
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    private ErrorResponse build(int status, String msg, String path) {
        ErrorResponse e = new ErrorResponse();
        e.setStatus(status);
        e.setError(HttpStatus.valueOf(status).getReasonPhrase());
        e.setMessage(msg);
        e.setPath(path);
        return e;
    }
}
