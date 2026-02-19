package com.training.ecommerce.exceptions;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleProduct(ProductException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<String> handleCartException(CartException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<String> handleCartException(OrderException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", HttpStatus.FORBIDDEN.value());
//        body.put("errore", "Accesso Negato");
//        body.put("messaggio", "Non hai i permessi ADMIN per eseguire questa azione!");
//
//        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Errore: Non hai i permessi necessari. Solo l'Admin può eseguire questa operazione.");
    }
}
