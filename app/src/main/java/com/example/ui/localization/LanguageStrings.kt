package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    FRENCH("fr", "Français", false),
    ARABIC("ar", "العربية", true),
    DARIJA("dar", "الدارجة (Darija)", true)
}

object Strings {
    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Sawt Distribution"
        AppLanguage.FRENCH -> "Sawt Distribution"
        AppLanguage.ARABIC -> "صوت المغرب للتوزيع الموسيقي"
        AppLanguage.DARIJA -> "صوت - توزيع الموسيقى"
    }

    fun appSlogan(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Morocco's Home for Independent Music"
        AppLanguage.FRENCH -> "La maison de la musique indépendante au Maroc"
        AppLanguage.ARABIC -> "المنصة المغربية المستقلة لتوزيع الموسيقى العالمية"
        AppLanguage.DARIJA -> "دار الموسيقى المستقلة فالمغرب"
    }

    fun myReleases(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "My Releases"
        AppLanguage.FRENCH -> "Mes Sorties"
        AppLanguage.ARABIC -> "إصداراتي"
        AppLanguage.DARIJA -> "الإصدارات ديالي"
    }

    fun createRelease(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Create Release"
        AppLanguage.FRENCH -> "Créer une Sortie"
        AppLanguage.ARABIC -> "إنشاء إصدار جديد"
        AppLanguage.DARIJA -> "خرج أغنية / ألبوم"
    }

    fun royalties(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Royalties & Earnings"
        AppLanguage.FRENCH -> "Revenus & Royalties"
        AppLanguage.ARABIC -> "العائدات والأرباح"
        AppLanguage.DARIJA -> "الأرباح والمستحقات"
    }

    fun adminDashboard(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Admin Dashboard"
        AppLanguage.FRENCH -> "Tableau de Bord Admin"
        AppLanguage.ARABIC -> "لوحة التحكم الإدارية"
        AppLanguage.DARIJA -> "لوحة التحكم (Admin)"
    }

    fun artistPortal(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Artist Portal"
        AppLanguage.FRENCH -> "Portail Artiste"
        AppLanguage.ARABIC -> "بوابة الفنان"
        AppLanguage.DARIJA -> "حساب الفنان"
    }

    fun readyToSubmit(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "READY TO SUBMIT"
        AppLanguage.FRENCH -> "PRÊT À SOUMETTRE"
        AppLanguage.ARABIC -> "جاهز للإرسال والتوزيع"
        AppLanguage.DARIJA -> "واجد للمصادقة والتوزيع"
    }

    fun fixTheseIssues(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "FIX THESE ISSUES"
        AppLanguage.FRENCH -> "CORRIGER CES ANOMALIES"
        AppLanguage.ARABIC -> "يرجى تصحيح هذه الأخطاء"
        AppLanguage.DARIJA -> "صلح هاد المشاكل عاد كمل"
    }

    fun rightsConfirmation(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "I confirm that I own or control the rights necessary to distribute this recording and artwork."
        AppLanguage.FRENCH -> "Je confirme que je possède ou contrôle les droits nécessaires pour distribuer cet enregistrement et cet artwork."
        AppLanguage.ARABIC -> "أؤكد أنني أمتلك أو أتحكم قانونياً في الحقوق اللازمة لتوزيع هذا التسجيل الصوتي والغلاف الفني."
        AppLanguage.DARIJA -> "كنأكد بلي عندي كامل الحقوق القانونية باش نوزع هاد الموسيقى وتصويرة الألبوم."
    }

    fun sandboxMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "SANDBOX / DEMO"
        AppLanguage.FRENCH -> "SANDBOX / DÉMO"
        AppLanguage.ARABIC -> "الوضع التجريبي"
        AppLanguage.DARIJA -> "وضع التجربة (SANDBOX)"
    }

    fun productionMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "PRODUCTION INGEST"
        AppLanguage.FRENCH -> "INGESTION PRODUCTION"
        AppLanguage.ARABIC -> "وضع الإنتاج الحقيقي"
        AppLanguage.DARIJA -> "الإنتاج الحقيقي"
    }
}
