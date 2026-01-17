package com.shamim.helparticles.data.network

import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails

val FAKE_ARTICLES = listOf(
    Article("1", "How to create an account", "Account Creation Guide", 1705500000000L),
    Article("2", "Reset your password easily", "Password Reset", 1705586400000L),
    Article("3", "Update your profile info", "Profile Update", 1705672800000L),
    Article("4", "Connecting your social accounts", "Social Account Linking", 1705759200000L),
    Article("5", "Understanding notifications", "Notifications Settings", 1705845600000L),
    Article("6", "Managing subscriptions", "Subscription Management", 1705932000000L),
    Article("7", "Using dark mode", "Dark Mode Feature", 1706018400000L),
    Article("8", "Data privacy tips", "Privacy & Security", 1706104800000L),
    Article("9", "Troubleshooting login issues", "Login Troubleshooting", 1706191200000L),
    Article("10", "Exporting your data", "Data Export Guide", 1706277600000L),
    Article("11", "Deleting your account", "Account Deletion", 1706364000000L),
    Article("12", "App shortcuts you should know", "App Shortcuts", 1706450400000L),
    Article("13", "Customizing your dashboard", "Dashboard Customization", 1706536800000L),
    Article("14", "Frequently asked questions", "FAQ", 1706623200000L),
    Article("15", "Contacting support", "Support Contact", 1706709600000L)
)



val FAKE_ARTICLE_DETAILS = listOf(
    ArticleDetails(
        id = "1",
        title = "Account Creation Guide",
        summary = "How to create an account",
        updatedAt = 1705500000000L,
        content = """
            # Account Creation Guide

            Creating an account is simple:

            1. Open the app
            2. Tap **Sign Up**
            3. Enter your email and password
            4. Confirm your email
            5. Start using the app!

            **Tip:** Use a strong password for security.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "2",
        title = "Password Reset",
        summary = "Reset your password easily",
        updatedAt = 1705586400000L,
        content = """
            # Password Reset

            Forgot your password? Follow these steps:

            1. Tap **Forgot Password**
            2. Enter your registered email
            3. Click the link in the email
            4. Set a new password

            **Markdown Example:** `val password = "newPassword123"`
        """.trimIndent()
    ),
    ArticleDetails(
        id = "3",
        title = "Profile Update",
        summary = "Update your profile info",
        updatedAt = 1705672800000L,
        content = """
            # Profile Update

            Keep your profile up-to-date:

            - Go to **Settings > Profile**
            - Edit your details
            - Tap **Save**

            **Tip:** Add a profile picture to personalize your account.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "4",
        title = "Social Account Linking",
        summary = "Connecting your social accounts",
        updatedAt = 1705759200000L,
        content = """
            # Social Account Linking

            Link your social accounts for easy login:

            ```kotlin
            val linked = linkAccount("Google")
            ```

            Supported accounts: Google, Facebook, Twitter.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "5",
        title = "Notifications Settings",
        summary = "Understanding notifications",
        updatedAt = 1705845600000L,
        content = """
            # Notifications Settings

            Manage notifications:

            - Go to **Settings > Notifications**
            - Toggle on/off types of alerts
            - Set **Do Not Disturb** hours

            **Markdown Tip:** Use `**bold**` for important notifications.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "6",
        title = "Subscription Management",
        summary = "Managing subscriptions",
        updatedAt = 1705932000000L,
        content = """
            # Subscription Management

            - View active subscriptions
            - Upgrade or downgrade plan
            - Cancel subscription anytime

            **Note:** Refunds are handled according to policy.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "7",
        title = "Dark Mode Feature",
        summary = "Using dark mode",
        updatedAt = 1706018400000L,
        content = """
            # Dark Mode Feature

            Enable dark mode:

            1. Go to **Settings > Theme**
            2. Choose **Dark Mode**

            ```kotlin
            val theme = Theme.DARK
            applyTheme(theme)
            ```
        """.trimIndent()
    ),
    ArticleDetails(
        id = "8",
        title = "Privacy & Security",
        summary = "Data privacy tips",
        updatedAt = 1706104800000L,
        content = """
            # Privacy & Security

            Tips to stay secure:

            - Use strong passwords
            - Enable 2FA
            - Review app permissions

            **Markdown List Example:**
            - Item 1
            - Item 2
            - Item 3
        """.trimIndent()
    ),
    ArticleDetails(
        id = "9",
        title = "Login Troubleshooting",
        summary = "Troubleshooting login issues",
        updatedAt = 1706191200000L,
        content = """
            # Login Troubleshooting

            Common issues:

            1. Incorrect password
            2. Account not verified
            3. App outdated

            **Tip:** Restart the app before troubleshooting.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "10",
        title = "Data Export Guide",
        summary = "Exporting your data",
        updatedAt = 1706277600000L,
        content = """
            # Data Export Guide

            Export your data:

            - Go to **Settings > Data**
            - Tap **Export**
            - Choose format (CSV, JSON)

            **Example JSON:**
            ```json
            {
              "id": "123",
              "name": "John Doe"
            }
            ```
        """.trimIndent()
    ),
    ArticleDetails(
        id = "11",
        title = "Account Deletion",
        summary = "Deleting your account",
        updatedAt = 1706364000000L,
        content = """
            # Account Deletion

            To delete your account:

            1. Go to **Settings > Account**
            2. Tap **Delete Account**
            3. Confirm deletion

            **Warning:** This action is irreversible.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "12",
        title = "App Shortcuts",
        summary = "App shortcuts you should know",
        updatedAt = 1706450400000L,
        content = """
            # App Shortcuts

            Useful shortcuts:

            - **Double-tap** to like
            - **Swipe left** to delete
            - **Long press** for options

            **Tip:** Memorize shortcuts for faster navigation.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "13",
        title = "Dashboard Customization",
        summary = "Customizing your dashboard",
        updatedAt = 1706536800000L,
        content = """
            # Dashboard Customization

            Customize your dashboard:

            - Add widgets
            - Rearrange sections
            - Change theme

            ```kotlin
            val dashboard = Dashboard()
            dashboard.addWidget("Stats")
            ```
        """.trimIndent()
    ),
    ArticleDetails(
        id = "14",
        title = "FAQ",
        summary = "Frequently asked questions",
        updatedAt = 1706623200000L,
        content = """
            # FAQ

            **Q:** How to reset password?  
            **A:** Use the Forgot Password option.

            **Q:** How to contact support?  
            **A:** Go to Settings > Support.
        """.trimIndent()
    ),
    ArticleDetails(
        id = "15",
        title = "Support Contact",
        summary = "Contacting support",
        updatedAt = 1706709600000L,
        content = """
            # Support Contact

            Reach out to support:

            - Email: **support@example.com**
            - Phone: **+1234567890**
            - Chat: **In-app chat**

            **Tip:** Provide your user ID for faster assistance.
        """.trimIndent()
    )
)