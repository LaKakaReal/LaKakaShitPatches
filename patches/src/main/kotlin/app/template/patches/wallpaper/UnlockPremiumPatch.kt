package app.template.patches.wallpaper

import app.template.patches.shared.Constants
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.bytecodePatch

/**
 * Unlocks all premium features in Depth Wallpapers & Live Clock.
 *
 * Layers:
 *  1. Force `Category.isPremium()` → always true.
 *  2. Disable Pairip license check → no Play Store redirect.
 *  3. Force `dz2.m4037n()` → always return full access.
 *  4. Bypass final "owns premium" gate `n40.m10268e()`.
 *  5. Force `n40.j()` to always set the premium LiveData to true.
 */
@Suppress("unused")
val unlockPremiumPatch = bytecodePatch(
    name = "Unlock Premium",
    description = "Unlocks all premium wallpapers and removes license verification.",
    default = true
) {
    compatibleWith(Constants.DEPTH_WALLPAPERS_COMPATIBILITY)

    execute {
        // ── Layer 1: Make every category premium ─────────────────
        IsPremiumFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(
                0,
                """
                const/4 v0, 0x1
                return v0
                """.trimIndent()
            )
        }

        // ── Layer 2: Disable Pairip license check ─────────────────
        LicenseClientFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(0, "return-void")
        }

        // ── Layer 3: Force premium access gate ────────────────────
        M4037NFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(
                0,
                """
                const/4 v0, 0x1
                const/4 v1, 0x1
                new-instance v2, Lrn4;
                invoke-direct {v2, v0, v1, p1}, Lrn4;-><init>(ZZZ)V
                return-object v2
                """.trimIndent()
            )
        }

        // ── Layer 4: Bypass final "owns premium" gate ─────────────
        IsPremiumOwnedFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(
                0,
                """
                const/4 v0, 0x1
                return v0
                """.trimIndent()
            )
        }

        // ── Layer 5: Force the premium LiveData to TRUE ───────────
        PremiumSetterFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(
                0,
                """
                const/4 v0, 0x1
                invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
                move-result-object v0
                iget-object p0, p0, Ln40;->C:Lyr5;
                const/4 v1, 0x0
                invoke-virtual {p0, v1, v0}, Lyr5;->j(Ljava/lang/Object;Ljava/lang/Object;)Z
                return-void
                """.trimIndent()
            )
        }
    }
}