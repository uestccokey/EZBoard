/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.i18n;

/**
 * Used for testing to provide a fake message context.
 * This should probably go in a separate testsupport package, but that does not exist yet.
 *
 * @author Barry Becker
 */
public final class StubMessageContext extends MessageContext {

    /**
     * Constructor
     */
    public StubMessageContext() {
        super("");
    }

    /**
     * Look first in the common message bundle.
     * If not found there, look in the application specific bundle if there is one.
     *
     * @param key the message key to find in resource bundle.
     * @return the localized message label
     */
    public String getLabel(String key) {
        return key.toLowerCase();
    }

    /**
     * Look first in the common message bundle.
     * If not found there, look in the application specific bundle if there is one.
     *
     * @param key    the message key to find in resource bundle.
     * @param params typically a list of string sto use a parameters to the template defined by the message from key.
     * @return the localized message label
     */
    public String getLabel(String key, Object[] params) {
        return key.toLowerCase();
    }
}