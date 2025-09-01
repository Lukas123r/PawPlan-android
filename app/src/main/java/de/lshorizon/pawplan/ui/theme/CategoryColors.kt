package de.lshorizon.pawplan.ui.theme

import androidx.compose.ui.graphics.Color

enum class ReminderCategory { VACCINATION, VET, DEWORMING, GROOMING, WALK, FEEDING, OTHER }

fun reminderCategoryFor(title: String): ReminderCategory {
    val t = title.lowercase()
    return when {
        t.contains("vaccination") || t.contains("impf") -> ReminderCategory.VACCINATION
        t.contains("vet") || t.contains("tierarzt") -> ReminderCategory.VET
        t.contains("deworm") || t.contains("entwurm") -> ReminderCategory.DEWORMING
        t.contains("groom") || t.contains("pflege") -> ReminderCategory.GROOMING
        t.contains("walk") || t.contains("spazier") -> ReminderCategory.WALK
        t.contains("feed") || t.contains("fuetter") || t.contains("fütter") || t.contains("futter") -> ReminderCategory.FEEDING
        else -> ReminderCategory.OTHER
    }
}

fun colorFor(category: ReminderCategory): Color = when (category) {
    ReminderCategory.VACCINATION -> SecondaryGreen
    ReminderCategory.VET -> WarningYellow
    ReminderCategory.DEWORMING -> AccentOrange
    ReminderCategory.GROOMING -> PrimaryBlue
    ReminderCategory.WALK -> PrimaryBlue
    ReminderCategory.FEEDING -> LoginButtonOrange
    ReminderCategory.OTHER -> PrimaryBlue
}

enum class DocumentCategory { PDF, IMAGE, REPORT, OTHER }

fun documentCategoryFor(name: String): DocumentCategory {
    val n = name.lowercase()
    return when {
        n.endsWith(".pdf") -> DocumentCategory.PDF
        n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp") -> DocumentCategory.IMAGE
        n.contains("report") || n.contains("bericht") -> DocumentCategory.REPORT
        else -> DocumentCategory.OTHER
    }
}

fun colorFor(category: DocumentCategory): Color = when (category) {
    DocumentCategory.PDF -> AccentOrange
    DocumentCategory.IMAGE -> SecondaryGreen
    DocumentCategory.REPORT -> PrimaryBlue
    DocumentCategory.OTHER -> PrimaryBlue
}

