package com.projectd.interpreter.runtime.environment;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
import com.projectd.interpreter.shared.exception.RuntimeExceptionFactory;

import java.util.ArrayDeque;
import java.util.Deque;

public class RuntimeEnvironment {

    private final Deque<RuntimeScope> scopes = new ArrayDeque<>();

    public RuntimeEnvironment() {
        scopes.push(new RuntimeScope());
    }

    public void nestScope() {
        scopes.push(new RuntimeScope());
    }

    public void flushScope() {
        scopes.pop();
    }

    public void assignVariable(LexIdentifierToken identifierToken, RuntimeValue value) {
        String identifier = identifierToken.getIdentifier();
        for (RuntimeScope scope : scopes) {
            if (scope.hasIdentifier(identifier)) {
                scope.storeValueByIdentifier(identifier, value);
                return;
            }
        }
        throw RuntimeExceptionFactory.undefinedVariable(identifier, identifierToken.getSpan());
    }

    public void declareVariable(LexIdentifierToken identifierToken) {
        String identifier = identifierToken.getIdentifier();
        if (scopes.getFirst().hasIdentifier(identifier)) {
            throw RuntimeExceptionFactory.alreadyDefined(identifier, identifierToken.getSpan());
        }
        scopes.getFirst().storeValueByIdentifier(identifier, RuntimeValue.empty());
    }

    public void declareAndAssignVariable(LexIdentifierToken identifierToken, RuntimeValue value) {
        String identifier = identifierToken.getIdentifier();
        if (scopes.getFirst().hasIdentifier(identifier)) {
            throw RuntimeExceptionFactory.alreadyDefined(identifier, identifierToken.getSpan());
        }
        scopes.getFirst().storeValueByIdentifier(identifier, value);
    }

    public RuntimeValue getVariableValue(LexIdentifierToken identifierToken) {
        String identifier = identifierToken.getIdentifier();
        for (RuntimeScope scope : scopes) {
            if (scope.hasIdentifier(identifier)) {
                return scope.getValueByIdentifier(identifier);
            }
        }
        throw RuntimeExceptionFactory.undefinedVariable(identifier, identifierToken.getSpan());
    }
}
