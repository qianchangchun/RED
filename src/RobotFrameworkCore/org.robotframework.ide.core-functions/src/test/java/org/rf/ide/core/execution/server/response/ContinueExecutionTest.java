/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.execution.server.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.rf.ide.core.execution.server.response.ServerResponse.ResponseException;

public class ContinueExecutionTest {

    @Test
    public void properMessageIsConstructed_forContinueExecutionMessage() {
        assertThat(new ContinueExecution().toMessage()).isEqualTo("{\"continue\":[]}");
    }

    @Test(expected = ResponseException.class)
    public void mapperIOExceptionIsWrappedAsResponseException() throws Exception {
        final ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(any(Object.class))).thenThrow(IOException.class);

        final ContinueExecution response = new ContinueExecution(mapper);

        response.toMessage();
    }
}
