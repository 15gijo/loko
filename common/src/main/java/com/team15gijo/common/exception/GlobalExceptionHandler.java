package com.team15gijo.common.exception;

import com.team15gijo.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 전역 에러 처리 클래스
 * <p>
 * CustomException : 커스텀 예외 발생
 * <p>
 * IllegalArgumentException : 잘못된 인자값이 전달된 경우
 * <p>
 * MethodArgumentNotValidException : 유효성 검사 실패 시
 * - dto 등 @Valid 검사 실패
 * <p>
 * AsyncRequestTimeoutException : WebClient / R2DBC 비동기 요청 시간 초과
 * <p>
 * Exception : 그 외 예상치 못한 예외
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        log.error("CustomException: ", e);
        return handleExceptionInternal(e.getExceptionCode());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.warn("IllegalArgumentException : ", e);
        return handleExceptionInternal(CommonExceptionCode.INVALID_PARAMETER);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders httpHeaders,
            HttpStatusCode httpStatusCode,
            WebRequest request
    ) {
        log.info("MethodArgumentNotValidException : ", ex);

        //첫번째 유효성 검증 오류 메세지 가져오기
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.exception(errorMessage, CommonExceptionCode.VALIDATION_FAILED));
    }

//    @ExceptionHandler(AsyncRequestTimeoutException.class)
//    public Mono<ResponseEntity<ApiResponse<?>>> handleAsyncTimeoutException(AsyncRequestTimeoutException e) {
//        log.warn("AsyncRequestTimeoutException : ", e);
//        return Mono.just(handleExceptionInternal(CommonExceptionCode.REQUEST_TIMEOUT));
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : ", e);
        return handleExceptionInternal(CommonExceptionCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> handleExceptionInternal(ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(ApiResponse.exception(exceptionCode.getMessage(), exceptionCode));
    }
}
