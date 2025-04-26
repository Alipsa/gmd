# The gmd-gradle-plugin

This gradle plugin makes it possible to process a directory of GMD files and transform them into md, html of pdf files.

To use it in your gradle build script, add the following to your build.gradle file:

```groovy
plugins {
  id 'se.alipsa.gmd.gmd-gradle-plugin'
}
gmdPlugin {
  sourceDir = 'src/test/gmd'
  targetDir = 'build/target'
  outputType = 'html'
}
```
Possible parameters are:
- `sourceDir` - the directory where the GMD files are located. Default is `src/main/gmd`
- `targetDir` - the directory where the output files will be created. Default is `build/gmd`
- `outputType` - the type of output file to create. Possible values are `md`, `html`, `pdf`. Default is `md`
- `groovyVersion` - the version of Groovy to use. Default is `4.0.26`
- `gmdVersion` - the version of GMD to use. Default is `3.0.0`
- `log4jVersion` - the version of log4j to use. Default is `2.24.3`
- `ivyVersion` - the version of ivy to use. Default is `2.5.3`

The target task is called `processGmd` so it can be invoked from the command line as follows:

```bash
./gradlew processGmd
```