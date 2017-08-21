# gdx-backend-jtransc

[![Maven Version](https://img.shields.io/github/tag/jtransc/gdx-backend-jtransc.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22gdx-backend-jtransc%22) [![Build Status](https://secure.travis-ci.org/jtransc/gdx-backend-jtransc.svg)](http://travis-ci.org/#!/jtransc/gdx-backend-jtransc)


GDX backend for JTransc: targeting every platform lime supports (html5, windows, linux, mac, android, ios...) ([flash is wip](https://github.com/jtransc/gdx-backend-jtransc/issues/3)) (consoles + unity likely to be supported in the future)

This backend for GDX uses:
* [JTransc](https://github.com/jtransc/jtransc/) for converting JVM bytecode into Haxe
* [Haxe](http://haxe.org/) as intermediate language
* [Lime](https://github.com/openfl/lime) as cross-platform multimedia Haxe library

Examples and demos:
* [Libgdx Demos](https://github.com/jtransc/jtransc-examples/tree/master/libgdx)
* [Libgdx + Spine](https://github.com/jtransc/jtransc-examples/tree/master/spine-demo)

## Supported targets:

### JavaScript (JS)

JavaScript target allows to target JavaScript+WebGL. Projects that already do this:
[GWT](http://www.gwtproject.org/) and [TeaVM](https://github.com/konsoletyper/teavm).
* GWT is source to source Java 7 and doesn't provide reflection. So can't use Kotlin or Scala or libraries without source.
* TeaVM is pretty cool. It is bytecode based as jtransc, and has great performance. Reflection is not supported directly, but with metaprogramming.

### Flash (SWF)

[Flash support is in the works](https://github.com/jtransc/gdx-backend-jtransc/issues/3). It includes a pure java GLSL compiler that generates AGAL bytecode. And there is a StateGL adaptor that simplifies the API and allows to emit Stage3D commands.
As far as I know this is the only option you have to target java/kotlin to flash.
Flash target allows to provide an option for people using older browsers.
Also Adobe AIR allows to generate both: Android and iOS executables in a windows machine, and without specific SDKs.
So you can target to flash, and then go to Adobe AIR targets.

### Desktop (C++): iOS/Windows/Linux

It is possible to generate native executables for desktop platforms using C++. Maybe not that useful since on desktop is pretty easy to have a complete JVM implementation. But allows to test C++ target on your development machine faster than running on a device.

### Android (C++)

Even when Android supports Java natively. You don't know where it is going to be executed: Dalvik or ART. So the optimizations are not predictable. C++ allows to generate native code whith predictable performance. Also this versions totally ignores the method cound problem. So you can all all the classes and methods you need.
Also will support the JVM version jtransc support, so when supported 1.8, it will support that version on all android devices including older ones.

### iOS (C++)

RoboVM was the ideal option here. But it was closed. So this is an option that also works for other targets.

For use game center install `haxelib install extension-gamecenter` and set in project gradle `param("enable_game_center", "true")`

### BlackBerry and WebOS (C++)

JTransc using lime is capable of target these too.

### Other future targets (C++/C#) (Unity, Windows Phone, Consoles: PS4, XboxOne...)

Since it already includes code for simplifying opengl and parting GlSL code this is pretty portable.
Right now we are using Lime that could support other targets.
But we could also target to [Kha](http://kha.tech/) that already targets unity, playstation and xbox.
