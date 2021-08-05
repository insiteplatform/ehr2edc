package com.custodix.insite.local.ehr2edc.rest.util;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

final class MockRemoteAddr {
    static RequestPostProcessor localhostIPv4() {
        return remoteAddr("127.0.0.1");
    }

    static RequestPostProcessor localhostIPv6() {
        return remoteAddr("::1");
    }

    static RequestPostProcessor nonLocalhost() {
        return remoteAddr("192.168.0.2");
    }

    private static RequestPostProcessor remoteAddr(final String remoteAddr) {
        return { request ->
            request.setRemoteAddr(remoteAddr)
            return request
        }
    }
}
