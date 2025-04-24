package se.alipsa.gmd.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class GmdPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    def extension = project.extensions.create('gmdPlugin', GmdPluginParams)
    project.tasks.register('processGmd') {
      it.doLast {
        def gmdProcessor = new GmdProcessor(project, extension)
        gmdProcessor.process()
      }
    }
  }
}
