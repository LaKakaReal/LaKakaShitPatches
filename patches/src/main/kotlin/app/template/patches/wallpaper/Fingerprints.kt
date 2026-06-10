package app.template.patches.wallpaper

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.literal
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/** Matches `Category.isPremium()` */
object IsPremiumFingerprint : Fingerprint(
    definingClass = "Lcom/jndapp/depth/live/wallpaper/model/Category;",
    strings = listOf("free"),
    returnType = "Z",
    parameters = listOf()
)

/** Matches `LicenseClient.checkLicense(Context)` */
object LicenseClientFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("Landroid/content/Context;")
)

/** Matches `dz2.m4037n(Category, boolean, Set)` */
object M4037NFingerprint : Fingerprint(
    definingClass = "Ldz2;",
    returnType = "Lrn4;",
    parameters = listOf(
        "Lcom/jndapp/depth/live/wallpaper/model/Category;",
        "Z",
        "Ljava/util/Set;"
    ),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/jndapp/depth/live/wallpaper/model/Category;",
            name = "isPremium",
            returnType = "Z"
        )
    )
)

/** Matches `n40.m10268e()` – the "owns premium" check */
object IsPremiumOwnedFingerprint : Fingerprint(
    definingClass = "Ln40;",
    returnType = "Z",
    parameters = listOf(),
    strings = listOf("premium_lifetime")
)

/** Matches `n40.j()` – updates the premium boolean from purchased sets */
object PremiumSetterFingerprint : Fingerprint(
    definingClass = "Ln40;",
    returnType = "V",
    parameters = listOf(),
    filters = listOf(
        methodCall(
            definingClass = "Lyr5;",
            name = "j",
            returnType = "Z"
        )
    )
)

/** Matches the static initializer of `fo6` that uses the missing `ic_featured` drawable */
object Fo6ClinitFingerprint : Fingerprint(
    definingClass = "Lfo6;",
    strings = listOf("Premium"),
    filters = listOf(
        literal(0x7f070090)   // ic_featured
    )
)

/** Matches the error dialog method in LicenseActivity */
object LicenseErrorDialogFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseActivity;",
    strings = listOf("Something went wrong"),
    returnType = "V",
    parameters = listOf()
)