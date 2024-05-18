package com.bhenriq.resume_backend.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SkipRemainingOnFailure.Extension.class)
public @interface SkipRemainingOnFailure {

    class Extension implements TestWatcher, ExecutionCondition {

        static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(Extension.class);

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            // Store in extension context of parent (i.e. test class) so that subsequent test can see failure
            context.getParent()
                    .orElseThrow(() -> new RuntimeException("test without parent"))
                    .getStore(NAMESPACE)
                    .put("failed", true);
        }

        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            return context.getStore(NAMESPACE).getOrDefault("failed", Boolean.class, false)
                    ? ConditionEvaluationResult.disabled("Skipping due to prior failure")
                    : ConditionEvaluationResult.enabled("No prior failure");
        }
    }
}