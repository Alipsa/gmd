package se.alipsa.gmd.core

class GmdException extends Exception {

  GmdException(String message) {
    super(message)
  }

  GmdException(String message, Throwable cause) {
    super(message, cause)
  }
}
