/**
 * Contains logic for application-wide exception handling.
 * <p>
 * This includes:
 * <ul>
 *     <li>
 *         application-wide error handlers
 *         ({@link com.danieldigiovanni.email.config.exception.ControllerExceptionHandler})
 *     </li>
 *     <li>
 *         default error controller for unhandled errors that make it past the
 *         exception handlers
 *         ({@link com.danieldigiovanni.email.config.exception.DefaultErrorController})
 *     </li>
 *     <li>
 *         common format for error response body
 *         ({@link com.danieldigiovanni.email.config.exception.ErrorResponseBody})
 *     </li>
 * </ul>
 */
package com.danieldigiovanni.email.config.exception;
