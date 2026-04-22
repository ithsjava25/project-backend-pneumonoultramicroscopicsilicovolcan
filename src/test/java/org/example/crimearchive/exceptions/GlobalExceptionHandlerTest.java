package org.example.crimearchive.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleNotFoundException() {
        Model model = new ExtendedModelMap();

        String view = handler.handleNotFound(
                new NotFoundException("Testmeddelande"),
                model
        );

        assertEquals("error/error", view);
        assertEquals(404, model.getAttribute("status"));
        assertEquals("Sidan finns inte", model.getAttribute("title"));
        assertEquals("Testmeddelande", model.getAttribute("message"));
    }

    @Test
    void shouldHandleRuntimeException() {
        Model model = new ExtendedModelMap();

        String view = handler.handleRuntime(
                new RuntimeException("Boom"),
                model
        );

        assertEquals("error/error", view);
        assertEquals(500, model.getAttribute("status"));
        assertEquals("Internt serverfel", model.getAttribute("title"));
        assertEquals("Ett oväntat fel inträffade.", model.getAttribute("message"));
    }

    @Test
    void shouldHandleNoResourceFoundException() {
        Model model = new ExtendedModelMap();

        String view = handler.handleNoResource(
                new NoResourceFoundException(
                        HttpMethod.GET,
                        "/test",
                        "Resource not found"
                ),
                model
        );

        assertEquals("error/error", view);
        assertEquals(404, model.getAttribute("status"));
        assertEquals("Sidan finns inte", model.getAttribute("title"));
    }
}
