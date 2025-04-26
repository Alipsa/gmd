package se.alipsa.gmd.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.process.ExecOperations

import javax.inject.Inject

@CompileStatic
class GmdPlugin implements Plugin<Project> {

  ExecOperations execOperations

  @Inject
  GmdPlugin(ExecOperations execOperations) {
    this.execOperations = execOperations
  }

  @Override
  void apply(Project project) {
    def extension = project.extensions.create('gmdPlugin', GmdPluginParams)
    project.tasks.register('processGmd') {
      it.doLast {
        def sourceDir= new File(project.projectDir, extension.sourceDir.getOrElse("src/main/gmd"))
        def targetDir= new File(project.projectDir, extension.targetDir.getOrElse("build/gmd"))
        def outputType= extension.outputType.getOrElse('md')
        def groovyVersion = extension.groovyVersion.getOrElse('4.0.26')
        def log4jVersion = extension.log4jVersion.getOrElse('2.24.3')
        def gmdVersion = extension.gmdVersion.getOrElse('3.0.0-SNAPSHOT')
        def ivyVersion = extension.ivyVersion.getOrElse('2.5.3')

        project.logger.info("Processing GMD in ${sourceDir} -> ${targetDir}, type: ${outputType}")

        execOperations.javaexec( a -> {
          a.classpath = declareAndResolveDependencies(project, groovyVersion, log4jVersion, gmdVersion, ivyVersion)
          a.mainClass.set('se.alipsa.gmd.core.GmdProcessor')
          a.args = [
            sourceDir.canonicalPath,
            targetDir.canonicalPath,
            outputType
          ]
        })
        project.logger.quiet("Gmd files processed and written to ${targetDir.canonicalPath}")
      }
    }
  }

  static ConfigurableFileCollection declareAndResolveDependencies(Project project, String groovyVersion, String log4jVersion, String gmdVersion, String ivyVersion) {
    List<ArtifactRepository> addedRepositories = []
    if (!project.repositories.find { it instanceof MavenArtifactRepository && it.url.toString().startsWith('file:') }) {
      def mavenLocal = project.repositories.mavenLocal()
      project.repositories.add(mavenLocal)
      addedRepositories.add(mavenLocal)
    }
    if (!project.repositories.find { it instanceof MavenArtifactRepository && it.url.toString().contains('repo.maven.apache.org') }) {
      def mavenCentral = project.repositories.mavenCentral()
      project.repositories.add(mavenCentral)
      addedRepositories.add(mavenCentral)
    }
    def configuration = project.configurations.detachedConfiguration(
        project.dependencies.create("org.apache.groovy:groovy:${groovyVersion}"),
        project.dependencies.create("org.apache.groovy:groovy-templates:${groovyVersion}"),
        project.dependencies.create("org.apache.groovy:groovy-jsr223:${groovyVersion}"),
        project.dependencies.create("org.apache.ivy:ivy:${ivyVersion}"), // needed for @Grab)
        project.dependencies.create( "org.apache.logging.log4j:log4j-core:${log4jVersion}"),
        project.dependencies.create("se.alipsa.gmd:gmd-core:$gmdVersion")
    )
    def resolvedFiles = configuration.resolve()
    addedRepositories.each { repo ->
      project.repositories.remove(repo)
    }
    return project.files(resolvedFiles)
  }

  /*
    def groovyVersion = '4.0.26'
  def log4jVersion = '2.24.3'
  def gmdVersion = '3.0.0-SNAPSHOT'
  def ivyVersion = '2.5.3'
  pluginRuntime "org.apache.groovy:groovy:${groovyVersion}"
  pluginRuntime "org.apache.groovy:groovy-templates:${groovyVersion}"
  pluginRuntime "org.apache.groovy:groovy-jsr223:${groovyVersion}"
  pluginRuntime "org.apache.ivy:ivy:${ivyVersion}" // needed for @Grab)
  pluginRuntime "org.apache.logging.log4j:log4j-api:${log4jVersion}"
  pluginRuntime "org.apache.logging.log4j:log4j-core:${log4jVersion}"
  pluginRuntime "se.alipsa.gmd:gmd-core:$gmdVersion"
   */
}
