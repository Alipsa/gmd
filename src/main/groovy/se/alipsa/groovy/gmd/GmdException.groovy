package se.alipsa.groovy.gmd

class GmdException extends Exception {

  GmdException(String message) {
    super(message)
  }

  GmdException(String message, Throwable cause) {
    super(message, cause)
  }
}
