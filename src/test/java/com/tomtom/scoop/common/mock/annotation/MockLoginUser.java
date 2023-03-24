package com.tomtom.scoop.common.mock.annotation;

import com.tomtom.scoop.common.mock.WithMockCustomUserSecurityContextFactory;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface MockLoginUser {
    String oauthId() default "8fk03afl";

    String name() default "koo";

}
