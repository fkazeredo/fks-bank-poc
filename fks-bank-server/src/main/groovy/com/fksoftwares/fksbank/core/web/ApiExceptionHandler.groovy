package com.fksoftwares.fksbank.core.web

import com.fasterxml.jackson.databind.JsonMappingException.Reference
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.PropertyBindingException
import com.fksoftwares.fksbank.core.*
import com.fksoftwares.fksbank.userprofile.PasswordRecoveryTokenExpiredException
import groovy.transform.PackageScope
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.util.StringUtils
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import java.util.stream.Collectors

@ControllerAdvice
@PackageScope
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ApiExceptionHandler)

    private MessageSource messageSource

    ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource
    }
    static final String MSG_ERRO_GENERICA_USUARIO_FINAL = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(status).headers(headers).build()
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                         WebRequest request) {

        return handleValidationInternal(ex, headers, status, request, ex.bindingResult)
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleValidationInternal(ex, headers, status, request, ex.bindingResult)
    }

    private ResponseEntity<Object> handleValidationInternal(Exception ex, HttpHeaders headers,
                                                            HttpStatus status, WebRequest request, BindingResult bindingResult) {
        def title = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente."

        List<ProblemModel.Detail> problemObjects = bindingResult.allErrors.stream()
                .map({ objectError ->

                    def message = messageSource.getMessage(objectError, LocaleContextHolder.locale)
                    def name = objectError.objectName

                    if (objectError instanceof FieldError)
                        name = ((FieldError) objectError).field

                    return new ProblemModel.Detail(name, message)
                })
                .collect(Collectors.toList())

        def problem = new ProblemModel(status.value(), title, problemObjects)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    @ExceptionHandler(Exception)
    ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR
        def title = MSG_ERRO_GENERICA_USUARIO_FINAL

        logger.error(ex.message, ex)

        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers, HttpStatus status, WebRequest request) {

        def title = "O recurso ${ex.requestURL}, que você tentou acessar, é inexistente."
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {

        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch(
                    (MethodArgumentTypeMismatchException) ex, headers, status, request)
        }

        return super.handleTypeMismatch(ex, headers, status, request)
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex)

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormat((InvalidFormatException) rootCause, headers, status, request)
        } else if (rootCause instanceof PropertyBindingException) {
            return handlePropertyBinding((PropertyBindingException) rootCause, headers, status, request)
        }

        def title = "O corpo da requisição está inválido. Verifique erro de sintaxe."
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex,
                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {

        String path = joinPath(ex.path)

        def title = "A propriedade ${path} não existe. Corrija ou remova essa propriedade e tente novamente."
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex,
                                                       HttpHeaders headers, HttpStatus status, WebRequest request) {

        def title = "A propriedade ${ex.value} recebeu o valor ${ex.targetType}, que é de um tipo inválido. Corrija e informe um valor compatível com o tipo ${ex.targetType.simpleName}."
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    @ExceptionHandler(AccessDeniedException)
    ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN
        def title = "Você não possui permissão para executar essa operação."
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(BusinessException)
    ResponseEntity<?> handleBusinessException(BusinessException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST
        def title = messageSource.getMessage(ex.message, null, LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(MailException)
    ResponseEntity<?> handleMailException(MailException ex, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR
        def title = messageSource.getMessage(ex.message, [ex.mail] as String[], LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(FileException)
    ResponseEntity<?> handleFileException(FileException ex, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR
        def title = !StringUtils.isEmpty(ex.nameOrPath) ?
                messageSource.getMessage(ex.message, [ex.nameOrPath] as String[], LocaleContextHolder.locale)
                : messageSource.getMessage(ex.message, null, LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(PasswordRecoveryTokenExpiredException)
    ResponseEntity<?> handlePasswordRecoveryTokenExpiredException(PasswordRecoveryTokenExpiredException ex, WebRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED
        def title = messageSource.getMessage(ex.message, null, LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(EntityNotFoundException)
    ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND
        def title = messageSource.getMessage(ex.message, [ex.identifier] as String[], LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(EntityAlreadyExistsException)
    ResponseEntity<?> handleEntityAlreadyExists(EntityAlreadyExistsException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT
        def title = messageSource.getMessage(ex.message, [ex.identifier] as String[], LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    @ExceptionHandler(EntityIsDisabledException)
    ResponseEntity<?> handleEntityIsDisabledException(EntityIsDisabledException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST
        def title = messageSource.getMessage(ex.message, [ex.identifier] as String[], LocaleContextHolder.locale)
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request)
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        def title = "O parâmetro de URL ${ex.getName()} recebeu o valor ${ex.getValue()}, que é de um tipo inválido. Corrija e informe um valor compatível com o tipo ${ex.getRequiredType().getSimpleName()}"
        def problem = new ProblemModel(status.value(), title, null)

        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    private String joinPath(List<Reference> references) {
        return references.stream()
                .map({ ref -> ref.fieldName })
                .collect(Collectors.joining("."))
    }

}
