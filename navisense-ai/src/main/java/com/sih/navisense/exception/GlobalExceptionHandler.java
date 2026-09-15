package com.sih.navisense.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error on request {}: {}", request.getRequestURI(), ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Validation Failed");
            body.put("details", errors);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Validation failed: " + errors.values().iterator().next());
        mav.addObject("errorDetails", errors.toString());
        return mav;
    }

    @ExceptionHandler(BindException.class)
    public Object handleBindExceptions(BindException ex, HttpServletRequest request) {
        log.warn("Bind error on request {}: {}", request.getRequestURI(), ex.getMessage());

        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? "Invalid form input" : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", msg);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", msg);
        mav.addObject("errorDetails", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception processing {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "An internal error occurred in NaviSense AI");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Operational or calculation anomaly encountered.");
        mav.addObject("errorDetails", ex.getMessage() != null ? ex.getMessage() : "Unknown exception");
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api") || (accept != null && accept.contains("application/json"));
    }
}
