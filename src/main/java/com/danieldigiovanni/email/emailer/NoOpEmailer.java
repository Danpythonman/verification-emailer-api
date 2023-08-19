package com.danieldigiovanni.email.emailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs message instead of sending an email.
 * <p>
 * The purpose of this implementation of {@link Emailer} is to have quick
 * feedback without having to check an email inbox or Mailtrap, and to run
 * tests without actually sending anything out.
 */
public class NoOpEmailer implements Emailer {

    private final Logger log = LoggerFactory.getLogger(NoOpEmailer.class);

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Doesn't actually send an email.</strong>
     * <p>
     * Instead, just logs to the console the arguments provided.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String code, Integer duration) {
        this.log.info(
            "NoOpEmailer.sendEmail called. Here are the arguments: {" +
                "toAddress: " + toAddress + ", " +
                "subject: " + subject + ", " +
                "code: " + code + ", " +
                "duration: " + duration + "}"
        );
    }

}
