# 🌱 Fork of PlantUML

The purpose of this repository is to create native PlantUML images (optimized for Kroki).


## GraalVM

It seems that the latest version of GraalVM provided by OpenJDK does not work with PlantUML:

```
Exception in thread "main" java.lang.UnsatisfiedLinkError: Can't load library: awt | java.library.path = [.]
	at org.graalvm.nativeimage.builder/com.oracle.svm.core.jdk.NativeLibraries.loadLibraryRelative(NativeLibraries.java:141)
	at java.base@23.0.2/java.lang.ClassLoader.loadLibrary(ClassLoader.java:108)
	at java.base@23.0.2/java.lang.Runtime.loadLibrary0(Runtime.java:916)
	at java.base@23.0.2/java.lang.System.loadLibrary(System.java:2066)
	at java.desktop@23.0.2/sun.awt.PlatformGraphicsInfo.lambda$static$0(PlatformGraphicsInfo.java:38)
	at java.base@23.0.2/java.security.AccessController.executePrivileged(AccessController.java:132)
	at java.base@23.0.2/java.security.AccessController.doPrivileged(AccessController.java:319)
	at java.desktop@23.0.2/sun.awt.PlatformGraphicsInfo.<clinit>(PlatformGraphicsInfo.java:37)
	at java.desktop@23.0.2/java.awt.GraphicsEnvironment.lambda$getHeadlessProperty$0(GraphicsEnvironment.java:148)
	at java.base@23.0.2/java.security.AccessController.executePrivileged(AccessController.java:132)
	at java.base@23.0.2/java.security.AccessController.doPrivileged(AccessController.java:319)
	at java.desktop@23.0.2/java.awt.GraphicsEnvironment.getHeadlessProperty(GraphicsEnvironment.java:143)
	at java.desktop@23.0.2/java.awt.GraphicsEnvironment.isHeadless(GraphicsEnvironment.java:120)
	at net.sourceforge.plantuml.Run.main(Run.java:105)
	at java.base@23.0.2/java.lang.invoke.LambdaForm$DMH/sa346b79c.invokeStaticInit(LambdaForm$DMH)
```

As a workaround, the GraalVM distribution by Bellsoft seems to produce working native images: https://bell-sw.com/pages/downloads/native-image-kit/#nik-23-(jdk-21)
