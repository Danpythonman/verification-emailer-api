package com.danieldigiovanni.email;

import com.danieldigiovanni.email.config.filter.JwtAuthFilter;
import jakarta.annotation.Nonnull;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Adds a servlet path to a {@link MockHttpServletRequest}.
 * <p>
 * For some reason, the servlet path for tests is not populated. Adding this
 * post processor to the request will manually add the servlet path. This is
 * important for the {@link JwtAuthFilter} because it uses the servlet path to
 * ignore auth for login and register routes.
 *
 * @see <a href="https://stackoverflow.com/a/74182052">
 *     https://stackoverflow.com/a/74182052
 * </a>
 */
public class AddServletPathRequestPostProcessor implements RequestPostProcessor {

    private final String servletPath;

    /**
     * Creates an instance of a request post processor that adds the servlet
     * path to a {@link MockHttpServletRequest}.
     * <p>
     * Use this class with the {@link MockMvc#perform(RequestBuilder)} method:
     * <pre>{@code
     * mockMvc.perform(
     *     post("/path")
     *         .with(new AddServletPathRequestPostProcessor("/path"))
     *         ...
     *     )
     * }</pre>
     *
     * @param servletPath The servlet path to be added to the request.
     */
    public AddServletPathRequestPostProcessor(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    @Nonnull
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setServletPath(this.servletPath);
        return request;
    }

}
