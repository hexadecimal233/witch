-adaptclassstrings
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames
-ignorewarnings
-keepattributes *Annotation*
-mergeinterfacesaggressively
-optimizationpasses 3
-overloadaggressively
-renamesourcefileattribute
-repackageclasses

-keep @org.spongepowered.asm.mixin.Mixin class ** { *; }
-keep @org.spongepowered.asm.mixin.Mixin interface ** { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt