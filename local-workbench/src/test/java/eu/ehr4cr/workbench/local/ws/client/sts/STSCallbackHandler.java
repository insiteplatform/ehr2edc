package eu.ehr4cr.workbench.local.ws.client.sts;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class STSCallbackHandler implements CallbackHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(STSCallbackHandler.class);
	private static final String KEY_ALIAS = "mystskey";
	private static final String KEY_PASSWORD = "stskpass";

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (final Callback callback : callbacks) {
			if (callback instanceof WSPasswordCallback) {
				WSPasswordCallback pc = (WSPasswordCallback) callback;
				handleCallback(pc);
			}
		}
	}

    private void handleCallback(WSPasswordCallback pc) {
        if (pc.getUsage() == WSPasswordCallback.DECRYPT || pc.getUsage() == WSPasswordCallback.SIGNATURE) {
            if (KEY_ALIAS.equals(pc.getIdentifier())) {
                pc.setPassword(KEY_PASSWORD);
            }
        } else {
            LOGGER.warn("Unhandled password callback for type {} and alias {}", pc.getUsage(), pc.getIdentifier());
        }
    }
}