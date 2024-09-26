package searchengine.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import searchengine.exception.EmptySearchQueryException;
import searchengine.exception.IndexingIsAlreadyRunningException;
import searchengine.exception.IndexingIsAlreadyStoppedException;
import searchengine.exception.PageForIndexationIsOutsideTheConfigurationFileException;

@ControllerAdvice
@Slf4j
public class DefaultAdvice {

    @ExceptionHandler(IndexingIsAlreadyRunningException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResultMessage> IndexingIsAlreadyRunningExceptionHandle(IndexingIsAlreadyRunningException e) {
        ResultMessage resultMessage = new ResultMessage("false", "Индексация уже запущена");
        return new ResponseEntity<>(resultMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IndexingIsAlreadyStoppedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResultMessage> IndexingIsAlreadyStoppedExceptionHandle(IndexingIsAlreadyStoppedException e) {
        ResultMessage resultMessage = new ResultMessage("false", "Индексация не запущена");
        return new ResponseEntity<>(resultMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PageForIndexationIsOutsideTheConfigurationFileException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResultMessage> PageForIndexationIsOutsideTheConfigurationFileExceptionHandle(PageForIndexationIsOutsideTheConfigurationFileException e) {
        ResultMessage resultMessage = new ResultMessage("false", "Указанная страница не найдена");
        return new ResponseEntity<>(resultMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptySearchQueryException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResultMessage> EmptySearchQueryExceptionHandle(EmptySearchQueryException e) {
        ResultMessage resultMessage = new ResultMessage("false", "Пустой поисковой запрос");
        return new ResponseEntity<>(resultMessage, HttpStatus.FORBIDDEN);
    }


}
