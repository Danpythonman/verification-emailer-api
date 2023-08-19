/**
 * Contains logic for application-wide error and exception handling.
 * <p>
 * This includes:
 * <ul>
 *     <li>
 *         application-wide exception handlers
 *         ({@link com.danieldigiovanni.email.error.ControllerExceptionHandler})
 *     </li>
 *     <li>
 *         default error controller for unhandled errors that make it past the
 *         exception handlers
 *         ({@link com.danieldigiovanni.email.error.DefaultErrorController})
 *     </li>
 *     <li>
 *         common format for error response body
 *         ({@link com.danieldigiovanni.email.error.ErrorResponseBody})
 *     </li>
 * </ul>
 */
package com.danieldigiovanni.email.error;
