# Template Setup Checklist

Use this after creating a new repository from this template.

## 1. Rename project

- Update `rootProject.name` in `settings.gradle`
- Update `group` in `build.gradle` (recommended: your reverse domain, e.g. `com.acme`)

## 2. Rename Java package

- Move `src/main/java/com/webapp` to your package path
- Move `src/test/java/com/webapp` to your package path
- Update `package` declarations and imports
- Update `application.mainClass` in `build.gradle`

## 3. Verify locally

```powershell
.\scripts\bootstrap.ps1
.\gradlew.bat run
```

## 4. Verify container

```powershell
.\scripts\container-up.ps1
```

## 5. First commit

- Replace this file with project-specific onboarding notes, or delete it once setup is complete.
