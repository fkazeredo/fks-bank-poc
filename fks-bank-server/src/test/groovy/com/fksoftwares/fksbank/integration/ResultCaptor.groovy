package com.fksoftwares.fksbank.integration

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class ResultCaptor implements Answer {

    private result = null

    def getResult() {
        return result
    }

    @Override
    def answer(InvocationOnMock invocationOnMock) throws Throwable {
        result = invocationOnMock.callRealMethod()
        return result
    }
}