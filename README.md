# Foglight
Using ProGuard? Do not be left in the dark, detect minified and obfusicated data models before you ship. 

![foglight logo](https://github.com/blobtimm/foglight/blob/master/img/blob_foglight.png "Foglight Logo")

## About

More often than not, we forget to add a rule to our data models that exclude them from becoming minified by ProGuard. This causes runtime deserializing errors when utilizing response and request models for instance. This framework provides a means for detecting that minification has occurred and reports it. Through configuration this can be either be a crash or a logging event. Detect unwanted obfusication before you ship with Foglight. 

## Install

1. Add the JitPack repository to your build.gradle file

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency

```
dependencies {
    implementation 'com.github.blobtimm:foglight:0.9'
}
```
