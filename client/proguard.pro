-allowaccessmodification
-ignorewarnings
-mergeinterfacesaggressively
-overloadaggressively
-keepattributes *Annotation*
-optimizationpasses 3
-overloadaggressively
-renamesourcefileattribute
-repackageclasses net.minecraft.internal

-keep @org.spongepowered.asm.mixin.** class ** { *; }
-keep @org.spongepowered.asm.mixin.** interface ** { *; }

-keepclassmembers class me.soda.witch.shared.socket.messages.** { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt