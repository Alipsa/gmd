package se.alipsa.gmd.gradle

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property

@CompileStatic
interface GmdPluginParams {
  Property<String> getSourceDir()
  Property<String> getTargetDir()

  /**
   * The type of output to generate. Can be one of:
   * - md
   * - html
   * - pdf
   */
  Property<String> getOutputType()

  Property<String> getGroovyVersion()
  Property<String> getLog4jVersion()
  Property<String> getGmdVersion()
  Property<String> getIvyVersion()
}
