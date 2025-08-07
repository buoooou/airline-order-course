package com.postion.airlineorderbackend.exception;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class DummyResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    // do nothing
    return true;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    // do nothing
  }

}