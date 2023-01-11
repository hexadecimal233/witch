-allowaccessmodification
-ignorewarnings
-mergeinterfacesaggressively
-optimizationpasses 3
-overloadaggressively
-renamesourcefileattribute
-repackageclasses

-keep @org.spongepowered.asm.mixin.** class ** { *; }
-keep @org.spongepowered.asm.mixin.** interface ** { *; }

-keepclassmembers class me.soda.witch.shared.socket.messages.** { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt