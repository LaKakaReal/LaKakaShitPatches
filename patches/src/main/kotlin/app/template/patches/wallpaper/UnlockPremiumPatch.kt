package app.template.patches.wallpaper

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.DEPTH_WALLPAPERS_COMPATIBILITY
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

/**
 * Unlocks all premium wallpapers and manages internal execution gates.
 */
@Suppress("unused")
val unlockPremiumPatch = bytecodePatch(
    name = "Unlock Premium",
    description = "Unlocks all premium wallpapers and removes license verification features.",
    default = true
) {
    compatibleWith(DEPTH_WALLPAPERS_COMPATIBILITY)

    execute {
        // ── Layer 1: Force Category.isPremium() -> always true ───────────────
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

        // ── Layer 2: Disable Pairip license verification routines ────────────
        LicenseClientFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(0, "return-void")
        }

        // ── Layer 3: Bypass premium layout verification checks ───────────────
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

        // ── Layer 4: Clear "owns premium" global verification gates ───────────
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

        // ── Layer 5: Force the premium LiveData state mapping to true ─────────
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

        // ── Layer 6: Clear category view instantiation references ────────────
        // To safely bypass the resource/cast traps, we no-op the initialization assignments
        val match = Fo6ClinitFingerprint.instructionMatches.first()
        val index = match.index
        val register = match.getInstruction<OneRegisterInstruction>().registerA
        Fo6ClinitFingerprint.method.replaceInstruction(
            index,
            "const v$register, 0x7f070097"
        )

        // ── Layer 7: Disable validation warning modal layouts ────────────────
        LicenseErrorDialogFingerprint.method.apply {
            removeInstructions(0, instructions.count())
            addInstructions(0, "return-void")
        }
    }
}