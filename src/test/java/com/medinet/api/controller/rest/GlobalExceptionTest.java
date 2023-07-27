package com.medinet.api.controller.rest;

import com.medinet.api.controller.GlobalExceptionHandler;
import com.medinet.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleExceptionTest() {
        Exception exception = new Exception("Test exception");
        ModelAndView modelAndView = globalExceptionHandler.handleException(exception);

        assertEquals("error", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().containsKey("errorMessage"));
        assertEquals("Other exception occurred: [Test exception]", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    public void handleNoResourceFoundTest() {
        NotFoundException notFoundException = new NotFoundException("Resource not found");
        ModelAndView modelAndView = globalExceptionHandler.handleNoResourceFound(notFoundException);

        assertEquals("error", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().containsKey("errorMessage"));
        assertEquals("Could not find a resource: [Resource not found]", modelAndView.getModel().get("errorMessage"));
    }

}
