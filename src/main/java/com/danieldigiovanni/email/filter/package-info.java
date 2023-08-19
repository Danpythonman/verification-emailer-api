/**
 * Contains all the Spring filters for the application.
 * <p>
 * Currently includes:
 * <ul>
 *     <li>
 *         {@link com.danieldigiovanni.email.filter.JwtAuthFilter}: Filter for
 *         JSONWebToken authorization.
 *     </li>
 *     <li>
 *         {@link com.danieldigiovanni.email.filter.LoggingFilter} Filter to
 *         log information about each request.
 *     </li>
 *     <li>
 *         {@link com.danieldigiovanni.email.filter.MetricsFilter}: Filter to
 *         track metrics for each request.
 *     </li>
 * </ul>
 */
package com.danieldigiovanni.email.filter;
