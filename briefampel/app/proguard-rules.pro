# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class de.briefampel.app.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
